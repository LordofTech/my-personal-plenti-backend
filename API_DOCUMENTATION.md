# Plenti Backend API Documentation

Complete API documentation for Plenti - Digital FMCG Marketplace Backend.

**Base URL:** `http://localhost:8080` (Development) | `https://your-domain.com` (Production)

**API Version:** 1.0.0

---

## Table of Contents
1. [Authentication](#authentication)
2. [Users](#users)
3. [Products](#products)
4. [Categories](#categories)
5. [Elasticsearch Search](#elasticsearch-search)
6. [Cart](#cart)
7. [Wishlist](#wishlist)
8. [Orders](#orders)
9. [Payments](#payments)
10. [Addresses](#addresses)
11. [Reviews](#reviews)
12. [Promo Codes](#promo-codes)
13. [Stores](#stores)
14. [Banners](#banners)
15. [Search & Analytics](#search--analytics)
16. [Support & Tickets](#support--tickets)
17. [Riders](#riders)
18. [Admin](#admin)

---

## Authentication

### Register New User
```http
POST /api/auth/signup
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phoneNumber": "+2348012345678",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "phoneNumber": "+2348012345678",
    "referralCode": "PLTJOHN123",
    "metaCoins": 0.0,
    "role": "USER",
    "trustScore": 100.0
  }
}
```

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "phoneNumber": "+2348012345678",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "user": { /* user details */ }
  }
}
```

### Send OTP
```http
POST /api/auth/otp/send
Content-Type: application/json

{
  "phoneNumber": "+2348012345678"
}
```

### Verify OTP
```http
POST /api/auth/otp/verify
Content-Type: application/json

{
  "phoneNumber": "+2348012345678",
  "otpCode": "123456"
}
```

---

## Products

### Get All Products
```http
GET /api/products
Authorization: Bearer {token}
```

### Get Product by ID
```http
GET /api/products/{id}
Authorization: Bearer {token}
```

### Search Products
```http
GET /api/products/search?query=rice
Authorization: Bearer {token}
```

### Get Products by Category
```http
GET /api/products/category?category=Food
GET /api/products/category/{categoryId}
Authorization: Bearer {token}
```

### Get Clearance Products
```http
GET /api/products/clearance
Authorization: Bearer {token}
```

**Response:** List of products on clearance sale with discounted prices.

### Get Freebie Products
```http
GET /api/products/freebies
Authorization: Bearer {token}
```

**Response:** List of products marked as freebies.

### Get Featured Products
```http
GET /api/products/featured
Authorization: Bearer {token}
```

**Response:** List of featured/promoted products.

### Get Flash Sale Products
```http
GET /api/products/flash-sales
Authorization: Bearer {token}
```

**Response:** List of products with active flash sales and time-limited pricing.

---

## Cart

### Get User Cart
```http
GET /api/cart/{userId}
Authorization: Bearer {token}
```

### Add Item to Cart
```http
POST /api/cart/add
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 1,
  "productId": 5,
  "quantity": 2
}
```

### Update Cart Item Quantity
```http
PUT /api/cart/update
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 1,
  "productId": 5,
  "quantity": 3
}
```

### Remove Item from Cart
```http
DELETE /api/cart/remove/{userId}/{productId}
Authorization: Bearer {token}
```

### Clear Cart
```http
DELETE /api/cart/clear/{userId}
Authorization: Bearer {token}
```

---

## Orders

### Create Order
```http
POST /api/orders
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 1,
  "deliveryAddress": "123 Main St, Lagos",
  "promoCode": "WELCOME30"
}
```

### Track Order
```http
GET /api/orders/{orderId}
Authorization: Bearer {token}
```

### Get User Orders
```http
GET /api/orders?userId={userId}
Authorization: Bearer {token}
```

### Cancel Order
```http
PUT /api/orders/{orderId}/cancel
Authorization: Bearer {token}
```

---

## Banners

### Get Active Banners
```http
GET /api/banners
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "Welcome to Plenti!",
      "description": "Get 30% off your first order",
      "imageUrl": "https://...",
      "linkUrl": "/promo",
      "displayOrder": 1,
      "isActive": true
    }
  ]
}
```

### Create Banner (Admin)
```http
POST /api/banners
Authorization: Bearer {admin-token}
Content-Type: application/json

{
  "title": "New Promo",
  "description": "Limited time offer",
  "imageUrl": "https://...",
  "linkUrl": "/products",
  "displayOrder": 1,
  "startDate": "2025-01-01T00:00:00",
  "endDate": "2025-01-31T23:59:59"
}
```

### Update Banner (Admin)
```http
PUT /api/banners/{id}
Authorization: Bearer {admin-token}
```

### Delete Banner (Admin)
```http
DELETE /api/banners/{id}
Authorization: Bearer {admin-token}
```

---

## Search & Analytics

### Track Search
```http
POST /api/search/track
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 1,
  "searchTerm": "rice",
  "resultCount": 15
}
```

### Get User Search History
```http
GET /api/search/history/{userId}
Authorization: Bearer {token}
```

### Get Recent Searches (Autocomplete)
```http
GET /api/search/recent/{userId}?limit=10
Authorization: Bearer {token}
```

### Get Popular Search Terms
```http
GET /api/search/popular?days=30
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "searchTerm": "rice",
      "count": 1245
    },
    {
      "searchTerm": "milk",
      "count": 892
    }
  ]
}
```

---

## Support & Tickets

### Get FAQs
```http
GET /api/support/faq
```

### Submit Feedback
```http
POST /api/support/feedback
Content-Type: application/json

{
  "userId": "1",
  "message": "Great service!"
}
```

### Create Support Ticket
```http
POST /api/support/tickets
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 1,
  "subject": "Order not delivered",
  "description": "My order #123 hasn't arrived",
  "category": "DELIVERY_ISSUE",
  "priority": "HIGH",
  "orderId": 123
}
```

### Get User Tickets
```http
GET /api/support/tickets/{userId}
Authorization: Bearer {token}
```

### Update Ticket Status (Admin)
```http
PUT /api/support/tickets/{ticketId}/status
Authorization: Bearer {admin-token}
Content-Type: application/json

{
  "status": "RESOLVED",
  "resolution": "Order has been delivered successfully"
}
```

---

## Riders

### Rider Login
```http
POST /api/rider/login
Content-Type: application/json

{
  "phoneNumber": "+2348012345678",
  "password": "rider123"
}
```

### Get Available Riders (Admin)
```http
GET /api/rider/available
Authorization: Bearer {admin-token}
```

### Update Rider Status
```http
PUT /api/rider/{riderId}/status
Authorization: Bearer {rider-token}
Content-Type: application/json

{
  "status": "BUSY"
}
```
**Status Options:** `AVAILABLE`, `BUSY`, `OFFLINE`

### Update Rider Location
```http
PUT /api/rider/location
Authorization: Bearer {rider-token}
Content-Type: application/json

{
  "riderId": 1,
  "latitude": 6.5964,
  "longitude": 3.3486
}
```

### Get Rider Location
```http
GET /api/rider/{riderId}/location
Authorization: Bearer {token}
```

### Get Rider Location History
```http
GET /api/rider/{riderId}/location/history
Authorization: Bearer {admin-token}
```

---

## Admin

### Get All Users
```http
GET /api/admin/users
Authorization: Bearer {admin-token}
```

### Create Product
```http
POST /api/admin/products
Authorization: Bearer {admin-token}
Content-Type: application/json

{
  "name": "New Product",
  "description": "Product description",
  "price": 1500.00,
  "category": "Food",
  "stock": 100,
  "imageUrl": "https://...",
  "categoryId": 1
}
```

### Update Product
```http
PUT /api/admin/products/{id}
Authorization: Bearer {admin-token}
```

### Delete Product
```http
DELETE /api/admin/products/{id}
Authorization: Bearer {admin-token}
```

### Restock Product
```http
PUT /api/admin/products/{id}/restock
Authorization: Bearer {admin-token}
Content-Type: application/json

{
  "quantity": 50
}
```

### Get Analytics Summary
```http
GET /api/admin/analytics/summary
Authorization: Bearer {admin-token}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "totalUsers": 15420,
    "totalOrders": 8932,
    "totalRevenue": 12450000.00,
    "activeOrders": 234,
    "lowStockProducts": 12
  }
}
```

---

## Response Format

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { /* response data */ }
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error message",
  "error": "Detailed error description"
}
```

---

## Status Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 500 | Internal Server Error |

---

## Authentication

Most endpoints require authentication. Include the JWT token in the Authorization header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

---

## Rate Limiting

- **Development:** No rate limiting
- **Production:** 1000 requests per hour per IP

---

## Pagination

Endpoints that return lists support pagination:

```http
GET /api/products?page=1&perPage=20
```

**Parameters:**
- `page`: Page number (default: 1)
- `perPage`: Items per page (default: 20, max: 100)

---

## Error Codes

| Code | Message |
|------|---------|
| USER_NOT_FOUND | User does not exist |
| INVALID_CREDENTIALS | Invalid phone number or password |
| PRODUCT_NOT_FOUND | Product does not exist |
| INSUFFICIENT_STOCK | Product out of stock |
| INVALID_PROMO_CODE | Promo code is invalid or expired |
| ORDER_NOT_FOUND | Order does not exist |

---

## WebSocket

Connect to real-time updates:

```javascript
const socket = new WebSocket('ws://localhost:8080/ws');

// Subscribe to order updates
socket.send(JSON.stringify({
  action: 'subscribe',
  channel: '/topic/orders/123'
}));
```

---

## Elasticsearch Search

The Plenti backend now includes Elasticsearch integration for lightning-fast search capabilities with fuzzy matching, advanced filtering, and autocomplete.

### Fast Product Search
```http
GET /api/es/products/search?q=indomie
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Indomie Instant Noodles",
    "description": "Popular instant noodles - Pack of 5",
    "price": 500.0,
    "category": "Groceries",
    "categoryId": 1,
    "stock": 100,
    "imageUrl": "https://example.com/indomie.jpg",
    "averageRating": 4.5,
    "reviewCount": 10,
    "isClearance": false,
    "isFreebie": false,
    "isFeatured": true,
    "lastUpdated": "2025-12-04T15:00:00"
  }
]
```

**Features:**
- Full-text search with fuzzy matching (tolerates typos)
- Searches across product name, description, and category
- Boosted scoring: name matches weighted higher than description

### Advanced Product Search
```http
GET /api/es/products/advanced-search?q=rice&category=1&minPrice=100&maxPrice=1000&inStock=true
```

**Query Parameters:**
- `q` (optional) - Search query text
- `category` (optional) - Filter by category ID
- `minPrice` (optional) - Minimum price filter
- `maxPrice` (optional) - Maximum price filter
- `inStock` (optional) - Filter for in-stock items only (true/false)

**Response:** Same as above

### Autocomplete Suggestions
```http
GET /api/es/autocomplete?q=indo&limit=5
```

**Response:**
```json
[
  "Indomie Instant Noodles",
  "Indomie Chicken Flavor",
  "Indomie Spicy"
]
```

### Search Categories
```http
GET /api/es/categories/search?q=grocery
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Groceries",
    "description": "Essential food items and household goods",
    "imageUrl": "https://example.com/groceries.jpg",
    "parentId": null
  }
]
```

### Search Stores
```http
GET /api/es/stores/search?q=ikeja
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Ikeja Dark Store",
    "location": "Ikeja, Lagos",
    "type": "dark",
    "latitude": 6.5964,
    "longitude": 3.3486
  }
]
```

### Manual Reindex (Admin Only)
```http
POST /api/es/reindex
Authorization: Bearer {admin_jwt_token}
```

**Response:**
```json
{
  "status": "success",
  "message": "Reindex started successfully"
}
```

Triggers a full reindex of all data from MySQL to Elasticsearch.

### Elasticsearch Health Check
```http
GET /api/es/health
```

**Response:**
```json
{
  "elasticsearch": "UP",
  "status": "healthy"
}
```

---

## Swagger UI

Interactive API documentation available at:
```
http://localhost:8080/swagger-ui.html
```

---

## Support

For API support, contact: support@plenti.ng

---

**Last Updated:** December 2025  
**Version:** 1.0.0
