# Changelog

All notable changes to the Online Grocery Ordering System project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-08-03

### Added

#### Backend Features
- Complete Spring Boot 3.2.1 backend with Java 21
- MySQL 8.0 database with Docker Compose setup
- Comprehensive database schema with foreign key constraints
- Sample data initialization with default admin user and products
- Spring Security configuration with session management
- CORS configuration for cross-origin requests
- Security headers (HSTS, X-Frame-Options, etc.)
- BCrypt password encryption for secure authentication
- Role-based access control (ADMIN, CUSTOMER)

#### API Endpoints
- Authentication endpoints (`/api/auth/*`)
  - POST `/api/auth/login` - User/Admin login
  - POST `/api/auth/logout` - Logout
  - GET `/api/auth/status` - Check authentication status
- Customer management endpoints (`/api/customers/*`)
  - POST `/api/customers/register` - Register new customer
  - GET `/api/customers` - Get all customers (Admin only)
  - GET `/api/customers/search` - Search customers by name (Admin only)
  - POST `/api/customers/check-email` - Check email availability
- Product management endpoints (`/api/products/*`)
  - GET `/api/products` - Get all products
  - GET `/api/products/{id}` - Get product by ID
  - POST `/api/products` - Create product (Admin only)
  - PUT `/api/products/{id}` - Update product (Admin only)
  - DELETE `/api/products/{id}` - Delete product (Admin only)
- Order management endpoints (`/api/orders/*`)
  - GET `/api/orders` - Get all orders (Admin only)
  - POST `/api/orders` - Create new order
  - GET `/api/orders/{id}` - Get order by ID
  - PUT `/api/orders/{id}/status` - Update order status (Admin only)
  - GET `/api/orders/customer/{customerId}` - Get orders by customer
  - GET `/api/orders/statistics` - Get order statistics (Admin only)

#### Admin Dashboard (JSP)
- Secure admin login page with CSRF protection
- Comprehensive admin dashboard with sidebar navigation
- Customer management interface with search functionality
- Product management with CRUD operations
- Order management with status updates
- Statistics and reporting dashboard
- Responsive design with Bootstrap 5

#### Frontend (Angular 17)
- Modern Angular 17 application with TypeScript
- Bootstrap 5 integration for responsive design
- Font Awesome icons for enhanced UI
- Home component with hero section and featured products
- Navigation bar with authentication status
- Footer with admin portal link
- Routing configuration with lazy loading support

#### Security Features
- SQL injection prevention using prepared statements with parameterized queries
- Input validation using Jakarta validation annotations
- Comprehensive error handling with try-catch blocks
- Custom exception handling for better error responses
- Session management with Spring Security
- Password encryption using BCrypt algorithm
- Role-based authorization for different user types

#### Database Features
- Normalized database schema with proper relationships
- Foreign key constraints for data integrity
- Check constraints for data validation
- Indexes for performance optimization
- Database views for complex queries
- Stored procedures for advanced operations
- Sample data for testing and demonstration

#### Development Tools
- Maven configuration with all necessary dependencies
- Docker Compose for MySQL database containerization
- Angular CLI configuration with development server
- Hot reload support for both backend and frontend
- Comprehensive error logging and debugging support

### Technical Specifications

#### Backend Dependencies
- Spring Boot Starter Web 3.2.1
- Spring Boot Starter Security 3.2.1
- Spring Boot Starter JDBC 3.2.1
- Spring Boot Starter Validation 3.2.1
- Spring Boot Starter Tomcat (JSP support)
- MySQL Connector/J 8.0.33
- Jakarta Servlet JSP JSTL 3.0.1
- Tomcat Embed Jasper 10.1.17
- Spring Security Web 6.2.1
- Spring Security Config 6.2.1

#### Frontend Dependencies
- Angular 17.3.0
- Angular Router 17.3.0
- Angular Common 17.3.0
- TypeScript 5.4.2
- Bootstrap 5.3.3
- Font Awesome 6.0.0
- RxJS 7.8.0
- Zone.js 0.14.3

#### Database Schema
- `customers` table with encrypted passwords
- `products` table with inventory management
- `orders` table with status tracking
- `order_items` table for order details
- `admins` table for admin users
- Database views for reporting
- Stored procedures for complex operations

### Default Credentials

#### Admin User
- Username: `admin`
- Password: `admin123`

#### Sample Customer
- Email: `john.doe@example.com`
- Password: `password123`

### Installation Requirements
- Java 21 or higher
- Node.js 18 or higher
- Docker and Docker Compose
- Maven 3.6 or higher

### Deployment Information
- Backend runs on port 8080
- Frontend runs on port 4200
- MySQL database runs on port 3306
- Docker Compose manages database container

### Known Issues
- None at this time

### Future Enhancements
- Payment gateway integration
- Email notification system
- Advanced search and filtering
- Mobile application support
- Real-time order tracking
- Inventory management alerts
- Customer reviews and ratings
- Promotional codes and discounts

---

**Author:** Chirag Singhal ([@chirag127](https://github.com/chirag127))  
**Date:** August 3, 2025 - 07:45 UTC  
**Version:** 1.0.0  
**Repository:** https://github.com/chirag127/Online-Grocery-Ordering-System-5
