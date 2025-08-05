# Online Grocery Ordering System - User Manual

**Version:** 1.0.0  
**Date:** August 3, 2025  
**Author:** Chirag Singhal  

## Table of Contents

1. [Introduction](#introduction)
2. [System Requirements](#system-requirements)
3. [Getting Started](#getting-started)
4. [Customer Portal Guide](#customer-portal-guide)
5. [Admin Dashboard Guide](#admin-dashboard-guide)
6. [Features Overview](#features-overview)
7. [Troubleshooting](#troubleshooting)
8. [Support](#support)

## 1. Introduction

The Online Grocery Ordering System is a comprehensive web application that allows customers to order groceries online and provides administrators with tools to manage products, customers, and orders. The system features a modern Angular frontend for customers and a JSP-based admin dashboard for administrators.

### Key Features
- Customer registration and authentication
- Product browsing and searching
- Order placement and tracking
- Admin dashboard for management
- Secure payment processing
- Real-time inventory management

## 2. System Requirements

### For Users (Customers)
- Modern web browser (Chrome, Firefox, Safari, Edge)
- Internet connection
- JavaScript enabled

### For Administrators
- Modern web browser with JavaScript enabled
- Admin credentials provided by system administrator

## 3. Getting Started

### Accessing the System

**Customer Portal:** http://localhost:4200  
**Admin Dashboard:** http://localhost:8080/login

### Default Credentials

**Admin Login:**
- Username: `admin`
- Password: `admin123`

**Sample Customer:**
- Email: `john.doe@example.com`
- Password: `password123`

## 4. Customer Portal Guide

### 4.1 Registration

1. Navigate to http://localhost:4200
2. Click "Register" in the navigation bar
3. Fill in the registration form:
   - **Full Name:** Your complete name
   - **Email:** Valid email address (will be your username)
   - **Password:** Must be at least 8 characters with uppercase, lowercase, digit, and special character
   - **Address:** Your delivery address
   - **Contact Number:** 10-digit phone number
4. Click "Register" to create your account
5. You will be automatically logged in after successful registration

### 4.2 Login

1. Click "Login" in the navigation bar
2. Enter your email and password
3. Click "Login" to access your account

### 4.3 Browsing Products

1. Click "Products" in the navigation menu
2. Browse through available products
3. Use the search bar to find specific products
4. View product details including:
   - Product name and description
   - Price per unit
   - Available quantity
   - Product image (if available)

### 4.4 Searching Products

1. Navigate to the Products page
2. Enter product name in the search box
3. Click the search button or press Enter
4. View filtered results
5. Click "Clear" to reset search results

### 4.5 Placing Orders

1. Browse or search for products
2. Click "Add to Cart" for desired products
3. Specify quantity if needed
4. Review your cart
5. Click "Checkout" to place order
6. Confirm order details
7. Submit your order

### 4.6 Viewing Order History

1. Log in to your account
2. Click "My Orders" in the navigation menu
3. View your order history including:
   - Order ID and date
   - Products ordered
   - Order amount
   - Order status
   - Delivery information

### 4.7 Managing Profile

1. Click on your name in the navigation bar
2. Select "Profile" from dropdown
3. Update your information:
   - Full name
   - Email address
   - Delivery address
   - Contact number
4. Change password if needed
5. Save changes

## 5. Admin Dashboard Guide

### 5.1 Admin Login

1. Navigate to http://localhost:8080/login
2. Enter admin credentials:
   - Username: `admin`
   - Password: `admin123`
3. Click "Login" to access the dashboard

### 5.2 Dashboard Overview

The admin dashboard provides:
- System statistics and metrics
- Recent orders overview
- Low stock alerts
- Quick access to management functions

### 5.3 Customer Management

#### Viewing All Customers
1. Click "Customers" in the sidebar
2. View complete customer list with:
   - Customer ID
   - Full name
   - Email address
   - Contact information
   - Registration date

#### Searching Customers
1. Navigate to Customers section
2. Enter customer name in search box
3. Click "Search" to filter results
4. View matching customer records

#### Customer Actions
- View customer details
- View customer order history
- Delete customer accounts (if necessary)

### 5.4 Product Management

#### Viewing Products
1. Click "Products" in the sidebar
2. View all products with:
   - Product ID
   - Product name
   - Price
   - Available quantity
   - Reserved quantity

#### Adding New Products
1. Click "Add Product" button
2. Fill in product details:
   - Product name
   - Price (must be positive)
   - Initial quantity
   - Product description
3. Click "Save" to add product

#### Updating Products
1. Find the product in the list
2. Click "Edit" button
3. Modify product information
4. Click "Update" to save changes

#### Deleting Products
1. Find the product in the list
2. Click "Delete" button
3. Confirm deletion
4. Product will be removed from system

### 5.5 Order Management

#### Viewing Orders
1. Click "Orders" in the sidebar
2. View all orders with:
   - Order ID
   - Customer information
   - Order date
   - Products ordered
   - Order amount
   - Current status

#### Filtering Orders
1. Use status filter dropdown
2. Select desired status:
   - Pending
   - Confirmed
   - Shipped
   - Delivered
   - Cancelled
3. View filtered results

#### Updating Order Status
1. Find the order in the list
2. Click "Update Status" button
3. Select new status from dropdown
4. Click "Update" to save changes
5. Customer will be notified of status change

### 5.6 Reports and Analytics

#### Order Statistics
1. Click "Reports" in the sidebar
2. View comprehensive statistics:
   - Total orders
   - Orders by status
   - Revenue metrics
   - Customer metrics

#### Generating Reports
1. Select date range for reports
2. Choose report type
3. Click "Generate Report"
4. Download or view report

## 6. Features Overview

### 6.1 Security Features

- **SQL Injection Prevention:** All database queries use prepared statements
- **Password Encryption:** BCrypt encryption for all passwords
- **Session Management:** Secure session handling with automatic timeout
- **Input Validation:** Comprehensive validation for all user inputs
- **Role-based Access:** Separate access levels for customers and admins

### 6.2 User Experience Features

- **Responsive Design:** Works on desktop, tablet, and mobile devices
- **Real-time Updates:** Inventory and order status updates in real-time
- **Search Functionality:** Fast and accurate product and customer search
- **Intuitive Navigation:** Easy-to-use interface for all user types

### 6.3 Business Features

- **Inventory Management:** Real-time stock tracking and low stock alerts
- **Order Tracking:** Complete order lifecycle management
- **Customer Management:** Comprehensive customer information system
- **Reporting:** Detailed analytics and reporting capabilities

## 7. Troubleshooting

### Common Issues

#### Cannot Login
- Verify username/email and password
- Check if account is active
- Clear browser cache and cookies
- Contact administrator if issue persists

#### Products Not Loading
- Check internet connection
- Refresh the page
- Clear browser cache
- Try different browser

#### Order Not Placed
- Verify product availability
- Check if you're logged in
- Ensure all required fields are filled
- Contact support if issue continues

#### Admin Dashboard Not Accessible
- Verify admin credentials
- Check if you have admin privileges
- Clear browser cache
- Contact system administrator

### Error Messages

#### "Please Enter Correct UserName and Password"
- Double-check your credentials
- Ensure caps lock is off
- Try password reset if available

#### "Product not found"
- Product may be out of stock
- Check spelling in search
- Browse categories instead

#### "Customer not found"
- Verify customer name spelling
- Check if customer is registered
- Try partial name search

## 8. Support

### Contact Information

**Technical Support:**
- Email: support@grocerystore.com
- Phone: +1-800-GROCERY

**System Administrator:**
- Email: admin@grocerystore.com

### Business Hours
- Monday - Friday: 9:00 AM - 6:00 PM
- Saturday: 10:00 AM - 4:00 PM
- Sunday: Closed

### Online Resources
- User Guide: Available in the application
- FAQ: Check the help section
- Video Tutorials: Available on our website

---

**Document Information:**
- Version: 1.0.0
- Last Updated: August 3, 2025
- Author: Chirag Singhal
- Document Type: User Manual
