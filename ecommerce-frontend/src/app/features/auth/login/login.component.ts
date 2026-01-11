import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService, LoginRequest } from '../../../core/services/auth.service';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
})
export class LoginComponent {
    credentials: LoginRequest = {
        username: '',
        password: ''
    };

    loading = false;
    errorMessage = '';

    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    onSubmit() {
        if (!this.credentials.username || !this.credentials.password) {
            this.errorMessage = 'Veuillez remplir tous les champs';
            return;
        }

        this.loading = true;
        this.errorMessage = '';

        this.authService.login(this.credentials).subscribe({
            next: (response) => {
                console.log('Login successful', response);
                this.loading = false;
                // Rediriger vers la page d'accueil ou la page demandée
                this.router.navigate(['/']);
            },
            error: (error) => {
                console.error('Login error', error);
                this.loading = false;
                if (error.status === 401) {
                    this.errorMessage = 'Nom d\'utilisateur ou mot de passe incorrect';
                } else {
                    this.errorMessage = 'Une erreur est survenue. Veuillez réessayer.';
                }
            }
        });
    }

    // Méthode pour remplir rapidement les credentials (développement uniquement)
    quickFill(type: 'admin' | 'customer') {
        if (type === 'admin') {
            this.credentials = { username: 'admin', password: 'admin123' };
        } else {
            this.credentials = { username: 'customer', password: 'customer123' };
        }
    }
}
