import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-profile',
    standalone: true,
    imports: [CommonModule, RouterLink],
    template: `
    <div class="profile-page">
      <header class="header">
        <div class="container">
          <a routerLink="/" class="logo">🛒 E-Commerce</a>
          <nav class="nav">
            <a routerLink="/" class="nav-link">Accueil</a>
            <a routerLink="/products" class="nav-link">Produits</a>
            <a routerLink="/orders" class="nav-link">Mes Commandes</a>
          </nav>
        </div>
      </header>
      <div class="container">
        <h1 class="page-title">Mon Profil</h1>
        <div class="profile-content card">
          <div class="profile-section">
            <h3>Informations personnelles</h3>
            <p>Page de profil en cours de développement...</p>
          </div>
          <div class="profile-actions">
            <a routerLink="/orders" class="btn btn-primary">Mes commandes</a>
            <a routerLink="/products" class="btn btn-secondary">Continuer mes achats</a>
          </div>
        </div>
      </div>
    </div>
  `,
    styles: [`
    .profile-page { min-height: 100vh; padding: 40px 0; background: var(--gray-50); }
    .header { background: white; box-shadow: var(--shadow-md); padding: 16px 0; margin-bottom: 32px; }
    .header .container { display: flex; justify-content: space-between; align-items: center; }
    .logo { font-size: 24px; font-weight: 800; text-decoration: none; color: var(--dark); }
    .nav { display: flex; gap: 16px; }
    .nav-link { text-decoration: none; color: var(--gray-700); padding: 8px 16px; }
    .page-title { text-align: center; margin-bottom: 32px; font-size: 2.5rem; }
    .profile-content { padding: 48px; }
    .profile-section { margin-bottom: 32px; }
    .profile-actions { display: flex; gap: 16px; justify-content: center; }
  `]
})
export class ProfileComponent { }
