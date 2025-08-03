/**
 * Admin Dashboard JavaScript
 * Handles dynamic content loading and user interactions
 * 
 * @author Chirag Singhal (chirag127)
 */

// Global variables
let currentSection = 'dashboard';
let allCustomers = [];
let allProducts = [];
let allOrders = [];

// Initialize dashboard when page loads
document.addEventListener('DOMContentLoaded', function() {
    updateCurrentTime();
    setInterval(updateCurrentTime, 1000);
    loadDashboardData();
});

/**
 * Update current time display
 */
function updateCurrentTime() {
    const now = new Date();
    const timeString = now.toLocaleString();
    const timeElement = document.getElementById('currentTime');
    if (timeElement) {
        timeElement.textContent = timeString;
    }
}

/**
 * Show specific section and hide others
 */
function showSection(sectionName) {
    // Hide all sections
    const sections = ['dashboard', 'customers', 'products', 'orders', 'reports'];
    sections.forEach(section => {
        const element = document.getElementById(section + '-section');
        if (element) {
            element.style.display = 'none';
        }
    });

    // Show selected section
    const selectedSection = document.getElementById(sectionName + '-section');
    if (selectedSection) {
        selectedSection.style.display = 'block';
    }

    // Update navigation
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
    });
    event.target.classList.add('active');

    currentSection = sectionName;

    // Load section-specific data
    switch (sectionName) {
        case 'dashboard':
            loadDashboardData();
            break;
        case 'customers':
            loadCustomers();
            break;
        case 'products':
            loadProducts();
            break;
        case 'orders':
            loadOrders();
            break;
        case 'reports':
            loadReports();
            break;
    }
}

/**
 * Load dashboard statistics and data
 */
async function loadDashboardData() {
    try {
        // Load order statistics
        const statsResponse = await fetch('/api/orders/statistics', {
            credentials: 'include'
        });
        
        if (statsResponse.ok) {
            const statsData = await statsResponse.json();
            displayStatistics(statsData.data);
        }

        // Load recent orders
        const ordersResponse = await fetch('/api/orders', {
            credentials: 'include'
        });
        
        if (ordersResponse.ok) {
            const ordersData = await ordersResponse.json();
            displayRecentOrders(ordersData.data.slice(0, 5)); // Show only 5 recent orders
        }

        // Load low stock products
        const productsResponse = await fetch('/api/products', {
            credentials: 'include'
        });
        
        if (productsResponse.ok) {
            const productsData = await productsResponse.json();
            const lowStockProducts = productsData.data.filter(product => product.availableQuantity <= 10);
            displayLowStockProducts(lowStockProducts);
        }

    } catch (error) {
        console.error('Error loading dashboard data:', error);
        showAlert('Error loading dashboard data', 'danger');
    }
}

/**
 * Display statistics cards
 */
function displayStatistics(stats) {
    const statsContainer = document.getElementById('statsContainer');
    if (!statsContainer) return;

    statsContainer.innerHTML = `
        <div class="col-md-3 mb-4">
            <div class="stats-card customers">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h3 class="mb-1">${stats.totalOrders || 0}</h3>
                        <p class="text-muted mb-0">Total Orders</p>
                    </div>
                    <i class="fas fa-shopping-bag fa-2x text-muted"></i>
                </div>
            </div>
        </div>
        <div class="col-md-3 mb-4">
            <div class="stats-card products">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h3 class="mb-1">${stats.pendingOrders || 0}</h3>
                        <p class="text-muted mb-0">Pending Orders</p>
                    </div>
                    <i class="fas fa-clock fa-2x text-muted"></i>
                </div>
            </div>
        </div>
        <div class="col-md-3 mb-4">
            <div class="stats-card orders">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h3 class="mb-1">${stats.deliveredOrders || 0}</h3>
                        <p class="text-muted mb-0">Delivered Orders</p>
                    </div>
                    <i class="fas fa-check-circle fa-2x text-muted"></i>
                </div>
            </div>
        </div>
        <div class="col-md-3 mb-4">
            <div class="stats-card revenue">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h3 class="mb-1">$${stats.totalRevenue || 0}</h3>
                        <p class="text-muted mb-0">Total Revenue</p>
                    </div>
                    <i class="fas fa-dollar-sign fa-2x text-muted"></i>
                </div>
            </div>
        </div>
    `;
}

/**
 * Display recent orders
 */
function displayRecentOrders(orders) {
    const container = document.getElementById('recentOrders');
    if (!container) return;

    if (orders.length === 0) {
        container.innerHTML = '<p class="text-muted">No recent orders found.</p>';
        return;
    }

    const ordersHtml = orders.map(order => `
        <div class="d-flex justify-content-between align-items-center py-2 border-bottom">
            <div>
                <strong>Order #${order.id}</strong><br>
                <small class="text-muted">Customer: ${order.customerId}</small>
            </div>
            <div class="text-end">
                <span class="badge bg-${getStatusColor(order.status)}">${order.status}</span><br>
                <small class="text-muted">$${order.orderAmount}</small>
            </div>
        </div>
    `).join('');

    container.innerHTML = ordersHtml;
}

/**
 * Display low stock products
 */
function displayLowStockProducts(products) {
    const container = document.getElementById('lowStockProducts');
    if (!container) return;

    if (products.length === 0) {
        container.innerHTML = '<p class="text-muted">All products are well stocked.</p>';
        return;
    }

    const productsHtml = products.map(product => `
        <div class="d-flex justify-content-between align-items-center py-2 border-bottom">
            <div>
                <strong>${product.productName}</strong><br>
                <small class="text-muted">ID: ${product.id}</small>
            </div>
            <div class="text-end">
                <span class="badge bg-warning">${product.availableQuantity} left</span><br>
                <small class="text-muted">$${product.price}</small>
            </div>
        </div>
    `).join('');

    container.innerHTML = productsHtml;
}

/**
 * Load customers data
 */
async function loadCustomers() {
    try {
        const response = await fetch('/api/customers', {
            credentials: 'include'
        });
        
        if (response.ok) {
            const data = await response.json();
            allCustomers = data.data;
            displayCustomers(allCustomers);
        } else {
            showAlert('Failed to load customers', 'danger');
        }
    } catch (error) {
        console.error('Error loading customers:', error);
        showAlert('Error loading customers', 'danger');
    }
}

/**
 * Display customers table
 */
function displayCustomers(customers) {
    const container = document.getElementById('customersTable');
    if (!container) return;

    if (customers.length === 0) {
        container.innerHTML = '<p class="text-muted">No customers found.</p>';
        return;
    }

    const tableHtml = `
        <div class="table-responsive">
            <table class="table table-striped table-hover">
                <thead>
                    <tr>
                        <th>Customer ID</th>
                        <th>Full Name</th>
                        <th>Email</th>
                        <th>Contact Number</th>
                        <th>Address</th>
                        <th>Created At</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    ${customers.map(customer => `
                        <tr>
                            <td>${customer.customerId}</td>
                            <td>${customer.fullName}</td>
                            <td>${customer.email}</td>
                            <td>${customer.contactNumber}</td>
                            <td>${customer.address}</td>
                            <td>${new Date(customer.createdAt).toLocaleDateString()}</td>
                            <td>
                                <button class="btn btn-sm btn-outline-danger" onclick="deleteCustomer('${customer.customerId}')">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        </div>
    `;

    container.innerHTML = tableHtml;
}

/**
 * Search customers by name
 */
async function searchCustomers() {
    const searchTerm = document.getElementById('customerSearch').value.trim();
    
    if (!searchTerm) {
        displayCustomers(allCustomers);
        return;
    }

    try {
        const response = await fetch(`/api/customers/search?name=${encodeURIComponent(searchTerm)}`, {
            credentials: 'include'
        });
        
        if (response.ok) {
            const data = await response.json();
            displayCustomers(data.data);
        } else {
            const errorData = await response.json();
            showAlert(errorData.message || 'Search failed', 'warning');
        }
    } catch (error) {
        console.error('Error searching customers:', error);
        showAlert('Error searching customers', 'danger');
    }
}

/**
 * Refresh customers data
 */
function refreshCustomers() {
    document.getElementById('customerSearch').value = '';
    loadCustomers();
}

/**
 * Get status color for badges
 */
function getStatusColor(status) {
    switch (status) {
        case 'PENDING': return 'warning';
        case 'CONFIRMED': return 'info';
        case 'SHIPPED': return 'primary';
        case 'DELIVERED': return 'success';
        case 'CANCELLED': return 'danger';
        default: return 'secondary';
    }
}

/**
 * Show alert message
 */
function showAlert(message, type = 'info') {
    const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    // Insert alert at the top of the main content
    const mainContent = document.querySelector('.main-content');
    if (mainContent) {
        mainContent.insertAdjacentHTML('afterbegin', alertHtml);
        
        // Auto-dismiss after 5 seconds
        setTimeout(() => {
            const alert = mainContent.querySelector('.alert');
            if (alert) {
                alert.remove();
            }
        }, 5000);
    }
}

// Placeholder functions for other operations
function loadProducts() {
    console.log('Loading products...');
    // Implementation will be added
}

function loadOrders() {
    console.log('Loading orders...');
    // Implementation will be added
}

function loadReports() {
    console.log('Loading reports...');
    // Implementation will be added
}

function searchProducts() {
    console.log('Searching products...');
    // Implementation will be added
}

function refreshProducts() {
    console.log('Refreshing products...');
    // Implementation will be added
}

function refreshOrders() {
    console.log('Refreshing orders...');
    // Implementation will be added
}

function filterOrdersByStatus() {
    console.log('Filtering orders by status...');
    // Implementation will be added
}

function showAddProductModal() {
    console.log('Showing add product modal...');
    // Implementation will be added
}

function deleteCustomer(customerId) {
    if (confirm('Are you sure you want to delete this customer?')) {
        console.log('Deleting customer:', customerId);
        // Implementation will be added
    }
}
