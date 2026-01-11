package com.ghali.ecommerce.product.repository;

import com.ghali.ecommerce.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findByCategory(String category);

    Page<Product> findByActiveTrue(Pageable pageable);

    List<Product> findByActiveTrueAndStockQuantityGreaterThan(int threshold);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.active = true")
    Page<Product> findByCategoryAndActive(@Param("category") String category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.active = true")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                    @Param("maxPrice") BigDecimal maxPrice, 
                                    Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.rating >= :minRating AND p.active = true")
    List<Product> findByMinRating(@Param("minRating") Double minRating);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.active = true")
    List<String> findAllCategories();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category AND p.active = true")
    long countByCategory(@Param("category") String category);
}
