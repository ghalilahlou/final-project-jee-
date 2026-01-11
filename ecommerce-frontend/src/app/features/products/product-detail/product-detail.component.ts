import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-product-detail',
    standalone: true,
    imports: [CommonModule, RouterLink],
    template: `
    <div class="product-detail-page">
      <header class="header">
        <div class="container">
          <a routerLink="/" class="logo">🛒 E-Commerce</a>
          <nav class="nav">
            <a routerLink="/" class="nav-link">Accueil</a>
            <a routerLink="/products" class="nav-link">Produits</a>
            <a routerLink="/cart" class="btn btn-primary btn-sm">Panier</a>
          </nav>
        </div>
      </header>
      <div class="container">
        <div class="breadcrumb">
          <a routerLink="/">Accueil</a> / <a routerLink="/products">Produits</a> / Détails
        </div>
        <div class="product-detail card">
          <h2>Détail du produit</h2>
          <p>Page en cours de développement...</p>
          <a routerLink="/products" class="btn btn-primary">Retour aux produits</a>
       </div>
      </div>
    </div>
  `,
    styles: [`
    .product-detail-page { min-height: 100vh; padding: 40px 0; }
    .header { background: white; box-shadow: var(--shadow-md); padding: 16px 0; margin-bottom: 32px; }
    .header .container { display: flex; justify-content: space-between; align-items: center; }
    .logo { font-size: 24px; font-weight: 800; text-decoration: none; color: var(--dark); }
    .nav { display: flex; gap: 16px; align-items: center; }
    .nav-link { text-decoration: none; color: var(--gray-700); padding: 8px 16px; }
    .breadcrumb { margin-bottom: 24px; color: var(--gray-700); }
    .breadcrumb a { color: var(--primary); text-decoration: none; }
    .product-detail { padding: 48px; text-align: center; }
  `]
})
export class ProductDetailComponent { }
