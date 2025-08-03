-- Online Grocery Ordering System Database Schema
-- Author: Chirag Singhal (chirag127)

USE grocery_db;

-- Create admin_users table
CREATE TABLE IF NOT EXISTS admin_users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    customer_id VARCHAR(6) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    contact_number VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create products table
CREATE TABLE IF NOT EXISTS products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    reserved INT DEFAULT 0,
    customer_id VARCHAR(6),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL,
    CHECK (price >= 0),
    CHECK (quantity >= 0),
    CHECK (reserved >= 0)
);

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(6) NOT NULL,
    product_id INT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    order_amount DECIMAL(10,2) NOT NULL,
    quantity_ordered INT NOT NULL DEFAULT 1,
    status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    CHECK (order_amount >= 0),
    CHECK (quantity_ordered > 0)
);

-- Create sessions table for session management
CREATE TABLE IF NOT EXISTS user_sessions (
    session_id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    user_type ENUM('ADMIN', 'CUSTOMER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- Insert default admin user (password: admin123 - will be BCrypt encoded in application)
INSERT INTO admin_users (username, password, email) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxIcnvtcflQjXaC', 'admin@grocery.com')
ON DUPLICATE KEY UPDATE username = username;

-- Insert sample products
INSERT INTO products (product_name, price, quantity) VALUES 
('Apples (1kg)', 150.00, 100),
('Bananas (1kg)', 80.00, 150),
('Rice (5kg)', 400.00, 50),
('Wheat Flour (1kg)', 45.00, 200),
('Milk (1L)', 60.00, 80),
('Bread (500g)', 35.00, 120),
('Eggs (12 pieces)', 90.00, 60),
('Chicken (1kg)', 250.00, 40),
('Tomatoes (1kg)', 40.00, 180),
('Onions (1kg)', 30.00, 200),
('Potatoes (1kg)', 25.00, 250),
('Sugar (1kg)', 50.00, 100),
('Salt (1kg)', 20.00, 300),
('Cooking Oil (1L)', 120.00, 75),
('Tea (250g)', 180.00, 90)
ON DUPLICATE KEY UPDATE product_name = product_name;

-- Insert sample customer for testing
INSERT INTO customers (customer_id, full_name, email, password, address, contact_number) VALUES 
('CUS001', 'John Doe', 'john.doe@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxIcnvtcflQjXaC', '123 Main Street, City, State 12345', '9876543210')
ON DUPLICATE KEY UPDATE customer_id = customer_id;

-- Insert sample orders
INSERT INTO orders (customer_id, product_id, order_amount, quantity_ordered) VALUES 
('CUS001', 1, 300.00, 2),
('CUS001', 3, 400.00, 1),
('CUS001', 5, 120.00, 2)
ON DUPLICATE KEY UPDATE order_id = order_id;

-- Create indexes for better performance
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_products_name ON products(product_name);
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_date ON orders(order_date);
CREATE INDEX idx_sessions_user ON user_sessions(user_id, user_type);
CREATE INDEX idx_sessions_expires ON user_sessions(expires_at);

-- Create views for common queries
CREATE OR REPLACE VIEW customer_order_summary AS
SELECT 
    c.customer_id,
    c.full_name,
    o.order_id,
    o.order_date,
    p.product_id,
    p.product_name,
    o.order_amount,
    o.quantity_ordered,
    o.status
FROM customers c
JOIN orders o ON c.customer_id = o.customer_id
JOIN products p ON o.product_id = p.product_id
ORDER BY o.order_date DESC;

-- Create stored procedures for common operations
DELIMITER //

CREATE PROCEDURE GetCustomerOrders(IN customerId VARCHAR(6))
BEGIN
    SELECT * FROM customer_order_summary WHERE customer_id = customerId;
END //

CREATE PROCEDURE SearchCustomersByName(IN searchName VARCHAR(100))
BEGIN
    SELECT customer_id, full_name, email, address, contact_number, created_at
    FROM customers 
    WHERE LOWER(full_name) LIKE LOWER(CONCAT('%', searchName, '%'));
END //

CREATE PROCEDURE SearchProductsByName(IN searchName VARCHAR(100))
BEGIN
    SELECT product_id, product_name, price, quantity, reserved
    FROM products 
    WHERE LOWER(product_name) LIKE LOWER(CONCAT('%', searchName, '%'))
    AND quantity > 0;
END //

DELIMITER ;

-- Grant necessary permissions
GRANT SELECT, INSERT, UPDATE, DELETE ON grocery_db.* TO 'grocery_user'@'%';
FLUSH PRIVILEGES;

-- Display table creation summary
SELECT 'Database initialization completed successfully' AS status;
