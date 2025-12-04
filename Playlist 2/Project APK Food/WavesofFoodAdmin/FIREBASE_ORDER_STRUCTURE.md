# Firebase Firestore Database Structure for Waves of Food

## Collections Structure

### 1. Users Collection: `/users/{userId}`

```json
{
  "userId": "user_unique_id",
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+62812345678",
  "address": {
    "street": "Jl. Merdeka No. 10",
    "city": "Jakarta",
    "postalCode": "12345",
    "coordinates": {
      "lat": -6.2088,
      "lng": 106.8456
    }
  },
  "role": "user", // "user" or "admin"
  "createdAt": "2025-09-17T10:00:00Z",
  "isActive": true
}
```

### 2. Menu Items Collection: `/menuItems/{itemId}`

```json
{
  "itemId": "menu_item_id",
  "name": "Nasi Goreng Special",
  "description": "Nasi goreng dengan ayam, telur, dan sayuran segar",
  "price": 25000,
  "category": "rice", // "burger", "pizza", "sushi", "rice", "drink", "dessert"
  "imageUrl": "https://firebasestorage.../nasi-goreng.jpg",
  "isAvailable": true,
  "preparationTime": 15, // minutes
  "ingredients": ["rice", "chicken", "egg", "vegetables"],
  "allergens": ["egg"],
  "createdAt": "2025-09-17T10:00:00Z",
  "updatedAt": "2025-09-17T10:00:00Z"
}
```

### 3. Orders Collection: `/orders/{orderId}`

```json
{
  "orderId": "order_unique_id",
  "userId": "user_id",
  "userName": "John Doe",
  "userEmail": "john@example.com",
  "userPhone": "+62812345678",
  "status": "pending", // "pending", "confirmed", "preparing", "ready", "delivered", "cancelled"
  "orderItems": [
    {
      "itemId": "menu_item_id",
      "name": "Nasi Goreng Special",
      "price": 25000,
      "quantity": 2,
      "subtotal": 50000,
      "notes": "Extra pedas"
    }
  ],
  "pricing": {
    "subtotal": 50000,
    "deliveryFee": 5000,
    "tax": 5500,
    "total": 60500
  },
  "deliveryAddress": {
    "street": "Jl. Merdeka No. 10",
    "city": "Jakarta",
    "postalCode": "12345",
    "coordinates": {
      "lat": -6.2088,
      "lng": 106.8456
    },
    "notes": "Rumah warna biru, pagar putih"
  },
  "paymentMethod": "cash", // "cash", "transfer", "ewallet"
  "paymentStatus": "unpaid", // "unpaid", "paid", "refunded"
  "estimatedDeliveryTime": "2025-09-17T11:30:00Z",
  "actualDeliveryTime": null,
  "orderNotes": "Jangan terlalu pedas",
  "adminNotes": "", // Notes from admin
  "createdAt": "2025-09-17T10:00:00Z",
  "updatedAt": "2025-09-17T10:00:00Z",
  "statusHistory": [
    {
      "status": "pending",
      "timestamp": "2025-09-17T10:00:00Z",
      "by": "system"
    },
    {
      "status": "confirmed",
      "timestamp": "2025-09-17T10:05:00Z",
      "by": "admin_user_id"
    }
  ]
}
```

### 4. Order Status Tracking: `/orderTracking/{orderId}`

```json
{
  "orderId": "order_unique_id",
  "currentStatus": "preparing",
  "estimatedCompletionTime": "2025-09-17T11:30:00Z",
  "driverInfo": {
    "name": "Driver Name",
    "phone": "+62812345679",
    "vehicleNumber": "B 1234 ABC"
  },
  "trackingUpdates": [
    {
      "status": "confirmed",
      "message": "Pesanan dikonfirmasi dan sedang dipersiapkan",
      "timestamp": "2025-09-17T10:05:00Z"
    },
    {
      "status": "preparing",
      "message": "Makanan sedang dimasak",
      "timestamp": "2025-09-17T10:10:00Z"
    }
  ]
}
```

### 5. Admin Settings: `/adminSettings/config`

```json
{
  "deliveryFee": 5000,
  "taxRate": 0.1, // 10%
  "operatingHours": {
    "open": "08:00",
    "close": "22:00"
  },
  "maxDeliveryDistance": 10, // km
  "estimatedPrepTime": 20, // minutes
  "isAcceptingOrders": true
}
```

## Security Rules

- Users can only read/write their own data
- Orders can be read by users (owner) and admins
- Orders can only be updated by admins
- Menu items can be read by all, written by admins only
- Admin settings can only be accessed by admins

## Indexes Needed

- orders: userId, status, createdAt
- orders: status, createdAt
- menuItems: category, isAvailable
- users: email, role
