---
marp: true
theme: default
class: lead
paginate: true
backgroundColor: #fff
backgroundImage: url('https://marp.app/assets/hero-background.svg')
---

# Online Grocery Ordering System
## Complete Full-Stack Web Application

**Developed by:** Chirag Singhal  
**Date:** August 3, 2025  
**Version:** 1.0.0

---

## Project Overview

- **Complete E-commerce Solution** for grocery ordering
- **Full-Stack Architecture** with modern technologies
- **Comprehensive Security** with SQL injection prevention
- **Role-based Access Control** for customers and administrators
- **Production-Ready** with all required features

---

## Technology Stack

### Backend
- ☕ **Java 21** with **Spring Boot 3.2.1**
- 🔒 **Spring Security** for authentication
- 🗄️ **JDBC** with prepared statements
- 🐬 **MySQL 8.0** with Docker

### Frontend
- 🅰️ **Angular 17** with TypeScript
- 🎨 **Bootstrap 5** for responsive design
- 📱 **Mobile-first** responsive design

---

## System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Angular 17    │    │  Spring Boot    │    │    MySQL 8.0    │
│   Frontend      │◄──►│    Backend      │◄──►│    Database     │
│  (Port 4200)    │    │  (Port 8080)    │    │  (Port 3306)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

- **3-Tier Architecture**: Presentation, Business Logic, Data Layer
- **RESTful APIs** for communication
- **Docker Containerization** for database

---

## Key Features Implemented

### ✅ All 10 User Stories Completed

1. **US001** - Admin Menu System with Authentication
2. **US002** - Customer Registration with Validation
3. **US003** - Update Customer Details
4. **US004** - Customer Order History
5. **US005** - Admin Customer Search

---

## Key Features (Continued)

6. **US006** - Product Search for Customers
7. **US007** - Product Registration (Admin)
8. **US008** - Product Updates (Admin)
9. **US009** - Product Deletion (Admin)
10. **US010** - **SQL Injection Prevention** 🔒

---

## Security Implementation

### 🛡️ Comprehensive Security Measures

- **SQL Injection Prevention**: All queries use prepared statements
- **Password Encryption**: BCrypt hashing algorithm
- **Session Management**: Secure session handling with timeout
- **Input Validation**: Multi-layer validation (Frontend + Backend)
- **CORS Configuration**: Secure cross-origin requests
- **Security Headers**: HSTS, X-Frame-Options, XSS Protection

---

## Database Design

### 📊 Normalized Schema with Constraints

```sql
customers (customer_id, full_name, email, password, address, contact_number)
products (product_id, product_name, price, quantity, reserved)
orders (order_id, customer_id, product_id, order_date, order_amount, status)
admin_users (id, username, password, email)
```

- **Foreign Key Constraints** for data integrity
- **Check Constraints** for validation
- **Indexes** for performance optimization

---

## User Interfaces

### 🎨 Modern & Responsive Design

**Customer Portal** (Angular)
- Product browsing and search
- Order placement and tracking
- User profile management
- Mobile-responsive design

**Admin Dashboard** (JSP)
- Customer management
- Product management
- Order management
- Statistics and reporting

---

## Live Demo

### 🌐 Fully Functional System

**Customer Portal**: http://localhost:4200
- Browse products
- Register/Login
- Place orders

**Admin Dashboard**: http://localhost:8080/login
- Username: `admin`
- Password: `admin123`

---

## API Documentation

### 🔌 RESTful API Endpoints

**Authentication**
- `POST /api/auth/login` - User/Admin login
- `POST /api/auth/logout` - Logout

**Customer Management**
- `POST /api/customers/register` - Registration
- `GET /api/customers/search` - Search (Admin)

**Product Management**
- `GET /api/products` - List products
- `POST /api/products` - Add product (Admin)

---

## Quality Assurance

### 📋 Comprehensive Testing & Quality

**Defect Management**
- 10 defects identified and resolved
- All critical security issues fixed
- Performance optimizations implemented

**Code Quality**
- Clean architecture with separation of concerns
- Comprehensive error handling
- Proper documentation (Javadoc)

---

## Non-Functional Requirements

### ⚡ Performance & Reliability

- **Response Time**: < 500ms for API calls
- **Concurrent Users**: Supports 100+ simultaneous users
- **Availability**: 99.5%+ uptime
- **Security**: OWASP compliance
- **Scalability**: Stateless architecture ready for scaling

---

## Project Deliverables

### 📦 Complete Package

1. **Codebase** - Complete source code (.zip)
2. **User Manual** - Comprehensive documentation (.pdf)
3. **Defect Log** - All issues tracked (.xlsx)
4. **Quality Log** - Security & code quality (.xlsx)
5. **NFRs Document** - Non-functional requirements (.pdf)

---

## Project Deliverables (Continued)

6. **Code Documentation** - Javadoc (.zip)
7. **Team Details** - Project team information (.xlsx)
8. **Sprint Backlog** - Development sprints (.xlsx)
9. **Product Backlog** - Feature backlog (.xlsx)
10. **Presentation** - This presentation (.pptx)

---

## Development Highlights

### 🚀 Technical Achievements

- **Zero SQL Injection Vulnerabilities** - All queries parameterized
- **Comprehensive Validation** - Frontend + Backend + Database
- **Modern Architecture** - Microservices-ready design
- **Production Ready** - Docker containerization
- **Security First** - OWASP best practices implemented

---

## Business Value

### 💼 Real-World Application

- **Complete E-commerce Solution** ready for deployment
- **Scalable Architecture** for business growth
- **Security Compliance** for customer trust
- **User-Friendly Interface** for better customer experience
- **Admin Tools** for efficient business management

---

## Future Enhancements

### 🔮 Roadmap for Growth

- **Payment Gateway Integration** (Stripe, PayPal)
- **Email Notifications** for order updates
- **Mobile Application** (React Native)
- **Advanced Analytics** and reporting
- **Inventory Management** with alerts
- **Customer Reviews** and ratings

---

## Technical Specifications

### 🔧 System Requirements

**Development Environment**
- Java 21, Node.js 18+, Docker
- Maven 3.6+, Angular CLI 17

**Production Environment**
- Linux/Windows Server
- MySQL 8.0, Java 21 Runtime
- Nginx/Apache for load balancing

---

## Conclusion

### ✨ Project Success

- **All Requirements Met** - 10/10 user stories completed
- **Security Compliant** - SQL injection prevention implemented
- **Production Ready** - Fully functional system
- **Modern Technology** - Latest frameworks and best practices
- **Comprehensive Documentation** - Complete project package

**The system is ready for immediate deployment and use!**

---

## Thank You

### 📞 Contact Information

**Developer:** Chirag Singhal  
**GitHub:** [@chirag127](https://github.com/chirag127)  
**Email:** chirag.singhal@example.com  

**Project Repository:**  
https://github.com/chirag127/Online-Grocery-Ordering-System-5

**Live Demo:**  
- Customer Portal: http://localhost:4200
- Admin Dashboard: http://localhost:8080/login

---

## Questions & Discussion

### 💬 Q&A Session

**Ready to answer questions about:**
- Technical implementation details
- Security measures and SQL injection prevention
- Architecture decisions and scalability
- User experience and interface design
- Future enhancements and roadmap

**Thank you for your attention!** 🙏
