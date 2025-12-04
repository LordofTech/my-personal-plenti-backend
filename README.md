# Plenti Backend

Complete Spring Boot backend for **Plenti** - A digital FMCG marketplace in Nigeria offering 60-minute delivery, zero stockouts, and transparent pricing.

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **MySQL 8.0** for persistent data storage
- **Elasticsearch 8.11.0** for fast full-text search
- **Spring Security + JWT**
- **WebSocket (STOMP)** for real-time notifications
- **Lombok** for boilerplate reduction
- **SpringDoc OpenAPI** for Swagger documentation
- **Maven** for build management

## Features

### Core Features
- ✅ JWT-based authentication with OTP verification
- ✅ Role-based access control (Admin, User, Rider)
- ✅ User management with referral system (2000 MetaCoins per referral)
- ✅ Trust score system for users
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
- ✅ SMS notifications via Termii
- ✅ Low stock alerts

### New Features
- ✅ **Product Variants** - Size, color variants support (S, M, L, XL, XXL for Fashion)
- ✅ **Multiple Product Images** - Gallery support for products
- ✅ **Clearance Sale** - Products on clearance with discounted prices
- ✅ **Freebies** - Free products and promotional items
- ✅ **Featured Products** - Highlighted products on homepage
- ✅ **Flash Sales** - Time-limited promotional pricing
- ✅ **Bulk Pricing** - Discounts for bulk purchases
- ✅ **Banner Management** - Promotional banner system for admins
- ✅ **Search Analytics** - Track search history and popular terms
- ✅ **Support Ticket System** - Customer support ticketing
- ✅ **Rider Management** - Delivery rider operations and tracking
- ✅ **Real-time Rider Location** - GPS tracking for delivery riders
- ✅ **Activity Logs** - Audit trail for admin actions
- ✅ **Combo Deals** - Product bundling feature
- ✅ **Guest Checkout** - Support for guest users (field added)
- ✅ **Elasticsearch Integration** - Lightning-fast product search with typo tolerance
  - Full-text search with fuzzy matching
  - Advanced filtering (category, price range, stock)
  - Autocomplete suggestions
  - Graceful degradation if Elasticsearch is unavailable

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Elasticsearch 8.11.0 (optional, for enhanced search)
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

This will start MySQL, Elasticsearch, and the Spring Boot application in containers.

2. **Verify Elasticsearch is running**
   ```bash
   curl http://localhost:9200/_cluster/health
   ```

The application will automatically sync all data from MySQL to Elasticsearch on startup.

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
- `GET /api/products/search?query=` - Search products (MySQL-based)
- `GET /api/products/category?category=` - Products by category

### Elasticsearch Search (Fast Search)
- `GET /api/es/products/search?q=` - Lightning-fast product search
- `GET /api/es/products/advanced-search?q=&category=&minPrice=&maxPrice=&inStock=` - Advanced filtered search
- `GET /api/es/autocomplete?q=&limit=10` - Get autocomplete suggestions
- `GET /api/es/categories/search?q=` - Search categories
- `GET /api/es/stores/search?q=` - Search stores
- `POST /api/es/reindex` - Manual reindex (admin only)
- `GET /api/es/health` - Check Elasticsearch health

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

### Database Setup

1. **Create MySQL Database**
   ```sql
   CREATE DATABASE plenti_db;
   CREATE USER 'plenti_user'@'%' IDENTIFIED BY 'secure_password';
   GRANT ALL PRIVILEGES ON plenti_db.* TO 'plenti_user'@'%';
   FLUSH PRIVILEGES;
   ```

2. **Load Seed Data**
   ```bash
   mysql -u plenti_user -p plenti_db < src/main/resources/db/seed-data.sql
   ```

### Environment Variables

Set the following environment variables in your production environment:

```bash
# Database
export DB_URL="jdbc:mysql://localhost:3306/plenti_db"
export DB_USER="plenti_user"
export DB_PASSWORD="your_secure_password"

# JWT
export JWT_SECRET="your-jwt-secret-min-256-bits-long"

# Payment (Monnify)
export MONNIFY_API_KEY="your_monnify_api_key"
export MONNIFY_SECRET_KEY="your_monnify_secret_key"
export MONNIFY_CONTRACT_CODE="your_contract_code"
export MONNIFY_BASE_URL="https://api.monnify.com"

# SMS (Termii)
export SMS_API_KEY="your_termii_api_key"

# Email
export MAIL_HOST="smtp.gmail.com"
export MAIL_PORT="587"
export MAIL_USERNAME="your-email@gmail.com"
export MAIL_PASSWORD="your-app-password"

# CORS
export CORS_ORIGINS="https://yourdomain.com"
```

### Docker Production Deployment

1. **Using Docker Compose Production File**
   ```bash
   # Create .env file with your environment variables
   cp .env.example .env
   
   # Edit .env with your production values
   nano .env
   
   # Start services
   docker-compose -f docker-compose.prod.yml up -d
   ```

2. **Check Logs**
   ```bash
   docker-compose -f docker-compose.prod.yml logs -f plenti-backend
   ```

3. **Stop Services**
   ```bash
   docker-compose -f docker-compose.prod.yml down
   ```

### Deploy to Render

1. **Push to GitHub**
   ```bash
   git push origin main
   ```

2. **Create New Web Service on Render**
   - Connect your GitHub repository
   - Render will automatically detect `render.yaml`
   - Configure environment variables in Render dashboard
   - Deploy!

3. **Environment Variables on Render**
   - Set all required variables in the Render dashboard
   - JWT_SECRET will be auto-generated
   - Configure your Monnify and Termii API keys

### Deploy to Railway

1. **Install Railway CLI**
   ```bash
   npm i -g @railway/cli
   ```

2. **Login and Deploy**
   ```bash
   railway login
   railway init
   railway up
   ```

3. **Configure Environment**
   ```bash
   railway variables set DB_USER=plenti_user
   railway variables set DB_PASSWORD=your_password
   # ... set other variables
   ```

### Health Checks

The application exposes health check endpoints:

```
GET /actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

## Performance

- Load time: < 2 seconds
- Target uptime: 99.9%
- Scalable to 10K concurrent users
- Supports 100K orders/day

## License

Copyright © 2024 Plenti. All rights reserved.

## Contact

For issues or questions, please open an issue on GitHub.