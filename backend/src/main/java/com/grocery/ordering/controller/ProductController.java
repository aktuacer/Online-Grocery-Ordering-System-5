package com.grocery.ordering.controller;

import com.grocery.ordering.dto.ApiResponse;
import com.grocery.ordering.model.Product;
import com.grocery.ordering.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Product operations
 * Handles product registration, updates, search, and deletion
 * 
 * @author Chirag Singhal (chirag127)
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Register product endpoint (US007) - Admin only
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Product>> registerProduct(@Valid @RequestBody Product product,
                                                               HttpServletRequest request) {
        try {
            // Check admin authentication
            if (!isAdminAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
            }

            Product registeredProduct = productService.registerProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product registered successfully", registeredProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Product registration failed: " + e.getMessage()));
        }
    }

    /**
     * Update product endpoint (US008) - Admin only
     */
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Integer productId,
                                                             @Valid @RequestBody Product product,
                                                             HttpServletRequest request) {
        try {
            // Check admin authentication
            if (!isAdminAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
            }

            Product updatedProduct = productService.updateProduct(productId, product);
            return ResponseEntity.ok(ApiResponse.success("Product updated successfully", updatedProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Product update failed: " + e.getMessage()));
        }
    }

    /**
     * Delete product endpoint (US009) - Admin only
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Integer productId,
                                                            HttpServletRequest request) {
        try {
            // Check admin authentication
            if (!isAdminAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
            }

            boolean deleted = productService.deleteProduct(productId);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", "Deleted"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Product not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Product deletion failed: " + e.getMessage()));
        }
    }

    /**
     * Search products by name endpoint (US006)
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Product>>> searchProducts(@RequestParam String name,
                                                                    HttpServletRequest request) {
        try {
            // Check authentication
            if (!isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }

            List<Product> products = productService.searchProductsByName(name);
            return ResponseEntity.ok(ApiResponse.success("Products found", products));
        } catch (Exception e) {
            if (e.getMessage().contains("Product not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Product search failed: " + e.getMessage()));
        }
    }

    /**
     * Get product by ID endpoint
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable Integer productId,
                                                          HttpServletRequest request) {
        try {
            // Check authentication
            if (!isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }

            Optional<Product> productOpt = productService.findById(productId);
            if (productOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Product found", productOpt.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Product not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve product: " + e.getMessage()));
        }
    }

    /**
     * Get all products endpoint
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts(HttpServletRequest request) {
        try {
            // Check authentication
            if (!isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }

            List<Product> products = productService.getAllProducts();
            return ResponseEntity.ok(ApiResponse.success("All products retrieved", products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve products: " + e.getMessage()));
        }
    }

    /**
     * Get available products endpoint (quantity > 0)
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Product>>> getAvailableProducts(HttpServletRequest request) {
        try {
            // Check authentication
            if (!isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }

            List<Product> products = productService.getAvailableProducts();
            return ResponseEntity.ok(ApiResponse.success("Available products retrieved", products));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve available products: " + e.getMessage()));
        }
    }

    /**
     * Update product quantity endpoint - Admin only
     */
    @PatchMapping("/{productId}/quantity")
    public ResponseEntity<ApiResponse<String>> updateProductQuantity(@PathVariable Integer productId,
                                                                    @RequestBody QuantityUpdateRequest request,
                                                                    HttpServletRequest httpRequest) {
        try {
            // Check admin authentication
            if (!isAdminAuthenticated(httpRequest)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
            }

            boolean updated = productService.updateProductQuantity(productId, request.getQuantity());
            if (updated) {
                return ResponseEntity.ok(ApiResponse.success("Product quantity updated successfully", "Updated"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Product not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Quantity update failed: " + e.getMessage()));
        }
    }

    /**
     * Check product availability endpoint
     */
    @GetMapping("/{productId}/availability")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> checkAvailability(@PathVariable Integer productId,
                                                                               @RequestParam Integer quantity,
                                                                               HttpServletRequest request) {
        try {
            // Check authentication
            if (!isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }

            boolean available = productService.isProductAvailable(productId, quantity);
            int availableQuantity = productService.getAvailableQuantity(productId);
            
            AvailabilityResponse response = new AvailabilityResponse(available, availableQuantity);
            return ResponseEntity.ok(ApiResponse.success("Availability checked", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Availability check failed: " + e.getMessage()));
        }
    }

    /**
     * Check if user is authenticated
     */
    private boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && Boolean.TRUE.equals(session.getAttribute("authenticated"));
    }

    /**
     * Check if user is authenticated as admin
     */
    private boolean isAdminAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("authenticated"))) {
            return false;
        }

        String userType = (String) session.getAttribute("userType");
        return "ADMIN".equals(userType);
    }

    /**
     * DTO for quantity update requests
     */
    public static class QuantityUpdateRequest {
        private Integer quantity;

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    /**
     * DTO for availability response
     */
    public static class AvailabilityResponse {
        private boolean available;
        private int availableQuantity;

        public AvailabilityResponse(boolean available, int availableQuantity) {
            this.available = available;
            this.availableQuantity = availableQuantity;
        }

        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
        public int getAvailableQuantity() { return availableQuantity; }
        public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }
    }
}
