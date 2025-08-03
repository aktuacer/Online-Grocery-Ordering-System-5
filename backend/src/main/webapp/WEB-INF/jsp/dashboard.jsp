<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Online Grocery Ordering System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .sidebar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            color: white;
        }
        .sidebar .nav-link {
            color: rgba(255, 255, 255, 0.8);
            padding: 15px 20px;
            border-radius: 8px;
            margin: 5px 10px;
            transition: all 0.3s ease;
        }
        .sidebar .nav-link:hover,
        .sidebar .nav-link.active {
            background-color: rgba(255, 255, 255, 0.2);
            color: white;
            transform: translateX(5px);
        }
        .main-content {
            padding: 20px;
        }
        .stats-card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
            border-left: 4px solid;
            transition: transform 0.3s ease;
        }
        .stats-card:hover {
            transform: translateY(-5px);
        }
        .stats-card.customers { border-left-color: #28a745; }
        .stats-card.products { border-left-color: #007bff; }
        .stats-card.orders { border-left-color: #ffc107; }
        .stats-card.revenue { border-left-color: #dc3545; }
        .navbar-brand {
            font-weight: bold;
            color: #667eea !important;
        }
        .content-section {
            background: white;
            border-radius: 15px;
            padding: 25px;
            margin-bottom: 20px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
        }
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
        }
        .table th {
            background-color: #f8f9fa;
            border-top: none;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2 sidebar">
                <div class="p-3">
                    <h4 class="text-center mb-4">
                        <i class="fas fa-shopping-cart me-2"></i>
                        Admin Panel
                    </h4>
                    
                    <nav class="nav flex-column">
                        <a class="nav-link active" href="#dashboard" onclick="showSection('dashboard')">
                            <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                        </a>
                        <a class="nav-link" href="#customers" onclick="showSection('customers')">
                            <i class="fas fa-users me-2"></i>Customers
                        </a>
                        <a class="nav-link" href="#products" onclick="showSection('products')">
                            <i class="fas fa-box me-2"></i>Products
                        </a>
                        <a class="nav-link" href="#orders" onclick="showSection('orders')">
                            <i class="fas fa-shopping-bag me-2"></i>Orders
                        </a>
                        <a class="nav-link" href="#reports" onclick="showSection('reports')">
                            <i class="fas fa-chart-bar me-2"></i>Reports
                        </a>
                        <hr class="my-3">
                        <a class="nav-link" href="/logout">
                            <i class="fas fa-sign-out-alt me-2"></i>Logout
                        </a>
                    </nav>
                </div>
            </div>
            
            <!-- Main Content -->
            <div class="col-md-9 col-lg-10 main-content">
                <!-- Header -->
                <nav class="navbar navbar-expand-lg navbar-light bg-white rounded mb-4 shadow-sm">
                    <div class="container-fluid">
                        <span class="navbar-brand">
                            <i class="fas fa-user-shield me-2"></i>
                            Welcome, ${sessionScope.adminUser.username}
                        </span>
                        <div class="navbar-nav ms-auto">
                            <span class="nav-item nav-link">
                                <i class="fas fa-clock me-1"></i>
                                <span id="currentTime"></span>
                            </span>
                        </div>
                    </div>
                </nav>
                
                <!-- Dashboard Section -->
                <div id="dashboard-section" class="content-section">
                    <h2 class="mb-4">
                        <i class="fas fa-tachometer-alt me-2"></i>Dashboard Overview
                    </h2>
                    
                    <div class="row" id="statsContainer">
                        <!-- Stats cards will be loaded here -->
                    </div>
                    
                    <div class="row mt-4">
                        <div class="col-md-6">
                            <div class="content-section">
                                <h5><i class="fas fa-clock me-2"></i>Recent Orders</h5>
                                <div id="recentOrders">
                                    <!-- Recent orders will be loaded here -->
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="content-section">
                                <h5><i class="fas fa-exclamation-triangle me-2"></i>Low Stock Products</h5>
                                <div id="lowStockProducts">
                                    <!-- Low stock products will be loaded here -->
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Customers Section -->
                <div id="customers-section" class="content-section" style="display: none;">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h2><i class="fas fa-users me-2"></i>Customer Management</h2>
                        <button class="btn btn-primary" onclick="refreshCustomers()">
                            <i class="fas fa-sync-alt me-2"></i>Refresh
                        </button>
                    </div>
                    
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <div class="input-group">
                                <input type="text" class="form-control" id="customerSearch" 
                                       placeholder="Search customers by name...">
                                <button class="btn btn-outline-secondary" onclick="searchCustomers()">
                                    <i class="fas fa-search"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                    
                    <div id="customersTable">
                        <!-- Customers table will be loaded here -->
                    </div>
                </div>
                
                <!-- Products Section -->
                <div id="products-section" class="content-section" style="display: none;">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h2><i class="fas fa-box me-2"></i>Product Management</h2>
                        <div>
                            <button class="btn btn-success me-2" onclick="showAddProductModal()">
                                <i class="fas fa-plus me-2"></i>Add Product
                            </button>
                            <button class="btn btn-primary" onclick="refreshProducts()">
                                <i class="fas fa-sync-alt me-2"></i>Refresh
                            </button>
                        </div>
                    </div>
                    
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <div class="input-group">
                                <input type="text" class="form-control" id="productSearch" 
                                       placeholder="Search products by name...">
                                <button class="btn btn-outline-secondary" onclick="searchProducts()">
                                    <i class="fas fa-search"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                    
                    <div id="productsTable">
                        <!-- Products table will be loaded here -->
                    </div>
                </div>
                
                <!-- Orders Section -->
                <div id="orders-section" class="content-section" style="display: none;">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h2><i class="fas fa-shopping-bag me-2"></i>Order Management</h2>
                        <button class="btn btn-primary" onclick="refreshOrders()">
                            <i class="fas fa-sync-alt me-2"></i>Refresh
                        </button>
                    </div>
                    
                    <div class="row mb-3">
                        <div class="col-md-4">
                            <select class="form-select" id="orderStatusFilter" onchange="filterOrdersByStatus()">
                                <option value="">All Orders</option>
                                <option value="PENDING">Pending</option>
                                <option value="CONFIRMED">Confirmed</option>
                                <option value="SHIPPED">Shipped</option>
                                <option value="DELIVERED">Delivered</option>
                                <option value="CANCELLED">Cancelled</option>
                            </select>
                        </div>
                    </div>
                    
                    <div id="ordersTable">
                        <!-- Orders table will be loaded here -->
                    </div>
                </div>
                
                <!-- Reports Section -->
                <div id="reports-section" class="content-section" style="display: none;">
                    <h2 class="mb-4">
                        <i class="fas fa-chart-bar me-2"></i>Reports & Analytics
                    </h2>
                    
                    <div id="reportsContent">
                        <!-- Reports content will be loaded here -->
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="/js/admin-dashboard.js"></script>
</body>
</html>
