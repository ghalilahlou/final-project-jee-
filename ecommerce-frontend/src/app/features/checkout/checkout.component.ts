import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-checkout',
    standalone: true,
    imports: [CommonModule, RouterLink],
    template: `
    <div class="checkout-page">
      <header class="header">
        <div class="container">
          <a routerLink="/" class="logo">🛒 E-Commerce</a>
        </div>
      </header>
      <div class="container">
        <h1 class="page-title">Finaliser la commande</h1>
        <div class="checkout-content card">
          <p>Page de paiement en cours de développement...</p>
          <a routerLink="/cart" class="btn btn-secondary">Retour au panier</a>
        </div>
      </div>
    </div>
  `,
    styles: [`
    .checkout-page { min-height: 100vh; padding: 40px 0; background: var(--gray-50); }
    .header { background: white; box-shadow: var(--shadow-md); padding: 16px 0; margin-bottom: 32px; }
    .header .container { display: flex; justify-content: space-between; align-items: center; }
    .logo { font-size: 24px; font-weight: 800; text-decoration: none; color: var(--dark); }
    .page-title { text-align: center; margin-bottom: 32px; font-size: 2.5rem; }
    .checkout-content { padding: 48px; text-align: center; }
  `]
})
export class CheckoutComponent { }
