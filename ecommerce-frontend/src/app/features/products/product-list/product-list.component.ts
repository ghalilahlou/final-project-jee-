import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { Product, ProductPage, ProductFilterOptions } from '../../../core/models/api.models';
import { Subject, debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
    selector: 'app-product-list',
    standalone: true,
    imports: [CommonModule, RouterLink, FormsModule],
    templateUrl: './product-list.component.html',
    styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit, OnDestroy {
    products: Product[] = [];
    filteredProducts: Product[] = [];
    categories: string[] = [];

    // Filtres
    searchTerm = '';
    selectedCategory = '';
    minPrice: number | null = null;
    maxPrice: number | null = null;
    sortBy = 'name';
    sortDir: 'ASC' | 'DESC' = 'ASC';

    // Pagination
    currentPage = 0;
    totalPages = 0;
    totalElements = 0;
    pageSize = 12;

    // UI State
    isLoading = false;
    error: string | null = null;
    backendAvailable = true;

    // Search debounce
    private searchSubject = new Subject<string>();

    // Price range options
    priceRanges = [
        { label: 'Tous les prix', min: null, max: null },
        { label: 'Moins de 500 MAD', min: 0, max: 500 },
        { label: '500 - 1000 MAD', min: 500, max: 1000 },
        { label: '1000 - 5000 MAD', min: 1000, max: 5000 },
        { label: '5000 - 10000 MAD', min: 5000, max: 10000 },
        { label: 'Plus de 10000 MAD', min: 10000, max: null }
    ];

    constructor(
        private productService: ProductService,
        private cartService: CartService
    ) {
        // Setup search debounce
        this.searchSubject.pipe(
            debounceTime(400),
            distinctUntilChanged()
        ).subscribe(searchTerm => {
            this.searchTerm = searchTerm;
            this.currentPage = 0;
            this.loadProducts();
        });
    }

    ngOnInit() {
        this.loadProducts();
        this.loadCategories();
        this.checkBackendHealth();
    }

    ngOnDestroy() {
        this.searchSubject.complete();
    }

    /**
     * Charger les produits depuis le backend
     */
    loadProducts() {
        this.isLoading = true;
        this.error = null;

        const options: ProductFilterOptions = {
            page: this.currentPage,
            size: this.pageSize,
            sortBy: this.sortBy,
            sortDir: this.sortDir,
            keyword: this.searchTerm || undefined,
            category: this.selectedCategory || undefined,
            minPrice: this.minPrice !== null ? this.minPrice : undefined,
            maxPrice: this.maxPrice !== null ? this.maxPrice : undefined
        };

        this.productService.advancedSearch(options).subscribe({
            next: (response: ProductPage) => {
                this.products = response.content;
                this.totalPages = response.totalPages;
                this.totalElements = response.totalElements;
                this.currentPage = response.number;
                this.isLoading = false;
                this.backendAvailable = true;

                console.log('✅ Products loaded:', {
                    count: this.products.length,
                    total: this.totalElements,
                    page: this.currentPage + 1,
                    totalPages: this.totalPages
                });
            },
            error: (error) => {
                console.error('❌ Error loading products:', error);
                this.isLoading = false;
                this.backendAvailable = false;
                this.error = 'Impossible de charger les produits. Veuillez réessayer.';

                // Fallback to mock data
                this.loadMockData();
            }
        });
    }

    /**
     * Charger les catégories
     */
    loadCategories() {
        this.productService.getCategories().subscribe({
            next: (categories) => {
                this.categories = categories;
                console.log('✅ Categories loaded:', this.categories);
            },
            error: (error) => {
                console.error('❌ Error loading categories:', error);
                // Fallback categories
                this.categories = ['Électronique', 'Mode', 'Maison', 'Sports', 'Beauté', 'Livres'];
            }
        });
    }

    /**
     * Vérifier la santé du backend
     */
    checkBackendHealth() {
        this.productService.healthCheck().subscribe({
            next: (response) => {
                console.log('💚 Backend health:', response);
                this.backendAvailable = true;
            },
            error: () => {
                console.warn('⚠️ Backend unavailable, using fallback mode');
                this.backendAvailable = false;
            }
        });
    }

    /**
     * Recherche avec debounce
     */
    onSearchChange(value?: string) {
        this.searchSubject.next(value || this.searchTerm);
    }

    /**
     * Changement de catégorie
     */
    onCategoryChange() {
        this.currentPage = 0;
        this.loadProducts();
    }

    /**
     * Changement de fourchette de prix
     */
    onPriceRangeChange(event: any) {
        const selectedRange = this.priceRanges[event.target.selectedIndex];
        this.minPrice = selectedRange.min;
        this.maxPrice = selectedRange.max;
        this.currentPage = 0;
        this.loadProducts();
    }

    /**
     * Changement de tri
     */
    onSortChange() {
        this.loadProducts();
    }

    /**
     * Changer la direction du tri
     */
    toggleSortDirection() {
        this.sortDir = this.sortDir === 'ASC' ? 'DESC' : 'ASC';
        this.loadProducts();
    }

    /**
     * Ajouter au panier
     */
    addToCart(product: Product, event?: Event) {
        if (event) {
            event.preventDefault();
            event.stopPropagation();
        }

        this.cartService.addItem(product, 1);

        // Show success notification
        this.showNotification(`${product.name} ajouté au panier !`, 'success');
    }

    /**
     * Navigation pagination
     */
    goToPage(page: number) {
        if (page >= 0 && page < this.totalPages) {
            this.currentPage = page;
            this.loadProducts();
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }
    }

    previousPage() {
        this.goToPage(this.currentPage - 1);
    }

    nextPage() {
        this.goToPage(this.currentPage + 1);
    }

    /**
     * Réinitialiser tous les filtres
     */
    resetFilters() {
        this.searchTerm = '';
        this.selectedCategory = '';
        this.minPrice = null;
        this.maxPrice = null;
        this.sortBy = 'name';
        this.sortDir = 'ASC';
        this.currentPage = 0;
        this.loadProducts();
    }

    /**
     * Afficher une notification
     */
    private showNotification(message: string, type: 'success' | 'error' = 'success') {
        // Simple alert for now, can be replaced with a toast service
        alert(message);
    }

    /**
     * Récupérer l'image du produit
     */
    getProductImage(product: Product): string {
        if (product.images && product.images.length > 0) {
            return product.images[0];
        }
        // Fallback emoji based on category
        const categoryEmojis: { [key: string]: string } = {
            'Électronique': '💻',
            'Mode': '👔',
            'Maison': '🏠',
            'Sports': '⚽',
            'Beauté': '💄',
            'Livres': '📚'
        };
        return categoryEmojis[product.category] || '📦';
    }

    /**
     * Formater le prix
     */
    formatPrice(price: number): string {
        return new Intl.NumberFormat('fr-MA', {
            style: 'decimal',
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(price);
    }

    /**
     * Données de fallback si le backend n'est pas disponible
     */
    private loadMockData() {
        // Mock data identique au backend pour cohérence
        this.products = [
            { id: 1, sku: 'ELEC-001', name: 'MacBook Pro 16"', description: 'Ordinateur portable haute performance', price: 25000, stockQuantity: 15, category: 'Électronique' },
            { id: 2, sku: 'ELEC-002', name: 'iPhone 15 Pro', description: 'Smartphone dernière génération', price: 12000, stockQuantity: 30, category: 'Électronique' },
            { id: 3, sku: 'SPORT-001', name: 'Nike Air Max', description: 'Chaussures de sport confortables', price: 1200, stockQuantity: 50, category: 'Sports' },
            { id: 4, sku: 'MODE-001', name: 'Veste en Cuir', description: 'Veste élégante en cuir véritable', price: 2500, stockQuantity: 20, category: 'Mode' },
            { id: 5, sku: 'MAISON-001', name: 'Canapé Design', description: 'Canapé 3 places moderne', price: 8000, stockQuantity: 10, category: 'Maison' },
            { id: 6, sku: 'ELEC-003', name: 'AirPods Pro', description: 'Écouteurs sans fil avec réduction de bruit', price: 2800, stockQuantity: 40, category: 'Électronique' }
        ];
        this.totalElements = 6;
        this.totalPages = 1;
        this.filterProducts();
    }

    /**
     * Filtre les produits localement
     */
    filterProducts() {
        this.filteredProducts = this.products.filter(product => {
            // Filtre par catégorie
            if (this.selectedCategory && product.category !== this.selectedCategory) {
                return false;
            }
            // Filtre par recherche
            if (this.searchTerm) {
                const term = this.searchTerm.toLowerCase();
                return product.name.toLowerCase().includes(term) ||
                    (product.description && product.description.toLowerCase().includes(term));
            }
            return true;
        });
    }
}
