# Design Document

## Overview

The Gym Equipment & Fitness Supplement Ecommerce application is a Laravel-based web application following the MVC (Model-View-Controller) architecture. The design emphasizes simplicity, maintainability, and core ecommerce functionality suitable for a school competency exam. The application uses Laravel's built-in authentication, Eloquent ORM for database operations, and session-based cart management.

## Architecture

### Application Structure
```
app/
├── Http/
│   ├── Controllers/
│   │   ├── Auth/
│   │   ├── Admin/
│   │   └── User/
│   ├── Middleware/
│   └── Requests/
├── Models/
├── Services/
└── Providers/

resources/
├── views/
│   ├── layouts/
│   ├── auth/
│   ├── admin/
│   ├── user/
│   └── components/
└── css/

database/
├── migrations/
├── seeders/
└── factories/

routes/
├── web.php
└── auth.php

public/
├── css/
├── js/
└── images/
```

### Technology Stack
- **Backend Framework:** Laravel 10.x
- **Database:** MySQL
- **Authentication:** Laravel Breeze (simplified)
- **Frontend:** Blade Templates with Custom CSS
- **Session Management:** File-based sessions for cart storage
- **Image Storage:** Local storage in public/images directory

## Components and Interfaces

### Authentication System
- **LoginController:** Handles user authentication
- **RegisterController:** Manages user registration
- **Middleware:** `AdminMiddleware` for role-based access control
- **Views:** Simple login/register forms with validation

### Admin Panel Components
- **AdminDashboardController:** Displays statistics and overview
- **ProductController:** CRUD operations for products
- **CategoryController:** CRUD operations for categories  
- **OrderController:** Order management and status updates
- **Views:** Clean admin interface with navigation sidebar

### User Shopping Components
- **HomeController:** Homepage with featured products
- **ProductController:** Product listing and detail views
- **CartController:** Session-based cart management
- **CheckoutController:** Order processing and completion
- **Views:** Product cards, cart interface, checkout flow

### Cart Service
```php
class CartService
{
    public function addItem($productId, $quantity)
    public function removeItem($productId)
    public function updateQuantity($productId, $quantity)
    public function getTotal()
    public function clear()
    public function getItems()
}
```

## Data Models

### User Model
```php
class User extends Authenticatable
{
    protected $fillable = ['name', 'email', 'password', 'role'];
    
    public function orders()
    {
        return $this->hasMany(Order::class);
    }
    
    public function isAdmin()
    {
        return $this->role === 'admin';
    }
}
```

### Category Model
```php
class Category extends Model
{
    protected $fillable = ['name'];
    
    public function products()
    {
        return $this->hasMany(Product::class);
    }
}
```

### Product Model
```php
class Product extends Model
{
    protected $fillable = [
        'name', 'price', 'stock', 'description', 'image', 'category_id'
    ];
    
    public function category()
    {
        return $this->belongsTo(Category::class);
    }
    
    public function orderItems()
    {
        return $this->hasMany(OrderItem::class);
    }
    
    public function isInStock($quantity = 1)
    {
        return $this->stock >= $quantity;
    }
}
```

### Order Model
```php
class Order extends Model
{
    protected $fillable = ['user_id', 'total_price', 'status'];
    
    public function user()
    {
        return $this->belongsTo(User::class);
    }
    
    public function orderItems()
    {
        return $this->hasMany(OrderItem::class);
    }
}
```

### OrderItem Model
```php
class OrderItem extends Model
{
    protected $fillable = ['order_id', 'product_id', 'quantity', 'price'];
    
    public function order()
    {
        return $this->belongsTo(Order::class);
    }
    
    public function product()
    {
        return $this->belongsTo(Product::class);
    }
}
```

## Database Schema

### Migration Structure
```sql
-- users table
id (bigint, primary key)
name (varchar 255)
email (varchar 255, unique)
password (varchar 255)
role (enum: 'admin', 'user', default 'user')
created_at, updated_at

-- categories table
id (bigint, primary key)
name (varchar 255)
created_at, updated_at

-- products table
id (bigint, primary key)
name (varchar 255)
price (decimal 10,2)
stock (integer)
description (text)
image (varchar 255)
category_id (foreign key to categories.id)
created_at, updated_at

-- orders table
id (bigint, primary key)
user_id (foreign key to users.id)
total_price (decimal 10,2)
status (enum: 'pending', 'paid', 'completed', default 'pending')
created_at, updated_at

-- order_items table
id (bigint, primary key)
order_id (foreign key to orders.id)
product_id (foreign key to products.id)
quantity (integer)
price (decimal 10,2)
created_at, updated_at
```

## User Interface Design

### Design System
- **Color Palette:**
  - Primary: White (#FFFFFF)
  - Accent: Sporty Blue (#2563EB) or Green (#059669)
  - Text: Dark Gray (#374151)
  - Borders: Light Gray (#E5E7EB)

- **Typography:**
  - Headers: Bold, clean sans-serif
  - Body: Regular weight, readable font size
  - Buttons: Medium weight, clear labels

- **Layout Principles:**
  - Clean white backgrounds
  - Generous whitespace
  - Consistent spacing (8px grid system)
  - Simple navigation structure
  - Mobile-responsive design

### Component Design
- **Product Cards:** Clean borders, product image, name, price, stock status
- **Navigation:** Simple header with logo, main menu, cart icon, user menu
- **Forms:** Clear labels, validation messages, consistent button styling
- **Admin Interface:** Sidebar navigation, data tables, action buttons

## Error Handling

### Validation Strategy
- **Form Validation:** Laravel Form Requests for all user inputs
- **Business Logic Validation:** Model-level validation for data integrity
- **Stock Validation:** Check product availability before adding to cart/order
- **Authentication Validation:** Middleware for protected routes

### Error Response Patterns
- **User Errors:** Friendly messages with clear next steps
- **Admin Errors:** Detailed error information for troubleshooting
- **System Errors:** Graceful fallbacks with logging
- **Validation Errors:** Field-specific error messages

### Exception Handling
```php
// Stock validation example
if (!$product->isInStock($quantity)) {
    return back()->withErrors(['quantity' => 'Insufficient stock available']);
}

// Order processing error handling
try {
    DB::transaction(function () use ($cartItems, $user) {
        // Create order and order items
        // Update product stock
    });
} catch (Exception $e) {
    Log::error('Order processing failed: ' . $e->getMessage());
    return back()->withErrors(['order' => 'Order processing failed. Please try again.']);
}
```

## Testing Strategy

### Unit Testing
- **Model Tests:** Validate relationships, business logic methods
- **Service Tests:** Cart operations, order processing logic
- **Validation Tests:** Form request validation rules

### Feature Testing
- **Authentication Flow:** Registration, login, logout, role-based access
- **Product Management:** CRUD operations, image uploads
- **Shopping Flow:** Add to cart, checkout process, order creation
- **Admin Functions:** Dashboard statistics, order management

### Database Testing
- **Migration Tests:** Ensure all tables and relationships are created correctly
- **Seeder Tests:** Validate sample data creation
- **Factory Tests:** Confirm model factories generate valid data

### Integration Testing
- **Cart to Order Flow:** Complete shopping experience
- **Admin Order Management:** Status updates and data consistency
- **Stock Management:** Inventory updates during order processing

## Security Considerations

### Authentication & Authorization
- Password hashing using Laravel's built-in bcrypt
- CSRF protection on all forms
- Role-based middleware for admin access
- Session security with secure cookies

### Data Protection
- Input sanitization and validation
- SQL injection prevention through Eloquent ORM
- XSS protection in Blade templates
- File upload validation for product images

### Business Logic Security
- Stock validation to prevent overselling
- Order integrity checks
- User data isolation (users can only see their own orders)
- Admin action logging for audit trails