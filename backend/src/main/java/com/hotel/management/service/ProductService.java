package com.hotel.management.service;

import com.hotel.management.entity.Product;
import com.hotel.management.entity.enums.ProductCategory;

import java.util.List;

public interface ProductService {
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    Product getProductById(Long id);
    Product getProductBySku(String sku);
    List<Product> getAllProducts();
    List<Product> getActiveProducts();
    List<Product> getProductsByCategory(ProductCategory category);
    List<Product> getUpsellProducts();
    List<Product> getLowStockProducts();
    void deleteProduct(Long id);
    Product updateStock(Long id, Integer quantity);
}
