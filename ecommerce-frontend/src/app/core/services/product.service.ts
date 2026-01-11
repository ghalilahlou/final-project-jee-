import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Product, ProductPage, ProductFilterOptions, SortDirection } from '../models/api.models';

@Injectable({
    providedIn: 'root'
})
export class ProductService {
    private apiUrl = `${environment.apiUrl}/api/products`;

    constructor(private http: HttpClient) { }

    /**
     * Récupérer tous les produits avec pagination et tri
     */
    getAll(
        page: number = 0,
        size: number = 20,
        sortBy: string = 'id',
        sortDir: SortDirection = 'ASC'
    ): Observable<ProductPage> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString())
            .set('sortBy', sortBy)
            .set('sortDir', sortDir);

        return this.http.get<ProductPage>(this.apiUrl, { params });
    }

    /**
     * Récupérer tous les produits avec options de filtrage avancées
     */
    getAllWithFilters(options: ProductFilterOptions): Observable<ProductPage> {
        let params = new HttpParams()
            .set('page', (options.page || 0).toString())
            .set('size', (options.size || 20).toString())
            .set('sortBy', options.sortBy || 'id')
            .set('sortDir', options.sortDir || 'ASC');

        return this.http.get<ProductPage>(this.apiUrl, { params });
    }

    /**
     * Récupérer un produit par ID
     */
    getById(id: number): Observable<Product> {
        return this.http.get<Product>(`${this.apiUrl}/${id}`);
    }

    /**
     * Rechercher des produits par mot-clé
     */
    search(keyword: string, page: number = 0, size: number = 20): Observable<ProductPage> {
        const params = new HttpParams()
            .set('keyword', keyword)
            .set('page', page.toString())
            .set('size', size.toString());

        return this.http.get<ProductPage>(`${this.apiUrl}/search`, { params });
    }

    /**
     * Filtrer par catégorie
     */
    getByCategory(category: string, page: number = 0, size: number = 20): Observable<ProductPage> {
        const params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());

        return this.http.get<ProductPage>(`${this.apiUrl}/category/${category}`, { params });
    }

    /**
     * Filtrer par fourchette de prix
     */
    getByPriceRange(
        minPrice: number,
        maxPrice: number,
        page: number = 0,
        size: number = 20
    ): Observable<ProductPage> {
        const params = new HttpParams()
            .set('minPrice', minPrice.toString())
            .set('maxPrice', maxPrice.toString())
            .set('page', page.toString())
            .set('size', size.toString());

        return this.http.get<ProductPage>(`${this.apiUrl}/price-range`, { params });
    }

    /**
     * Récupérer toutes les catégories
     */
    getCategories(): Observable<string[]> {
        return this.http.get<string[]>(`${this.apiUrl}/categories`);
    }

    /**
     * Créer un nouveau produit (ADMIN uniquement)
     */
    create(product: Partial<Product>): Observable<Product> {
        return this.http.post<Product>(this.apiUrl, product);
    }

    /**
     * Mettre à jour un produit (ADMIN uniquement)
     */
    update(id: number, product: Partial<Product>): Observable<Product> {
        return this.http.put<Product>(`${this.apiUrl}/${id}`, product);
    }

    /**
     * Supprimer un produit (ADMIN uniquement)
     */
    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

    /**
     * Mettre à jour le stock d'un produit (ADMIN uniquement)
     */
    updateStock(id: number, quantityChange: number): Observable<void> {
        const params = new HttpParams().set('quantityChange', quantityChange.toString());
        return this.http.patch<void>(`${this.apiUrl}/${id}/stock`, null, { params });
    }

    /**
     * Vérifier la santé du service
     */
    healthCheck(): Observable<string> {
        return this.http.get(`${this.apiUrl}/health`, { responseType: 'text' });
    }

    /**
     * Recherche avancée avec tous les filtres
     */
    advancedSearch(options: ProductFilterOptions): Observable<ProductPage> {
        // Si recherche par mot-clé
        if (options.keyword && options.keyword.trim()) {
            return this.search(options.keyword, options.page, options.size);
        }

        // Si filtrage par catégorie
        if (options.category) {
            return this.getByCategory(options.category, options.page, options.size);
        }

        // Si filtrage par prix
        if (options.minPrice !== undefined && options.maxPrice !== undefined) {
            return this.getByPriceRange(options.minPrice, options.maxPrice, options.page, options.size);
        }

        // Sinon, récupération standard
        return this.getAll(options.page, options.size, options.sortBy, options.sortDir);
    }
}
