package com.hotel.management.service.impl;

import com.hotel.management.entity.Product;
import com.hotel.management.entity.enums.ProductCategory;
import com.hotel.management.entity.enums.ProductStatus;
import com.hotel.management.exception.BadRequestException;
import com.hotel.management.exception.ResourceNotFoundException;
import com.hotel.management.repository.ProductRepository;
import com.hotel.management.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Product createProduct(Product product) {
        if (productRepository.findBySku(product.getSku()).isPresent()) {
            throw new BadRequestException("Product with SKU " + product.getSku() + " already exists");
        }
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product product) {
        Product existing = getProductById(id);
        
        if (!existing.getSku().equals(product.getSku()) && 
            productRepository.findBySku(product.getSku()).isPresent()) {
            throw new BadRequestException("Product with SKU " + product.getSku() + " already exists");
        }

        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setCategory(product.getCategory());
        existing.setPrice(product.getPrice());
        existing.setCostPrice(product.getCostPrice());
        existing.setStatus(product.getStatus());
        existing.setIsTaxable(product.getIsTaxable());
        existing.setTaxRate(product.getTaxRate());
        existing.setImageUrl(product.getImageUrl());
        existing.setIsUpsell(product.getIsUpsell());
        existing.setUpsellPriority(product.getUpsellPriority());
        existing.setUpsellDescription(product.getUpsellDescription());
        existing.setNotes(product.getNotes());

        return productRepository.save(existing);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    @Override
    public Product getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "sku", sku));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getActiveProducts() {
        return productRepository.findAllActive();
    }

    @Override
    public List<Product> getProductsByCategory(ProductCategory category) {
        return productRepository.findActiveByCategory(category);
    }

    @Override
    public List<Product> getUpsellProducts() {
        return productRepository.findUpsellProducts();
    }

    @Override
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", "id", id);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Product updateStock(Long id, Integer quantity) {
        Product product = getProductById(id);
        
        int newQuantity = product.getQuantity() + quantity;
        if (newQuantity < 0) {
            throw new BadRequestException("Insufficient stock. Available: " + product.getQuantity());
        }

        product.setQuantity(newQuantity);
        
        // Update status based on stock level
        if (newQuantity == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        } else if (product.getStatus() == ProductStatus.OUT_OF_STOCK) {
            product.setStatus(ProductStatus.ACTIVE);
        }

        return productRepository.save(product);
    }
}
