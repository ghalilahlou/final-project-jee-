import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-order-detail',
    standalone: true,
    imports: [CommonModule, RouterLink],
    template: `
    <div class="order-detail-page">
      <header class="header">
        <div class="container">
          <a routerLink="/" class="logo">🛒 E-Commerce</a>
        </div>
      </header>
      <div class="container">
        <h1 class="page-title">Détail de la commande</h1>
        <div class="order-detail card">
          <p>Détails de la commande...</p>
          <a routerLink="/orders" class="btn btn-secondary">Retour aux commandes</a>
        </div>
      </div>
    </div>
  `,
    styles: [`
    .order-detail-page { min-height: 100vh; padding: 40px 0; background: var(--gray-50); }
    .header { background: white; box-shadow: var(--shadow-md); padding: 16px 0; margin-bottom: 32px; }
    .header .container { display: flex; justify-content: space-between; align-items: center; }
    .logo { font-size: 24px; font-weight: 800; text-decoration: none; color: var(--dark); }
    .page-title { text-align: center; margin-bottom: 32px; font-size: 2.5rem; }
    .order-detail { padding: 48px; text-align: center; }
  `]
})
export class OrderDetailComponent { }
