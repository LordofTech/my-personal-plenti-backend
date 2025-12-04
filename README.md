# Plenti Backend

Complete Spring Boot backend for **Plenti** - A digital FMCG marketplace in Nigeria offering 60-minute delivery, zero stockouts, and transparent pricing.

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **MySQL 8.0**
- **Spring Security + JWT**
- **WebSocket (STOMP)** for real-time notifications
- **Lombok** for boilerplate reduction
- **SpringDoc OpenAPI** for Swagger documentation
- **Maven** for build management

## Features

- ✅ JWT-based authentication with OTP verification
- ✅ User management with referral system (2000 MetaCoins per referral)
- ✅ Product catalog with search and category filtering
- ✅ Shopping cart with 7-day persistence
- ✅ Wishlist management
- ✅ Order management with real-time status tracking
- ✅ Payment integration (Monnify)
- ✅ Multiple delivery addresses with GPS coordinates
- ✅ Product reviews and ratings (earn 10 Plenti Points per review)
- ✅ Promo codes with validation
- ✅ Store management (dark stores and partner stores)
- ✅ Admin dashboard with analytics
- ✅ Real-time notifications via WebSocket
- ✅ Email service integration
- ✅ Low stock alerts

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Docker (optional)

## Getting Started

### Local Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/LordofTech/my-personal-plenti-backend.git
   cd my-personal-plenti-backend
   ```

2. **Configure MySQL**
   
   Create a MySQL database:
   ```sql
   CREATE DATABASE plenti_db;
   ```

3. **Update application.properties**
   
   Edit `src/main/resources/application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/plenti_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

### Docker Setup

1. **Run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

This will start both MySQL and the Spring Boot application in containers.

## API Documentation

Once the application is running, access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

API documentation is also available at:
```
http://localhost:8080/api-docs
```

## API Endpoints

### Authentication
- `POST /api/auth/signup` - Register new user
- `POST /api/auth/login` - Login and get JWT token
- `POST /api/auth/otp/send` - Send OTP to phone
- `POST /api/auth/otp/verify` - Verify OTP code

### Users
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/profile` - Update profile
- `POST /api/users/refer` - Apply referral code
- `GET /api/users/payment-methods` - List payment methods
- `POST /api/users/payment-methods` - Add payment method

### Products
- `GET /api/products` - List all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/search?query=` - Search products
- `GET /api/products/category?category=` - Products by category

### Categories
- `GET /api/categories` - List all categories
- `GET /api/categories/{id}` - Get category with subcategories
- `POST /api/categories` - Create category (admin)
- `PUT /api/categories/{id}` - Update category (admin)
- `DELETE /api/categories/{id}` - Delete category (admin)

### Cart
- `GET /api/cart/{userId}` - Get user's cart
- `POST /api/cart/add` - Add item to cart
- `DELETE /api/cart/remove/{userId}/{productId}` - Remove item
- `PUT /api/cart/update` - Update quantity
- `DELETE /api/cart/clear/{userId}` - Clear cart

### Wishlist
- `GET /api/wishlist/{userId}` - Get user's wishlist
- `POST /api/wishlist/add` - Add product to wishlist
- `DELETE /api/wishlist/remove` - Remove from wishlist
- `POST /api/wishlist/move-to-cart` - Move item to cart

### Orders
- `POST /api/orders` - Place order
- `GET /api/orders/{id}` - Track order
- `GET /api/orders?userId=` - Order history
- `PUT /api/orders/{id}/status` - Update status (admin)
- `PUT /api/orders/{id}/cancel` - Cancel order
- `PUT /api/orders/{id}/assign-rider` - Assign rider (admin)

### Payments
- `POST /api/payments/initiate` - Start payment
- `GET /api/payments/verify` - Verify payment

### Addresses
- `GET /api/addresses/{userId}` - Get user addresses
- `POST /api/addresses` - Add address
- `PUT /api/addresses/{id}` - Update address
- `DELETE /api/addresses/{id}` - Delete address
- `PUT /api/addresses/{id}/default` - Set as default

### Reviews
- `POST /api/reviews` - Create review
- `GET /api/reviews/product/{productId}` - Product reviews
- `GET /api/reviews/user/{userId}` - User's reviews

### Promo Codes
- `POST /api/promo/validate` - Validate code
- `POST /api/promo/apply` - Apply to order
- `GET /api/promo` - List all (admin)
- `POST /api/promo` - Create code (admin)

### Stores
- `GET /api/stores` - List stores
- `POST /api/stores` - Add store (admin)
- `GET /api/stores/{id}` - Get store
- `PUT /api/stores/{id}` - Update store (admin)
- `GET /api/stores/nearest` - Find nearest store

### Support
- `GET /api/support/faq` - Get FAQs
- `POST /api/support/feedback` - Submit feedback
- `POST /api/support/rate` - Rate delivery

### Admin
- `GET /api/admin/users` - List users
- `POST /api/admin/products` - Create product
- `PUT /api/admin/products/{id}` - Update product
- `DELETE /api/admin/products/{id}` - Delete product
- `GET /api/admin/orders` - List all orders
- `GET /api/admin/analytics/summary` - Dashboard metrics
- `PUT /api/admin/products/{id}/restock` - Restock product

## Sample Data

The application includes a data loader that seeds sample data on first startup:
- 1 sample user (phone: +2348012345678, password: password123)
- 3 categories (Groceries, Beverages, Snacks)
- 3 sample products (Nigerian FMCG items)
- 2 stores (Ikeja Dark Store, Surulere Partner Store)

## WebSocket Connection

Connect to real-time order updates:
```
ws://localhost:8080/ws
```

Subscribe to order updates:
```
/topic/orders/{orderId}
```

## Testing

Run all tests:
```bash
mvn test
```

Run tests with coverage:
```bash
mvn test jacoco:report
```

Coverage report will be available at `target/site/jacoco/index.html`

## Configuration

Key configuration properties in `application.properties`:

- **JWT Secret**: Configure your JWT secret key
- **OTP Settings**: OTP expiry time and length
- **Cart Persistence**: Cart expiry (default 7 days)
- **Monnify Integration**: API keys and contract code
- **CORS**: Allowed origins for frontend

## Security

- All endpoints except `/api/auth/**` require JWT authentication
- Passwords are encrypted using BCrypt
- JWT tokens expire after 30 days
- OTP verification for phone numbers
- CORS configuration for frontend access

## Production Deployment

1. Update `application.properties` with production database credentials
2. Configure production JWT secret
3. Update Monnify API keys with production credentials
4. Set up SSL/TLS certificates
5. Configure email service (SMTP settings)
6. Set appropriate CORS origins

## Performance

- Load time: < 2 seconds
- Target uptime: 99.9%
- Scalable to 10K concurrent users
- Supports 100K orders/day

## License

Copyright © 2024 Plenti. All rights reserved.

## Contact

For issues or questions, please open an issue on GitHub.