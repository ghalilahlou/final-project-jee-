import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Order, CreateOrderRequest, OrderStatus, PageResponse } from '../models/api.models';

@Injectable({
    providedIn: 'root'
})
export class OrderService {
    private apiUrl = `${environment.apiUrl}/api/orders`;

    constructor(private http: HttpClient) { }

    /**
     * Créer une nouvelle commande
     */
    create(orderRequest: CreateOrderRequest): Observable<Order> {
        return this.http.post<Order>(this.apiUrl, orderRequest);
    }

    /**
     * Récupérer toutes les commandes de l'utilisateur connecté
     */
    getMyOrders(page: number = 0, size: number = 10): Observable<PageResponse<Order>> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());

        return this.http.get<PageResponse<Order>>(`${this.apiUrl}/my-orders`, { params });
    }

    /**
     * Récupérer une commande par ID
     */
    getById(id: number): Observable<Order> {
        return this.http.get<Order>(`${this.apiUrl}/${id}`);
    }

    /**
     * Récupérer toutes les commandes (ADMIN uniquement)
     */
    getAll(page: number = 0, size: number = 10): Observable<PageResponse<Order>> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());

        return this.http.get<PageResponse<Order>>(this.apiUrl, { params });
    }

    /**
     * Mettre à jour le statut d'une commande (ADMIN uniquement)
     */
    updateStatus(id: number, status: OrderStatus): Observable<Order> {
        return this.http.put<Order>(`${this.apiUrl}/${id}/status`, { status });
    }

    /**
     * Annuler une commande
     */
    cancel(id: number): Observable<Order> {
        return this.http.put<Order>(`${this.apiUrl}/${id}/cancel`, {});
    }

    /**
     * Suivre une commande par numéro
     */
    trackOrder(orderNumber: string): Observable<Order> {
        return this.http.get<Order>(`${this.apiUrl}/track/${orderNumber}`);
    }
}
