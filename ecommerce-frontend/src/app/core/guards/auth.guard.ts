import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Guard pour protéger les routes qui nécessitent une authentification
 */
export const authGuard: CanActivateFn = (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.isAuthenticated()) {
        return true;
    }

    // Rediriger vers la page de login
    router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
};

/**
 * Guard pour les routes réservées aux administrateurs
 */
export const adminGuard: CanActivateFn = (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.isAuthenticated() && authService.isAdmin()) {
        return true;
    }

    // Rediriger vers une page d'accès refusé ou l'accueil
    router.navigate(['/unauthorized']);
    return false;
};

/**
 * Guard pour les routes réservées aux clients
 */
export const customerGuard: CanActivateFn = (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.isAuthenticated() && (authService.isCustomer() || authService.isAdmin())) {
        return true;
    }

    router.navigate(['/unauthorized']);
    return false;
};
