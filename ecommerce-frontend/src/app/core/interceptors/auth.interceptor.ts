import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, switchMap, throwError } from 'rxjs';

/**
 * Intercepteur HTTP pour ajouter automatiquement le token JWT aux requêtes
 * et gérer le rafraîchissement automatique du token
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);

    // Ne pas ajouter le token pour les requêtes Keycloak
    if (req.url.includes('/realms/') || req.url.includes('/protocol/openid-connect')) {
        return next(req);
    }

    // Ajouter le token JWT si disponible
    const token = authService.getToken();

    if (token) {
        const clonedReq = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });

        return next(clonedReq).pipe(
            catchError(error => {
                // Si erreur 401, essayer de rafraîchir le token
                if (error.status === 401 && authService.getRefreshToken()) {
                    return authService.refreshToken().pipe(
                        switchMap(() => {
                            // Réessayer la requête avec le nouveau token
                            const newToken = authService.getToken();
                            const retryReq = req.clone({
                                setHeaders: {
                                    Authorization: `Bearer ${newToken}`
                                }
                            });
                            return next(retryReq);
                        }),
                        catchError(refreshError => {
                            // Si le refresh échoue, déconnecter l'utilisateur
                            authService.logout();
                            return throwError(() => refreshError);
                        })
                    );
                }

                return throwError(() => error);
            })
        );
    }

    return next(req);
};
