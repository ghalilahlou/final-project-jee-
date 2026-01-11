import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Product, CartItem, Cart } from '../models/api.models';

@Injectable({
    providedIn: 'root'
})
export class CartService {
    private readonly CART_STORAGE_KEY = 'ecommerce_cart';
    private cartSubject = new BehaviorSubject<Cart>(this.loadCart());

    public cart$: Observable<Cart> = this.cartSubject.asObservable();
    public items$: Observable<CartItem[]> = new BehaviorSubject<CartItem[]>(this.getItems());

    constructor() {
        // Synchroniser le panier avec localStorage
        this.cart$.subscribe(cart => {
            localStorage.setItem(this.CART_STORAGE_KEY, JSON.stringify(cart));
        });
    }

    /**
     * Charger le panier depuis localStorage
     */
    private loadCart(): Cart {
        const stored = localStorage.getItem(this.CART_STORAGE_KEY);
        if (stored) {
            try {
                return JSON.parse(stored);
            } catch (e) {
                console.error('Error loading cart from localStorage', e);
            }
        }
        return { items: [], totalAmount: 0, totalItems: 0 };
    }

    /**
     * Obtenir le panier actuel
     */
    getCart(): Cart {
        return this.cartSubject.value;
    }

    /**
     * Obtenir les items du panier
     */
    getItems(): CartItem[] {
        return this.cartSubject.value.items;
    }

    /**
     * Ajouter un produit au panier
     */
    addItem(product: Product, quantity: number = 1): void {
        const cart = this.getCart();
        const existingItem = cart.items.find(item => item.product.id === product.id);

        if (existingItem) {
            existingItem.quantity += quantity;
        } else {
            cart.items.push({ product, quantity });
        }

        this.updateCart(cart);
    }

    /**
     * Mettre à jour la quantité d'un produit
     */
    updateQuantity(productId: number, quantity: number): void {
        const cart = this.getCart();
        const item = cart.items.find(item => item.product.id === productId);

        if (item) {
            if (quantity <= 0) {
                this.removeItem(productId);
            } else {
                item.quantity = quantity;
                this.updateCart(cart);
            }
        }
    }

    /**
     * Retirer un produit du panier
     */
    removeItem(productId: number): void {
        const cart = this.getCart();
        cart.items = cart.items.filter(item => item.product.id !== productId);
        this.updateCart(cart);
    }

    /**
     * Vider le panier
     */
    clearCart(): void {
        const emptyCart: Cart = { items: [], totalAmount: 0, totalItems: 0 };
        this.cartSubject.next(emptyCart);
    }

    /**
     * Calculer et mettre à jour le panier
     */
    private updateCart(cart: Cart): void {
        cart.totalAmount = cart.items.reduce((sum, item) => sum + (item.product.price * item.quantity), 0);
        cart.totalItems = cart.items.reduce((sum, item) => sum + item.quantity, 0);
        this.cartSubject.next(cart);
    }

    /**
     * Obtenir le nombre total d'articles
     */
    getTotalItems(): number {
        return this.getCart().totalItems;
    }

    /**
     * Obtenir le montant total
     */
    getTotalAmount(): number {
        return this.getCart().totalAmount;
    }
}
