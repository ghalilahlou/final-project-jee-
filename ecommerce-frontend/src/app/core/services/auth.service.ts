import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, catchError, of } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

// Mode développement : mettre à true pour désactiver Keycloak temporairement
const DEV_MODE = true;

export interface LoginRequest {
    username: string;
    password: string;
}

export interface TokenResponse {
    access_token: string;
    refresh_token: string;
    expires_in: number;
    refresh_expires_in: number;
    token_type: string;
}

export interface UserInfo {
    sub: string;
    email: string;
    email_verified: boolean;
    preferred_username: string;
    given_name?: string;
    family_name?: string;
    roles: string[];
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private readonly TOKEN_KEY = 'access_token';
    private readonly REFRESH_TOKEN_KEY = 'refresh_token';
    private readonly USER_KEY = 'user_info';
    private isBrowser: boolean;

    private tokenSubject = new BehaviorSubject<string | null>(null);
    private userSubject = new BehaviorSubject<UserInfo | null>(null);

    public token$ = this.tokenSubject.asObservable();
    public currentUser$ = this.userSubject.asObservable();

    constructor(
        private http: HttpClient,
        private router: Router
    ) {
        // Vérifier si on est dans un navigateur
        this.isBrowser = typeof window !== 'undefined' && typeof localStorage !== 'undefined';

        // Initialiser les subjects seulement dans le navigateur
        if (this.isBrowser) {
            this.tokenSubject.next(this.getToken());
            this.userSubject.next(this.getUserInfo());
            // Vérifier si le token est expiré au démarrage
            this.checkTokenExpiration();
        }
    }

    /**
     * Connexion avec username et password
     */
    login(credentials: LoginRequest): Observable<TokenResponse> {
        // MODE DEV: Authentification simulée sans Keycloak
        if (DEV_MODE) {
            console.warn('🔧 MODE DEV: Authentification sans Keycloak');
            return this.mockLogin(credentials);
        }

        const body = new URLSearchParams();
        body.set('client_id', environment.keycloakClientId);
        body.set('username', credentials.username);
        body.set('password', credentials.password);
        body.set('grant_type', 'password');

        const headers = new HttpHeaders({
            'Content-Type': 'application/x-www-form-urlencoded'
        });

        const tokenUrl = `${environment.keycloakUrl}/realms/${environment.keycloakRealm}/protocol/openid-connect/token`;

        return this.http.post<TokenResponse>(tokenUrl, body.toString(), { headers }).pipe(
            tap(response => {
                this.storeTokens(response);
                this.loadUserInfo();
            }),
            catchError(error => {
                console.error('Login error:', error);
                throw error;
            })
        );
    }

    /**
     * Authentification simulée pour le mode développement
     */
    private mockLogin(credentials: LoginRequest): Observable<TokenResponse> {
        // Simuler credentials prédéfinis
        const validUsers = {
            'admin': { password: 'admin123', role: 'ADMIN', email: 'admin@ecommerce.com', name: 'Admin System' },
            'customer': { password: 'customer123', role: 'CUSTOMER', email: 'customer@example.com', name: 'Ahmed Benali' },
            'sarah': { password: 'sarah123', role: 'CUSTOMER', email: 'sarah@example.com', name: 'Sarah Martin' }
        };

        const user = validUsers[credentials.username as keyof typeof validUsers];

        if (!user || user.password !== credentials.password) {
            return new Observable(observer => {
                setTimeout(() => {
                    observer.error({ status: 401, message: 'Invalid credentials' });
                }, 500);
            });
        }

        // Créer un token mock
        const mockToken = {
            access_token: 'mock_access_token_' + Date.now(),
            refresh_token: 'mock_refresh_token_' + Date.now(),
            expires_in: 3600,
            refresh_expires_in: 7200,
            token_type: 'Bearer'
        };

        return new Observable<TokenResponse>(observer => {
            setTimeout(() => {
                // Stocker le token et les infos utilisateur
                this.storeTokens(mockToken);

                const userInfo: UserInfo = {
                    sub: credentials.username,
                    email: user.email,
                    email_verified: true,
                    preferred_username: credentials.username,
                    given_name: user.name.split(' ')[0],
                    family_name: user.name.split(' ').slice(1).join(' '),
                    roles: [user.role]
                };

                if (this.isBrowser) {
                    localStorage.setItem(this.USER_KEY, JSON.stringify(userInfo));
                }
                this.userSubject.next(userInfo);

                observer.next(mockToken);
                observer.complete();
            }, 500); // Simuler un délai réseau
        });
    }

    /**
     * Déconnexion
     */
    logout(): void {
        const token = this.getToken();

        if (token) {
            // Optionnel: Appeler l'endpoint de logout Keycloak
            const logoutUrl = `${environment.keycloakUrl}/realms/${environment.keycloakRealm}/protocol/openid-connect/logout`;
            const refreshToken = this.getRefreshToken();

            if (refreshToken) {
                const body = new URLSearchParams();
                body.set('client_id', environment.keycloakClientId);
                body.set('refresh_token', refreshToken);

                const headers = new HttpHeaders({
                    'Content-Type': 'application/x-www-form-urlencoded'
                });

                this.http.post(logoutUrl, body.toString(), { headers }).subscribe({
                    complete: () => this.clearSession()
                });
            } else {
                this.clearSession();
            }
        } else {
            this.clearSession();
        }
    }

    /**
     * Rafraîchir le token
     */
    refreshToken(): Observable<TokenResponse> {
        const refreshToken = this.getRefreshToken();

        if (!refreshToken) {
            this.clearSession();
            return of();
        }

        const body = new URLSearchParams();
        body.set('client_id', environment.keycloakClientId);
        body.set('grant_type', 'refresh_token');
        body.set('refresh_token', refreshToken);

        const headers = new HttpHeaders({
            'Content-Type': 'application/x-www-form-urlencoded'
        });

        const tokenUrl = `${environment.keycloakUrl}/realms/${environment.keycloakRealm}/protocol/openid-connect/token`;

        return this.http.post<TokenResponse>(tokenUrl, body.toString(), { headers }).pipe(
            tap(response => {
                this.storeTokens(response);
                this.loadUserInfo();
            }),
            catchError(error => {
                console.error('Refresh token error:', error);
                this.clearSession();
                return of();
            })
        );
    }

    /**
     * Charger les informations utilisateur depuis le token
     */
    private loadUserInfo(): void {
        const token = this.getToken();
        if (!token) return;

        const userInfoUrl = `${environment.keycloakUrl}/realms/${environment.keycloakRealm}/protocol/openid-connect/userinfo`;

        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`
        });

        this.http.get<UserInfo>(userInfoUrl, { headers }).subscribe({
            next: (userInfo) => {
                // Extraire les rôles du token (ils sont dans les claims)
                const decodedToken = this.decodeToken(token);
                userInfo.roles = decodedToken?.realm_access?.roles || [];

                if (this.isBrowser) {
                    localStorage.setItem(this.USER_KEY, JSON.stringify(userInfo));
                }
                this.userSubject.next(userInfo);
            },
            error: (error) => {
                console.error('Error loading user info:', error);
            }
        });
    }

    /**
     * Décoder un JWT token
     */
    private decodeToken(token: string): any {
        try {
            const payload = token.split('.')[1];
            return JSON.parse(atob(payload));
        } catch (e) {
            console.error('Error decoding token:', e);
            return null;
        }
    }

    /**
     * Stocker les tokens
     */
    private storeTokens(response: TokenResponse): void {
        if (!this.isBrowser) return;
        localStorage.setItem(this.TOKEN_KEY, response.access_token);
        localStorage.setItem(this.REFRESH_TOKEN_KEY, response.refresh_token);
        this.tokenSubject.next(response.access_token);
    }

    /**
     * Nettoyer la session
     */
    private clearSession(): void {
        if (!this.isBrowser) return;
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.REFRESH_TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
        this.tokenSubject.next(null);
        this.userSubject.next(null);
        this.router.navigate(['/']);
    }

    /**
     * Obtenir le token stocké
     */
    getToken(): string | null {
        if (!this.isBrowser) return null;
        return localStorage.getItem(this.TOKEN_KEY);
    }

    /**
     * Obtenir le refresh token
     */
    getRefreshToken(): string | null {
        if (!this.isBrowser) return null;
        return localStorage.getItem(this.REFRESH_TOKEN_KEY);
    }

    /**
     * Obtenir les informations utilisateur stockées
     */
    getUserInfo(): UserInfo | null {
        if (!this.isBrowser) return null;
        const stored = localStorage.getItem(this.USER_KEY);
        return stored ? JSON.parse(stored) : null;
    }

    /**
     * Vérifier si l'utilisateur est connecté
     */
    isAuthenticated(): boolean {
        const token = this.getToken();
        if (!token) return false;

        // Vérifier si le token est expiré
        const decoded = this.decodeToken(token);
        if (!decoded) return false;

        const expirationTime = decoded.exp * 1000; // Convertir en millisecondes
        return Date.now() < expirationTime;
    }

    /**
     * Vérifier si l'utilisateur a un rôle spécifique
     */
    hasRole(role: string): boolean {
        const user = this.getUserInfo();
        return user?.roles?.includes(role) || false;
    }

    /**
     * Vérifier si l'utilisateur est admin
     */
    isAdmin(): boolean {
        return this.hasRole('ADMIN');
    }

    /**
     * Vérifier si l'utilisateur est customer
     */
    isCustomer(): boolean {
        return this.hasRole('CUSTOMER');
    }

    /**
     * Vérifier l'expiration du token
     */
    private checkTokenExpiration(): void {
        if (!this.isAuthenticated() && this.getRefreshToken()) {
            // Essayer de rafraîchir le token
            this.refreshToken().subscribe();
        } else if (!this.isAuthenticated()) {
            this.clearSession();
        }
    }
}
