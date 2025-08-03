package com.grocery.ordering.service;

import com.grocery.ordering.model.Product;
import com.grocery.ordering.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Product operations
 * Implements business logic and validation
 * 
 * @author Chirag Singhal (chirag127)
 */
@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Register a new product with validation (admin only)
     */
    public Product registerProduct(Product product) {
        // Validate product name
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new RuntimeException("Product name is required");
        }

        // Validate price
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Product price must be greater than 0");
        }

        // Validate quantity
        if (product.getQuantity() == null || product.getQuantity() < 0) {
            throw new RuntimeException("Product quantity cannot be negative");
        }

        // Set default reserved quantity
        if (product.getReserved() == null) {
            product.setReserved(0);
        }

        return productRepository.save(product);
    }

    /**
     * Update product with validation (admin only)
     */
    public Product updateProduct(Integer productId, Product updatedProduct) {
        Optional<Product> existingProductOpt = productRepository.findById(productId);
        if (existingProductOpt.isEmpty()) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        Product existingProduct = existingProductOpt.get();

        // Validate and update product name
        if (updatedProduct.getProductName() != null && !updatedProduct.getProductName().trim().isEmpty()) {
            existingProduct.setProductName(updatedProduct.getProductName().trim());
        }

        // Validate and update price
        if (updatedProduct.getPrice() != null) {
            if (updatedProduct.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Product price must be greater than 0");
            }
            existingProduct.setPrice(updatedProduct.getPrice());
        }

        // Validate and update quantity
        if (updatedProduct.getQuantity() != null) {
            if (updatedProduct.getQuantity() < 0) {
                throw new RuntimeException("Product quantity cannot be negative");
            }
            existingProduct.setQuantity(updatedProduct.getQuantity());
        }

        // Update reserved quantity if provided
        if (updatedProduct.getReserved() != null) {
            if (updatedProduct.getReserved() < 0) {
                throw new RuntimeException("Reserved quantity cannot be negative");
            }
            existingProduct.setReserved(updatedProduct.getReserved());
        }

        // Update customer ID if provided
        if (updatedProduct.getCustomerId() != null) {
            existingProduct.setCustomerId(updatedProduct.getCustomerId());
        }

        return productRepository.update(existingProduct);
    }

    /**
     * Find product by ID
     */
    @Transactional(readOnly = true)
    public Optional<Product> findById(Integer productId) {
        return productRepository.findById(productId);
    }

    /**
     * Search products by name
     */
    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Search name cannot be empty");
        }
        
        List<Product> products = productRepository.searchByName(name.trim());
        
        if (products.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        
        return products;
    }

    /**
     * Get all products
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Get available products (quantity > 0)
     */
    @Transactional(readOnly = true)
    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }

    /**
     * Delete product (admin only)
     */
    public boolean deleteProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
        return productRepository.deleteById(productId);
    }

    /**
     * Update product quantity
     */
    public boolean updateProductQuantity(Integer productId, Integer newQuantity) {
        if (newQuantity < 0) {
            throw new RuntimeException("Quantity cannot be negative");
        }
        
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
        
        return productRepository.updateQuantity(productId, newQuantity);
    }

    /**
     * Reserve product quantity for order
     */
    public boolean reserveProductQuantity(Integer productId, Integer quantityToReserve) {
        if (quantityToReserve <= 0) {
            throw new RuntimeException("Quantity to reserve must be greater than 0");
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        Product product = productOpt.get();
        int availableQuantity = product.getAvailableQuantity();
        
        if (availableQuantity < quantityToReserve) {
            throw new RuntimeException("Insufficient quantity available. Available: " + availableQuantity + ", Requested: " + quantityToReserve);
        }

        return productRepository.reserveQuantity(productId, quantityToReserve);
    }

    /**
     * Release reserved quantity
     */
    public boolean releaseReservedQuantity(Integer productId, Integer quantityToRelease) {
        if (quantityToRelease <= 0) {
            throw new RuntimeException("Quantity to release must be greater than 0");
        }

        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        return productRepository.releaseReservedQuantity(productId, quantityToRelease);
    }

    /**
     * Check if product exists
     */
    @Transactional(readOnly = true)
    public boolean productExists(Integer productId) {
        return productRepository.existsById(productId);
    }

    /**
     * Check if product is available for ordering
     */
    @Transactional(readOnly = true)
    public boolean isProductAvailable(Integer productId, Integer requestedQuantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return false;
        }

        Product product = productOpt.get();
        return product.getAvailableQuantity() >= requestedQuantity;
    }

    /**
     * Get product availability information
     */
    @Transactional(readOnly = true)
    public int getAvailableQuantity(Integer productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        return productOpt.get().getAvailableQuantity();
    }
}
