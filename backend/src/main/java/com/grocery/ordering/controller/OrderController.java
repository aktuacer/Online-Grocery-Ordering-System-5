package com.grocery.ordering.controller;

import com.grocery.ordering.dto.ApiResponse;
import com.grocery.ordering.model.Order;
import com.grocery.ordering.model.Order.OrderStatus;
import com.grocery.ordering.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Order operations
 * Handles order creation, updates, and retrieval
 *
 * @author Chirag Singhal (chirag127)
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Create order endpoint (US004)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(@Valid @RequestBody Order order,
                                                         HttpServletRequest request) {
        try {
            // Check customer authentication
            if (!isCustomerAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Customer authentication required"));
            }

            // Set customer ID from session
            HttpSession session = request.getSession(false);
            String customerId = (String) session.getAttribute("customerId");
            order.setCustomerId(customerId);

            Order createdOrder = orderService.createOrder(order);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", createdOrder));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Order creation failed: " + e.getMessage()));
        }
    }

    /**
     * Get customer order details endpoint (US010)
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<Order>>> getCustomerOrderDetails(@PathVariable String customerId,
                                                                           HttpServletRequest request) {
        try {
            // Check authorization
            if (!isAuthorized(request, customerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied"));
            }

            List<Order> orders = orderService.getCustomerOrderDetails(customerId);
            return ResponseEntity.ok(ApiResponse.success("Customer orders retrieved", orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to retrieve customer orders: " + e.getMessage()));
        }
    }

    /**
     * Get order by ID endpoint
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable Integer orderId,
                                                      HttpServletRequest request) {
        try {
            // Check authentication
            if (!isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }

            Optional<Order> orderOpt = orderService.findById(orderId);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();

                // Check if customer can access this order
                HttpSession session = request.getSession(false);
                String userType = (String) session.getAttribute("userType");
                if ("CUSTOMER".equals(userType)) {
                    String customerId = (String) session.getAttribute("customerId");
                    if (!order.getCustomerId().equals(customerId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(ApiResponse.error("Access denied"));
                    }
                }

                return ResponseEntity.ok(ApiResponse.success("Order found", order));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Order not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve order: " + e.getMessage()));
        }
    }

    /**
     * Get all orders with details endpoint - Admin only
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders(HttpServletRequest request) {
        try {
            // Check admin authentication
            if (!isAdminAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
            }

            List<Order> orders = orderService.getAllOrdersWithDetails();
            return ResponseEntity.ok(ApiResponse.success("All orders retrieved", orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve orders: " + e.getMessage()));
        }
    }

    /**
     * Update order status endpoint - Admin only
     */
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<String>> updateOrderStatus(@PathVariable Integer orderId,
                                                                @RequestBody StatusUpdateRequest request,
                                                                HttpServletRequest httpRequest) {
        try {
            // Check admin authentication
            if (!isAdminAuthenticated(httpRequest)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
            }

            boolean updated = orderService.updateOrderStatus(orderId, request.getStatus());
            if (updated) {
                return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", "Updated"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Order not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Status update failed: " + e.getMessage()));
        }
    }

    /**
     * Cancel order endpoint
     */
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelOrder(@PathVariable Integer orderId,
                                                          HttpServletRequest request) {
        try {
            // Check authentication
            if (!isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }

            // Check if customer can cancel this order
            HttpSession session = request.getSession(false);
            String userType = (String) session.getAttribute("userType");
            if ("CUSTOMER".equals(userType)) {
                Optional<Order> orderOpt = orderService.findById(orderId);
                if (orderOpt.isPresent()) {
                    String customerId = (String) session.getAttribute("customerId");
                    if (!orderOpt.get().getCustomerId().equals(customerId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(ApiResponse.error("Access denied"));
                    }
                }
            }

            boolean cancelled = orderService.cancelOrder(orderId);
            if (cancelled) {
                return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", "Cancelled"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Order not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Order cancellation failed: " + e.getMessage()));
        }
    }

    /**
     * Delete order endpoint - Admin only
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<String>> deleteOrder(@PathVariable Integer orderId,
                                                          HttpServletRequest request) {
        try {
            // Check admin authentication
            if (!isAdminAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
            }

            boolean deleted = orderService.deleteOrder(orderId);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("Order deleted successfully", "Deleted"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Order not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Order deletion failed: " + e.getMessage()));
        }
    }

    /**
     * Get orders by status endpoint - Admin only
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByStatus(@PathVariable OrderStatus status,
                                                                     HttpServletRequest request) {
        try {
            // Check admin authentication
            if (!isAdminAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
            }

            List<Order> orders = orderService.findByStatus(status);
            return ResponseEntity.ok(ApiResponse.success("Orders by status retrieved", orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve orders by status: " + e.getMessage()));
        }
    }

    /**
     * Get orders by date range endpoint - Admin only
     */
    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            HttpServletRequest request) {
        try {
            // Check admin authentication
            if (!isAdminAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
            }

            List<Order> orders = orderService.findByDateRange(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Orders by date range retrieved", orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to retrieve orders by date range: " + e.getMessage()));
        }
    }

    /**
     * Get order statistics endpoint - Admin only
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<OrderService.OrderStatistics>> getOrderStatistics(HttpServletRequest request) {
        try {
            // Check admin authentication
            if (!isAdminAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
            }

            OrderService.OrderStatistics statistics = orderService.getOrderStatistics();
            return ResponseEntity.ok(ApiResponse.success("Order statistics retrieved", statistics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve order statistics: " + e.getMessage()));
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
     * Check if user is authenticated as customer
     */
    private boolean isCustomerAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("authenticated"))) {
            return false;
        }

        String userType = (String) session.getAttribute("userType");
        return "CUSTOMER".equals(userType);
    }

    /**
     * Check if user is authorized to access customer data
     */
    private boolean isAuthorized(HttpServletRequest request, String customerId) {
        HttpSession session = request.getSession(false);
        if (session == null || !Boolean.TRUE.equals(session.getAttribute("authenticated"))) {
            return false;
        }

        String userType = (String) session.getAttribute("userType");

        // Admin can access any customer data
        if ("ADMIN".equals(userType)) {
            return true;
        }

        // Customer can only access their own data
        if ("CUSTOMER".equals(userType)) {
            String sessionCustomerId = (String) session.getAttribute("customerId");
            return customerId.equals(sessionCustomerId);
        }

        return false;
    }

    /**
     * DTO for status update requests
     */
    public static class StatusUpdateRequest {
        private OrderStatus status;

        public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus status) { this.status = status; }
    }
}
