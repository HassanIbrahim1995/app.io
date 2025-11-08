package com.hotel.management.repository;

import com.hotel.management.entity.Product;
import com.hotel.management.entity.enums.ProductCategory;
import com.hotel.management.entity.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findBySku(String sku);
    
    List<Product> findByCategory(ProductCategory category);
    
    List<Product> findByStatus(ProductStatus status);
    
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE'")
    List<Product> findAllActive();
    
    @Query("SELECT p FROM Product p WHERE p.isUpsell = true AND p.status = 'ACTIVE' ORDER BY p.upsellPriority DESC")
    List<Product> findUpsellProducts();
    
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.status = 'ACTIVE'")
    List<Product> findActiveByCategory(ProductCategory category);
    
    @Query("SELECT p FROM Product p WHERE p.quantity <= p.minQuantity AND p.status = 'ACTIVE'")
    List<Product> findLowStockProducts();
}
