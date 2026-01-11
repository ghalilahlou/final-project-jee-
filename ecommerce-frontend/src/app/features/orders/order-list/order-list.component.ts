import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-order-list',
    standalone: true,
    imports: [CommonModule, RouterLink],
    template: `
    <div class="orders-page">
      <header class="header">
        <div class="container">
          <a routerLink="/" class="logo">🛒 E-Commerce</a>
          <nav class="nav">
            <a routerLink="/" class="nav-link">Accueil</a>
            <a routerLink="/products" class="nav-link">Produits</a>
            <a routerLink="/profile" class="nav-link">Profil</a>
          </nav>
        </div>
      </header>
      <div class="container">
        <h1 class="page-title">Mes Commandes</h1>
        <div class="empty-orders card">
          <div class="empty-icon">📦</div>
          <h3>Aucune commande</h3>
          <p>Vous n'avez pas encore passé de commande</p>
          <a routerLink="/products" class="btn btn-primary btn-lg">Commencer vos achats</a>
        </div>
      </div>
    </div>
  `,
    styles: [`
    .orders-page { min-height: 100vh; padding: 40px 0; background: var(--gray-50); }
    .header { background: white; box-shadow: var(--shadow-md); padding: 16px 0; margin-bottom: 32px; }
    .header .container { display: flex; justify-content: space-between; align-items: center; }
    .logo { font-size: 24px; font-weight: 800; text-decoration: none; color: var(--dark); }
    .nav { display: flex; gap: 16px; }
    .nav-link { text-decoration: none; color: var(--gray-700); padding: 8px 16px; }
    .page-title { text-align: center; margin-bottom: 32px; font-size: 2.5rem; }
    .empty-orders { text-align: center; padding: 80px 40px; }
    .empty-icon { font-size: 80px; margin-bottom: 24px; }
    .empty-orders h3 { margin-bottom: 12px; font-size: 1.5rem; }
    .empty-orders p { margin-bottom: 32px; color: var(--gray-700); }
  `]
})
export class OrderListComponent { }
