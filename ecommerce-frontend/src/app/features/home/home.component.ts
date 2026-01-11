import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-home',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent {
    features = [
        {
            icon: '🛍️',
            title: 'Large Catalogue',
            description: 'Des milliers de produits de qualité à découvrir'
        },
        {
            icon: '🚚',
            title: 'Livraison Rapide',
            description: 'Livraison gratuite pour toute commande supérieure à 500 MAD'
        },
        {
            icon: '💳',
            title: 'Paiement Sécurisé',
            description: 'Vos transactions sont cryptées et sécurisées'
        },
        {
            icon: '🤖',
            title: 'Assistant IA',
            description: 'Un chatbot intelligent pour vous aider 24/7'
        }
    ];

    categories = [
        { name: 'Électronique', image: '💻', link: '/products?category=electronique' },
        { name: 'Mode', image: '👔', link: '/products?category=mode' },
        { name: 'Maison', image: '🏠', link: '/products?category=maison' },
        { name: 'Sports', image: '⚽', link: '/products?category=sports' }
    ];
}
