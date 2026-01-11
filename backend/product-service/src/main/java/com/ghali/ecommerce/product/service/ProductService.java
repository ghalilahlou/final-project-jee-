package com.ghali.ecommerce.product.service;

import com.ghali.ecommerce.common.dto.ProductDTO;
import com.ghali.ecommerce.product.kafka.ProductEventProducer;
import com.ghali.ecommerce.product.model.Product;
import com.ghali.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service de gestion des produits
 * Avec cache Redis et publication d'événements Kafka
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductEventProducer productEventProducer;

    @Cacheable(value = "products", key = "#id")
    public ProductDTO getProductById(Long id) {
        log.info("📦 Fetching product by ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        return mapToDTO(product);
    }

    @Cacheable(value = "products", key = "#sku")
    public Product DTOgetProductBySku(String sku) {
        log.info("📦 Fetching product by SKU: {}", sku);
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Product not found with SKU: " + sku));
    }

    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        log.info("📦 Fetching all active products");
        return productRepository.findByActiveTrue(pageable)
                .map(this::mapToDTO);
    }

    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        log.info("🔍 Searching products with keyword: {}", keyword);
        return productRepository.searchByKeyword(keyword, pageable)
                .map(this::mapToDTO);
    }

    public Page<ProductDTO> getProductsByCategory(String category, Pageable pageable) {
        log.info("📦 Fetching products by category: {}", category);
        return productRepository.findByCategoryAndActive(category, pageable)
                .map(this::mapToDTO);
    }

    public Page<ProductDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.info("💰 Fetching products in price range: {} - {}", minPrice, maxPrice);
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable)
                .map(this::mapToDTO);
    }

    public List<String> getAllCategories() {
        log.info("📂 Fetching all categories");
        return productRepository.findAllCategories();
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO createProduct(ProductDTO productDTO) {
        log.info("✨ Creating new product: {}", productDTO.getName());
        
        // Vérifier si le SKU existe déjà
        if (productRepository.findBySku(productDTO.getSku()).isPresent()) {
            throw new RuntimeException("Product with SKU " + productDTO.getSku() + " already exists");
        }

        Product product = mapToEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        
        // Publier événement Kafka
        productEventProducer.publishProductEvent("PRODUCT_CREATED", savedProduct);
        
        log.info("✅ Product created successfully: {}", savedProduct.getSku());
        return mapToDTO(savedProduct);
    }

    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        log.info("🔄 Updating product ID: {}", id);
        
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        
        // Mise à jour des champs
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStockQuantity(productDTO.getStockQuantity());
        existingProduct.setCategory(productDTO.getCategory());
        existingProduct.setImages(productDTO.getImages());
        existingProduct.setTags(productDTO.getTags());
        existingProduct.setActive(productDTO.getActive());
        
        Product updatedProduct = productRepository.save(existingProduct);
        
        // Publier événement Kafka
        productEventProducer.publishProductEvent("PRODUCT_UPDATED", updatedProduct);
        
        log.info("✅ Product updated successfully: {}", updatedProduct.getSku());
        return mapToDTO(updatedProduct);
    }

    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        log.info("🗑️ Deleting product ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        
        // Soft delete - désactiver le produit
        product.setActive(false);
        productRepository.save(product);
        
        // Publier événement Kafka
        productEventProducer.publishProductEvent("PRODUCT_DELETED", product);
        
        log.info("✅ Product deleted (soft delete): {}", product.getSku());
    }

    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public void updateStock(Long productId, int quantityChange) {
        log.info("📊 Updating stock for product ID: {}, change: {}", productId, quantityChange);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        
        if (quantityChange > 0) {
            product.increaseStock(quantityChange);
        } else {
            product.decreaseStock(Math.abs(quantityChange));
        }
        
        productRepository.save(product);
        log.info("✅ Stock updated for product: {}, new stock: {}", product.getSku(), product.getStockQuantity());
    }

    // Mappers
    private ProductDTO mapToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .images(product.getImages())
                .tags(product.getTags())
                .active(product.getActive())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .build();
    }

    private Product mapToEntity(ProductDTO dto) {
        return Product.builder()
                .sku(dto.getSku())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .category(dto.getCategory())
                .images(dto.getImages())
                .tags(dto.getTags())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .rating(dto.getRating())
                .reviewCount(dto.getReviewCount() != null ? dto.getReviewCount() : 0)
                .build();
    }
}
