import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Product, PageResponse } from '../models/api.models';

@Injectable({
    providedIn: 'root'
})
export class ProductService {
    private endpoint = '/api/products';

    constructor(private api: ApiService) { }

    /**
     * Get all products with pagination
     */
    getAll(page: number = 0, size: number = 20, sortBy: string = 'id', sortDir: string = 'ASC'): Observable<PageResponse<Product>> {
        return this.api.get<PageResponse<Product>>(this.endpoint, { page, size, sortBy, sortDir });
    }

    /**
     * Get product by ID
     */
    getById(id: number): Observable<Product> {
        return this.api.get<Product>(`${this.endpoint}/${id}`);
    }

    /**
     * Search products
     */
    search(keyword: string, page: number = 0, size: number = 20): Observable<PageResponse<Product>> {
        return this.api.get<PageResponse<Product>>(`${this.endpoint}/search`, { keyword, page, size });
    }

    /**
     * Get products by category
     */
    getByCategory(category: string, page: number = 0, size: number = 20): Observable<PageResponse<Product>> {
        return this.api.get<PageResponse<Product>>(`${this.endpoint}/category/${category}`, { page, size });
    }

    /**
     * Get products by price range
     */
    getByPriceRange(minPrice: number, maxPrice: number, page: number = 0, size: number = 20): Observable<PageResponse<Product>> {
        return this.api.get<PageResponse<Product>>(`${this.endpoint}/price-range`, { minPrice, maxPrice, page, size });
    }

    /**
     * Get all categories
     */
    getCategories(): Observable<string[]> {
        return this.api.get<string[]>(`${this.endpoint}/categories`);
    }

    /**
     * Create product (ADMIN only)
     */
    create(product: Product): Observable<Product> {
        return this.api.post<Product>(this.endpoint, product);
    }

    /**
     * Update product (ADMIN only)
     */
    update(id: number, product: Product): Observable<Product> {
        return this.api.put<Product>(`${this.endpoint}/${id}`, product);
    }

    /**
     * Delete product (ADMIN only)
     */
    delete(id: number): Observable<void> {
        return this.api.delete<void>(`${this.endpoint}/${id}`);
    }

    /**
     * Update stock (ADMIN only)
     */
    updateStock(id: number, quantityChange: number): Observable<void> {
        return this.api.patch<void>(`${this.endpoint}/${id}/stock`, null, { quantityChange });
    }
}
