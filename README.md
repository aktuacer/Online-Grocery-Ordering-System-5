# Online Grocery Ordering System

A comprehensive full-stack web application for online grocery ordering built with Java Spring Boot backend, Angular frontend, and MySQL database.

## Features

-   **User Authentication & Authorization**

    -   Admin login (username: admin, password: admin123)
    -   Customer registration and login
    -   Secure session management

-   **Customer Management**

    -   Customer registration with validation
    -   Update customer details
    -   Customer search (admin only)
    -   View customer order history

-   **Product Management**

    -   Product registration (admin only)
    -   Product search
    -   Update product details (admin only)
    -   Delete products (admin only)

-   **Security Features**
    -   SQL injection prevention using prepared statements
    -   Input validation and sanitization
    -   Password encryption
    -   Role-based access control

## Technology Stack

-   **Backend**: Java 21, Spring Boot 3.x, Spring Security, JDBC
-   **Frontend**: Angular 17, TypeScript, Bootstrap 5
-   **Database**: MySQL 8.0
-   **View Layer**: JSP for admin dashboard
-   **Build Tools**: Maven, npm

## Prerequisites

-   Java 21 (already installed)
-   Node.js 22.x (already installed)
-   MySQL 8.0 (will be configured)
-   Maven (included with Spring Boot)

## Quick Start

1. **Start the application**:

    ```bash
    # Start MySQL database
    docker-compose up -d mysql

    # Start Spring Boot backend
    cd backend
    ./mvnw spring-boot:run

    # Start Angular frontend (in new terminal)
    cd frontend
    npm start
    ```

2. **Access the application**:

    - Frontend: http://localhost:4200
    - Backend API: http://localhost:8080
    - Admin Dashboard: http://localhost:8080/admin

3. **Default Admin Credentials**:
    - Username: admin
    - Password: admin123

## Project Structure

```
Online-Grocery-Ordering-System-5/
├── backend/                 # Spring Boot application
│   ├── src/main/java/      # Java source code
│   ├── src/main/resources/ # Configuration files
│   └── src/main/webapp/    # JSP views
├── frontend/               # Angular application
│   ├── src/app/           # Angular components
│   └── src/assets/        # Static assets
├── database/              # Database scripts
├── docker-compose.yml     # Docker configuration
└── README.md             # This file
```

## API Endpoints

### Authentication

-   `POST /api/auth/login` - User login
-   `POST /api/auth/logout` - User logout

### Customer Management

-   `POST /api/customers/register` - Customer registration
-   `PUT /api/customers/{id}` - Update customer details
-   `GET /api/customers/{id}/orders` - Get customer orders
-   `GET /api/customers/search` - Search customers (admin only)

### Product Management

-   `POST /api/products` - Register product (admin only)
-   `GET /api/products/search` - Search products
-   `PUT /api/products/{id}` - Update product (admin only)
-   `DELETE /api/products/{id}` - Delete product (admin only)

## Database Schema

The application uses the following main tables:

-   `customers` - Customer information
-   `products` - Product catalog
-   `orders` - Order details
-   `admin_users` - Admin user credentials

## Security Features

-   **SQL Injection Prevention**: All database queries use prepared statements
-   **Input Validation**: Server-side validation for all user inputs
-   **Password Security**: BCrypt encryption for passwords
-   **Session Management**: Secure session handling with Spring Security
-   **CSRF Protection**: Cross-site request forgery protection enabled

## Development

### Backend Development

```bash
cd backend
./mvnw spring-boot:run
```

### Frontend Development

```bash
cd frontend
npm install
npm start
```

### Database Setup

```bash
docker-compose up -d mysql
```

## Testing

Run the test suite:

```bash
# Backend tests
cd backend
./mvnw test

# Frontend tests
cd frontend
npm test
```

## Author

**Chirag Singhal** (chirag127)

## License

This project is licensed under the MIT License.

---

_Last updated: [Will be updated with current timestamp]_
