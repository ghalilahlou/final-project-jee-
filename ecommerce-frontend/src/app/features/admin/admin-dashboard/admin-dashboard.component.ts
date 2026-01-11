import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-admin-dashboard',
    standalone: true,
    imports: [CommonModule, RouterLink],
    templateUrl: './admin-dashboard.component.html',
    styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent {
    stats = [
        { title: 'Produits Totaux', value: '1,234', icon: '📦', color: 'primary' },
        { title: 'Commandes Aujourd\'hui', value: '56', icon: '🛒', color: 'success' },
        { title: 'Utilisateurs Actifs', value: '8,430', icon: '👥', color: 'warning' },
        { title: 'Revenus du Mois', value: '125,000 MAD', icon: '💰', color: 'error' }
    ];

    recentOrders = [
        { id: 'CMD-001', customer: 'Ahmed Benali', amount: 1500, status: 'En cours', date: new Date() },
        { id: 'CMD-002', customer: 'Sara Alaoui', amount: 2300, status: 'Livrée', date: new Date() },
        { id: 'CMD-003', customer: 'Youssef Idrissi', amount: 890, status: 'En cours', date: new Date() },
        { id: 'CMD-004', customer: 'Fatima Zahra', amount: 3200, status: 'En préparation', date: new Date() }
    ];

    topProducts = [
        { name: 'MacBook Pro 16"', sales: 145, revenue: 3625000 },
        { name: 'iPhone 15 Pro', sales: 230, revenue: 2760000 },
        { name: 'AirPods Pro', sales: 340, revenue: 952000 }
    ];
}
