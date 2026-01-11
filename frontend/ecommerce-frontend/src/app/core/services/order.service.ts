import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Order, OrderStatus, PageResponse } from '../models/api.models';

@Injectable({
    providedIn: 'root'
})
export class OrderService {
    private endpoint = '/api/orders';

    constructor(private api: ApiService) { }

    /**
     * Get my orders
     */
    getMyOrders(page: number = 0, size: number = 20): Observable<PageResponse<Order>> {
        return this.api.get<PageResponse<Order>>(`${this.endpoint}/me`, { page, size });
    }

    /**
     * Get order by ID
     */
    getById(id: number): Observable<Order> {
        return this.api.get<Order>(`${this.endpoint}/${id}`);
    }

    /**
     * Get order by number
     */
    getByNumber(orderNumber: string): Observable<Order> {
        return this.api.get<Order>(`${this.endpoint}/number/${orderNumber}`);
    }

    /**
     * Create order
     */
    create(order: Order): Observable<Order> {
        return this.api.post<Order>(this.endpoint, order);
    }

    /**
     * Get orders by status (ADMIN)
     */
    getByStatus(status: OrderStatus, page: number = 0, size: number = 20): Observable<PageResponse<Order>> {
        return this.api.get<PageResponse<Order>>(`${this.endpoint}/status/${status}`, { page, size });
    }

    /**
     * Confirm order (ADMIN)
     */
    confirm(id: number): Observable<Order> {
        return this.api.put<Order>(`${this.endpoint}/${id}/confirm`, {});
    }

    /**
     * Ship order (ADMIN)
     */
    ship(id: number): Observable<Order> {
        return this.api.put<Order>(`${this.endpoint}/${id}/ship`, {});
    }

    /**
     * Deliver order (ADMIN)
     */
    deliver(id: number): Observable<Order> {
        return this.api.put<Order>(`${this.endpoint}/${id}/deliver`, {});
    }

    /**
     * Cancel order (ADMIN)
     */
    cancel(id: number): Observable<Order> {
        return this.api.put<Order>(`${this.endpoint}/${id}/cancel`, {});
    }
}
