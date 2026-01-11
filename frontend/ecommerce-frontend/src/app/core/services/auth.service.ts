import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private currentUserSubject = new BehaviorSubject<any>(null);
    public currentUser$ = this.currentUserSubject.asObservable();

    private tokenKey = 'access_token';
    private userKey = 'current_user';

    constructor() {
        // Load user from localStorage on init
        const savedUser = localStorage.getItem(this.userKey);
        if (savedUser) {
            this.currentUserSubject.next(JSON.parse(savedUser));
        }
    }

    /**
     * Redirect to Keycloak login
     */
    login(): void {
        const keycloakLoginUrl = `${environment.keycloak.url}/realms/${environment.keycloak.realm}/protocol/openid-connect/auth`;
        const redirectUri = encodeURIComponent(window.location.origin + '/auth/callback');
        const clientId = environment.keycloak.clientId;

        const loginUrl = `${keycloakLoginUrl}?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=code&scope=openid`;

        window.location.href = loginUrl;
    }

    /**
     * Handle Keycloak callback with authorization code
     */
    handleCallback(code: string): Observable<any> {
        // In a real app, exchange code for token via backend
        // For now, simulate token storage
        const mockToken = 'mock_jwt_token_' + code;
        this.setToken(mockToken);

        const mockUser = {
            id: '123',
            email: 'user@example.com',
            roles: ['CUSTOMER']
        };

        this.setUser(mockUser);
        return new Observable(observer => {
            observer.next(mockUser);
            observer.complete();
        });
    }

    /**
     * Logout
     */
    logout(): void {
        localStorage.removeItem(this.tokenKey);
        localStorage.removeItem(this.userKey);
        this.currentUserSubject.next(null);

        // Redirect to Keycloak logout
        const keycloakLogoutUrl = `${environment.keycloak.url}/realms/${environment.keycloak.realm}/protocol/openid-connect/logout`;
        const redirectUri = encodeURIComponent(window.location.origin);
        window.location.href = `${keycloakLogoutUrl}?redirect_uri=${redirectUri}`;
    }

    /**
     * Check if user is authenticated
     */
    isAuthenticated(): boolean {
        return !!this.getToken();
    }

    /**
     * Get stored token
     */
    getToken(): string | null {
        return localStorage.getItem(this.tokenKey);
    }

    /**
     * Set token
     */
    setToken(token: string): void {
        localStorage.setItem(this.tokenKey, token);
    }

    /**
     * Get current user
     */
    getCurrentUser(): any {
        return this.currentUserSubject.value;
    }

    /**
     * Set user
     */
    setUser(user: any): void {
        localStorage.setItem(this.userKey, JSON.stringify(user));
        this.currentUserSubject.next(user);
    }

    /**
     * Check if user has specific role
     */
    hasRole(roles: string[]): boolean {
        const user = this.getCurrentUser();
        if (!user || !user.roles) return false;

        return roles.some(role => user.roles.includes(role));
    }

    /**
     * Check if user is admin
     */
    isAdmin(): boolean {
        return this.hasRole(['ADMIN']);
    }

    /**
     * Check if user is customer
     */
    isCustomer(): boolean {
        return this.hasRole(['CUSTOMER']);
    }

    /**
     * Check if user is vendor
     */
    isVendor(): boolean {
        return this.hasRole(['VENDOR']);
    }
}
