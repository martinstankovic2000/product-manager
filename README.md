# Product Manager
A simple REST service that manages a list of products with JWT-based authentication and role-based authorization.

## Features

### Product Management
Full CRUD operations for products

### JWT Authentication
Secure token-based authentication system

### Role-Based Authorization
ADMIN: Full access (create, read, update, delete products)

CUSTOMER: Read-only access (view and search products)

### Search Capabilities
Name filtering: Case-insensitive partial matching

Price range filtering: Support for both EUR and USD currencies

Pagination: Configurable page size (1-20 items)

Sorting: Sort by NAME or PRICE

### Validation
Input validation with custom error messages

Internationalization support

Proper HTTP status codes and error responses

### Security Features
JWT token-based authentication
Role-based access control
Secure password encoding
Custom security exception handling

### External Services
HNB Currency API
The application integrates with the Croatian National Bank API for EUR to USD currency conversion.

Endpoint: https://api.hnb.hr/tecajn-eur/v3?valuta=USD

## Technology Stack

Java 17

Spring Boot 3.5.4

Spring Security with JWT tokens

Spring Data JPA with Hibernate

PostgreSQL 14.18 database

Maven build tool

Lombok for reducing boilerplate code

Docker Compose for local development

## Prerequisites

Java 17 or higher

Maven 3.6+

Docker and Docker Compose

PostgreSQL 14+ (if running without Docker)

1. Clone the repository

	git clone https://github.com/martinstankovic2000/product-manager.git

	cd product-manager

2. Start the dev database

	docker compose --env-file .env.dev up -d

3. Run the application

	Run run-dev.sh script, it will load environment variables from .env.dev file and run 'mvn spring-boot:run'

## Database Access

pgAdmin: http://localhost:5050

Email: pgadmin@demo.com
Password: password

## Authentication
The application automatically creates a default admin user on startup:

Username: admin
Password: admin

## API Endpoints
### Postman collection used for testing is included
Optional add 'Accept-Language' header with value 'hr' for responses in Croatian
### Authentication
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}

POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin"
}

### Product Management
All product endpoints require authentication. Include the JWT token from /api/auth/login response in the Authorization header.

### Create Product (ADMIN only)

POST /api/products
Content-Type: application/json

{
  "name": "Laptop",
  "priceEur": 399.99,
  "isAvailable": true
}

### Get Product by Code (ADMIN & CUSTOMER)

GET /api/products/{code}

### Search Products (ADMIN & CUSTOMER)

POST /api/products/search
Content-Type: application/json

{
  "name": "Test",
  "minPriceEur": 10.00,
  "maxPriceEur": 100.00,
  "page": 0,
  "size": 10,
  "sortBy": "NAME",
  "sortAscending": true
}

### Update Product (ADMIN only)

PUT /api/products/{code}
Content-Type: application/json

{
  "name": "Laptop",
  "priceEur": 349.99,
  "isAvailable": true
}

### Delete Product (ADMIN only)

DELETE /api/products/{code}