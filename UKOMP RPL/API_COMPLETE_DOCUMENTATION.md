# 🚀 MuscleCart API Documentation

## ✅ Status Konfigurasi:
- **Backend API**: ✅ Ready for Mobile App
- **Authentication**: ✅ Laravel Sanctum
- **CORS**: ✅ Configured
- **Endpoints**: ✅ 18 API endpoints implemented

---

## 📋 Base Configuration

### Base URLs
```
Local Development: http://localhost:8000/api/v1/
Android Emulator: http://10.0.2.2:8000/api/v1/
Production: https://your-domain.com/api/v1/
```

### Authentication
- **Type**: Bearer Token (Laravel Sanctum)
- **Header**: `Authorization: Bearer {token}`
- **Token Source**: Login/Register response

---

## 🔐 Authentication Endpoints

### 1. Register
```http
POST /api/v1/register
Content-Type: application/json

{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "password_confirmation": "password123",
    "phone": "081234567890",
    "address": "Jl. Contoh No. 123"
}
```

**Response:**
```json
{
    "status": "success",
    "message": "User registered successfully",
    "data": {
        "user": {
            "id": 1,
            "name": "John Doe",
            "email": "john@example.com",
            "phone": "081234567890",
            "address": "Jl. Contoh No. 123",
            "role": "customer"
        },
        "token": "1|abc123..."
    }
}
```

### 2. Login
```http
POST /api/v1/login
Content-Type: application/json

{
    "email": "john@example.com",
    "password": "password123"
}
```

### 3. Get User Profile (Protected)
```http
GET /api/v1/user
Authorization: Bearer {token}
```

### 4. Update Profile (Protected)
```http
PUT /api/v1/user/profile
Authorization: Bearer {token}
Content-Type: application/json

{
    "name": "John Updated",
    "phone": "081234567890",
    "address": "New Address",
    "password": "newpassword123",
    "password_confirmation": "newpassword123"
}
```

### 5. Logout (Protected)
```http
POST /api/v1/logout
Authorization: Bearer {token}
```

---

## 📦 Product Endpoints

### 6. Get All Products
```http
GET /api/v1/products
Query Parameters:
- search: string (optional)
- category_id: integer (optional)
- featured: boolean (optional)
- sort_by: price|name|created_at (default: created_at)
- sort_order: asc|desc (default: desc)
```

**Response:**
```json
{
    "status": "success",
    "data": [
        {
            "id": 1,
            "name": "Whey Protein",
            "description": "High quality whey protein",
            "price": "450000.00",
            "stock_quantity": 100,
            "image": "/storage/products/whey.jpg",
            "is_active": true,
            "is_featured": true,
            "category": {
                "id": 1,
                "name": "Supplements"
            }
        }
    ],
    "pagination": {
        "current_page": 1,
        "last_page": 5,
        "per_page": 20,
        "total": 95
    }
}
```

### 7. Get Product Details
```http
GET /api/v1/products/{id}
```

**Response includes related products from same category**

---

## 🏷️ Category Endpoints

### 8. Get All Categories
```http
GET /api/v1/categories
Query Parameters:
- search: string (optional)
```

**Response:**
```json
{
    "status": "success",
    "data": [
        {
            "id": 1,
            "name": "Supplements",
            "description": "Fitness supplements",
            "image": "/storage/categories/supplements.jpg",
            "is_active": true,
            "products_count": 25
        }
    ]
}
```

### 9. Get Products by Category
```http
GET /api/v1/categories/{id}/products
Query Parameters: (same as products endpoint)
```

---

## 🛒 Cart Endpoints (Protected)

### 10. Get Cart Items
```http
GET /api/v1/cart
Authorization: Bearer {token}
```

**Response:**
```json
{
    "status": "success",
    "data": {
        "items": [
            {
                "id": 1,
                "product": {
                    "id": 1,
                    "name": "Whey Protein",
                    "price": "450000.00",
                    "image": "/storage/products/whey.jpg",
                    "stock_quantity": 100,
                    "category": "Supplements"
                },
                "quantity": 2,
                "subtotal": 900000,
                "added_at": "2024-01-15T10:30:00Z"
            }
        ],
        "summary": {
            "total_items": 2,
            "subtotal": 900000,
            "tax": 0,
            "shipping": 0,
            "total": 900000
        }
    }
}
```

### 11. Add to Cart
```http
POST /api/v1/cart/add
Authorization: Bearer {token}
Content-Type: application/json

{
    "product_id": 1,
    "quantity": 2
}
```

### 12. Update Cart Item
```http
PUT /api/v1/cart/update/{cart_item_id}
Authorization: Bearer {token}
Content-Type: application/json

{
    "quantity": 3
}
```

### 13. Remove from Cart
```http
DELETE /api/v1/cart/remove/{cart_item_id}
Authorization: Bearer {token}
```

### 14. Clear Cart
```http
DELETE /api/v1/cart/clear
Authorization: Bearer {token}
```

---

## 📋 Order Endpoints (Protected)

### 15. Get Order History
```http
GET /api/v1/orders
Authorization: Bearer {token}
Query Parameters:
- status: pending|processing|shipped|delivered|cancelled (optional)
```

### 16. Create Order
```http
POST /api/v1/orders
Authorization: Bearer {token}
Content-Type: application/json

{
    "payment_method": "credit_card",
    "shipping_address": {
        "name": "John Doe",
        "phone": "081234567890",
        "address": "Jl. Contoh No. 123",
        "city": "Jakarta",
        "postal_code": "12345",
        "province": "DKI Jakarta"
    },
    "billing_address": {
        "name": "John Doe",
        "phone": "081234567890",
        "address": "Jl. Contoh No. 123",
        "city": "Jakarta",
        "postal_code": "12345",
        "province": "DKI Jakarta"
    },
    "notes": "Deliver before 5 PM"
}
```

**Response:**
```json
{
    "status": "success",
    "message": "Order created successfully",
    "data": {
        "order": {
            "id": 1,
            "order_number": "ORD-20240115-ABC123",
            "total_amount": "915000.00",
            "tax_amount": "0.00",
            "shipping_amount": "15000.00",
            "status": "pending",
            "payment_status": "pending",
            "payment_method": "credit_card",
            "orderItems": [
                {
                    "product_name": "Whey Protein",
                    "quantity": 2,
                    "price": "450000.00",
                    "total": "900000.00"
                }
            ]
        }
    }
}
```

### 17. Get Order Details
```http
GET /api/v1/orders/{id}
Authorization: Bearer {token}
```

### 18. Cancel Order
```http
PUT /api/v1/orders/{id}/cancel
Authorization: Bearer {token}
```

---

## 🧪 Testing the API

### 1. Start Backend Server
```bash
cd "c:\XAAMP\htdocs\UKOMP CODING RPL SMK\musclecart-admin"
php artisan serve --host=0.0.0.0 --port=8000
```

### 2. Test with cURL/Postman

#### Register Test:
```bash
curl -X POST http://localhost:8000/api/v1/register \
-H "Content-Type: application/json" \
-d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123",
    "password_confirmation": "password123"
}'
```

#### Get Products Test:
```bash
curl -X GET http://localhost:8000/api/v1/products
```

#### Login Test:
```bash
curl -X POST http://localhost:8000/api/v1/login \
-H "Content-Type: application/json" \
-d '{
    "email": "test@example.com",
    "password": "password123"
}'
```

### 3. Android Development

#### Network Security Config
Add to `android/app/src/main/res/xml/network_security_config.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">localhost</domain>
    </domain-config>
</network-security-config>
```

#### Base API Configuration
```kotlin
object ApiConfig {
    private const val BASE_URL = "http://10.0.2.2:8000/api/v1/"
    
    fun getApiService(): ApiService {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
            
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            
        return retrofit.create(ApiService::class.java)
    }
}
```

---

## 🔧 Error Handling

### Standard Error Response Format:
```json
{
    "status": "error",
    "message": "Error description",
    "errors": {
        "field": ["Validation error message"]
    }
}
```

### Common HTTP Status Codes:
- **200**: Success
- **201**: Created
- **401**: Unauthorized (invalid/missing token)
- **403**: Forbidden (account suspended)
- **404**: Not Found
- **422**: Validation Error
- **500**: Server Error

---

## 🎯 Development Tips

1. **Always use HTTPS in production**
2. **Store tokens securely in mobile app**
3. **Implement proper error handling**
4. **Add loading states for API calls**
5. **Handle network connectivity issues**
6. **Implement retry mechanisms**
7. **Use proper request/response models**

---

## 📞 Support

Jika ada kendala dalam integrasi API, silakan cek:
1. Laravel logs: `storage/logs/laravel.log`
2. Network connectivity
3. Token expiration
4. Request format/headers
5. Database connection

**API Status**: ✅ Ready for Mobile Integration!