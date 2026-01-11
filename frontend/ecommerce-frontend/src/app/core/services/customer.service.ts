import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Customer, CustomerAddress } from '../models/api.models';

@Injectable({
    providedIn: 'root'
})
export class CustomerService {
    private endpoint = '/api/customers';

    constructor(private api: ApiService) { }

    /**
     * Get my profile
     */
    getProfile(): Observable<Customer> {
        return this.api.get<Customer>(`${this.endpoint}/me`);
    }

    /**
     * Update my profile
     */
    updateProfile(customer: Customer): Observable<Customer> {
        return this.api.put<Customer>(`${this.endpoint}/me`, customer);
    }

    /**
     * Get my addresses
     */
    getAddresses(): Observable<CustomerAddress[]> {
        return this.api.get<CustomerAddress[]>(`${this.endpoint}/me/addresses`);
    }

    /**
     * Add address
     */
    addAddress(address: CustomerAddress): Observable<CustomerAddress> {
        return this.api.post<CustomerAddress>(`${this.endpoint}/me/addresses`, address);
    }

    /**
     * Set default address
     */
    setDefaultAddress(addressId: number): Observable<void> {
        return this.api.put<void>(`${this.endpoint}/me/addresses/${addressId}/default`, {});
    }

    /**
     * Delete address
     */
    deleteAddress(addressId: number): Observable<void> {
        return this.api.delete<void>(`${this.endpoint}/me/addresses/${addressId}`);
    }

    /**
     * Search customers (ADMIN)
     */
    search(keyword: string): Observable<Customer[]> {
        return this.api.get<Customer[]>(`${this.endpoint}/search`, { keyword });
    }

    /**
     * Get top customers (ADMIN)
     */
    getTopCustomers(minOrders: number = 5): Observable<Customer[]> {
        return this.api.get<Customer[]>(`${this.endpoint}/top`, { minOrders });
    }

    /**
     * Get customer by ID (ADMIN)
     */
    getById(id: number): Observable<Customer> {
        return this.api.get<Customer>(`${this.endpoint}/${id}`);
    }
}
