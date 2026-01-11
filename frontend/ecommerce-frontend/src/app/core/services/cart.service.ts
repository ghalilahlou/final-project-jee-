import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { CartItem, Product } from '../models/api.models';

@Injectable({
    providedIn: 'root'
})
export class CartService {
    private cartKey = 'shopping_cart';
    private itemsSubject = new BehaviorSubject<CartItem[]>(this.loadCart());
    public items$ = this.itemsSubject.asObservable();

    constructor() { }

    /**
     * Load cart from localStorage
     */
    private loadCart(): CartItem[] {
        const saved = localStorage.getItem(this.cartKey);
        return saved ? JSON.parse(saved) : [];
    }

    /**
     * Save cart to localStorage
     */
    private saveCart(items: CartItem[]): void {
        localStorage.setItem(this.cartKey, JSON.stringify(items));
        this.itemsSubject.next(items);
    }

    /**
     * Get current cart items
     */
    getItems(): CartItem[] {
        return this.itemsSubject.value;
    }

    /**
     * Add item to cart
     */
    addItem(product: Product, quantity: number = 1): void {
        const items = this.getItems();
        const existingItem = items.find(item => item.product.id === product.id);

        if (existingItem) {
            existingItem.quantity += quantity;
        } else {
            items.push({ product, quantity });
        }

        this.saveCart(items);
    }

    /**
     * Remove item from cart
     */
    removeItem(productId: number): void {
        const items = this.getItems().filter(item => item.product.id !== productId);
        this.saveCart(items);
    }

    /**
     * Update item quantity
     */
    updateQuantity(productId: number, quantity: number): void {
        const items = this.getItems();
        const item = items.find(i => i.product.id === productId);

        if (item) {
            if (quantity <= 0) {
                this.removeItem(productId);
            } else {
                item.quantity = quantity;
                this.saveCart(items);
            }
        }
    }

    /**
     * Clear cart
     */
    clear(): void {
        this.saveCart([]);
    }

    /**
     * Get cart total
     */
    getTotal(): number {
        return this.getItems().reduce((total, item) => {
            return total + (item.product.price * item.quantity);
        }, 0);
    }

    /**
     * Get total items count
     */
    getItemCount(): number {
        return this.getItems().reduce((count, item) => count + item.quantity, 0);
    }
}
