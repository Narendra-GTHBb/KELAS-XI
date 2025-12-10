# Firebase Database Structure untuk Admin Panel

## Collections yang dibutuhkan:

### 1. **users** (Collection)

```json
{
  "userId": {
    "id": "string",
    "name": "string",
    "email": "string",
    "phone": "string",
    "profileImageUrl": "string",
    "address": "string",
    "isActive": "boolean",
    "totalOrders": "number",
    "totalSpent": "number",
    "joinDate": "timestamp",
    "lastOrderDate": "timestamp",
    "fcmToken": "string"
  }
}
```

### 2. **restaurants** (Collection)

```json
{
  "restaurantId": {
    "id": "string",
    "name": "string",
    "description": "string",
    "imageUrl": "string",
    "address": "string",
    "phone": "string",
    "email": "string",
    "category": "string",
    "rating": "number",
    "isActive": "boolean",
    "isApproved": "boolean",
    "ownerId": "string",
    "ownerName": "string",
    "createdAt": "timestamp",
    "updatedAt": "timestamp",
    "coordinates": {
      "latitude": "number",
      "longitude": "number"
    },
    "businessHours": {
      "monday": { "open": "string", "close": "string" },
      "tuesday": { "open": "string", "close": "string" }
      // ... other days
    }
  }
}
```

### 3. **orders** (Collection)

```json
{
  "orderId": {
    "id": "string",
    "userId": "string",
    "userName": "string",
    "userPhone": "string",
    "restaurantId": "string",
    "restaurantName": "string",
    "items": [
      {
        "foodId": "string",
        "foodName": "string",
        "price": "number",
        "quantity": "number",
        "totalPrice": "number",
        "imageUrl": "string"
      }
    ],
    "totalAmount": "number",
    "status": "string", // PENDING, CONFIRMED, PREPARING, READY, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    "paymentMethod": "string",
    "paymentStatus": "string", // PENDING, PAID, FAILED, REFUNDED
    "deliveryAddress": "string",
    "deliveryFee": "number",
    "notes": "string",
    "orderDate": "timestamp",
    "deliveryTime": "timestamp",
    "completedAt": "timestamp",
    "estimatedDeliveryTime": "number"
  }
}
```

### 4. **foods** (Collection)

```json
{
  "foodId": {
    "id": "string",
    "name": "string",
    "description": "string",
    "price": "number",
    "imageUrl": "string",
    "category": "string",
    "restaurantId": "string",
    "restaurantName": "string",
    "isAvailable": "boolean",
    "isActive": "boolean",
    "ingredients": ["string"],
    "allergens": ["string"],
    "nutritionInfo": {
      "calories": "number",
      "protein": "number",
      "carbs": "number",
      "fat": "number"
    },
    "createdAt": "timestamp",
    "updatedAt": "timestamp"
  }
}
```

### 5. **admin_users** (Collection)

```json
{
  "adminId": {
    "id": "string",
    "name": "string",
    "email": "string",
    "role": "string", // SUPER_ADMIN, ADMIN, MODERATOR
    "permissions": ["string"],
    "isActive": "boolean",
    "createdAt": "timestamp",
    "lastLogin": "timestamp"
  }
}
```

### 6. **analytics** (Collection)

```json
{
  "date": {
    "date": "string", // YYYY-MM-DD
    "totalOrders": "number",
    "totalRevenue": "number",
    "totalUsers": "number",
    "totalRestaurants": "number",
    "activeOrders": "number",
    "completedOrders": "number",
    "cancelledOrders": "number",
    "averageOrderValue": "number",
    "topRestaurants": [
      {
        "id": "string",
        "name": "string",
        "totalOrders": "number",
        "totalRevenue": "number"
      }
    ],
    "ordersByStatus": {
      "PENDING": "number",
      "CONFIRMED": "number",
      "PREPARING": "number",
      "READY": "number",
      "OUT_FOR_DELIVERY": "number",
      "DELIVERED": "number",
      "CANCELLED": "number"
    }
  }
}
```

### 7. **notifications** (Collection)

```json
{
  "notificationId": {
    "id": "string",
    "title": "string",
    "message": "string",
    "type": "string", // ORDER_UPDATE, PROMOTION, SYSTEM
    "targetType": "string", // ALL_USERS, SPECIFIC_USER, RESTAURANT_OWNERS
    "targetId": "string",
    "imageUrl": "string",
    "actionType": "string",
    "actionData": "object",
    "isActive": "boolean",
    "sentAt": "timestamp",
    "createdBy": "string"
  }
}
```

## Firebase Security Rules

### Firestore Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users collection - users can read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
      allow read: if request.auth != null &&
        exists(/databases/$(database)/documents/admin_users/$(request.auth.uid));
    }

    // Restaurants collection - owners can manage their restaurant
    match /restaurants/{restaurantId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null &&
        (resource.data.ownerId == request.auth.uid ||
         exists(/databases/$(database)/documents/admin_users/$(request.auth.uid)));
    }

    // Orders collection
    match /orders/{orderId} {
      allow read: if request.auth != null &&
        (resource.data.userId == request.auth.uid ||
         resource.data.restaurantId in get(/databases/$(database)/documents/users/$(request.auth.uid)).data.restaurantIds ||
         exists(/databases/$(database)/documents/admin_users/$(request.auth.uid)));
      allow write: if request.auth != null &&
        (resource.data.userId == request.auth.uid ||
         exists(/databases/$(database)/documents/admin_users/$(request.auth.uid)));
    }

    // Foods collection
    match /foods/{foodId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null &&
        (resource.data.restaurantId in get(/databases/$(database)/documents/users/$(request.auth.uid)).data.restaurantIds ||
         exists(/databases/$(database)/documents/admin_users/$(request.auth.uid)));
    }

    // Admin only collections
    match /admin_users/{adminId} {
      allow read, write: if request.auth != null &&
        exists(/databases/$(database)/documents/admin_users/$(request.auth.uid));
    }

    match /analytics/{document=**} {
      allow read, write: if request.auth != null &&
        exists(/databases/$(database)/documents/admin_users/$(request.auth.uid));
    }

    match /notifications/{notificationId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null &&
        exists(/databases/$(database)/documents/admin_users/$(request.auth.uid));
    }
  }
}
```

### Storage Rules

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /users/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    match /restaurants/{restaurantId}/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null &&
        (request.auth.uid == resource.metadata.ownerId ||
         exists(/databases/(default)/documents/admin_users/$(request.auth.uid)));
    }

    match /foods/{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }

    match /admin/{allPaths=**} {
      allow read, write: if request.auth != null &&
        exists(/databases/(default)/documents/admin_users/$(request.auth.uid));
    }
  }
}
```

## Cloud Functions yang direkomendasikan:

1. **onOrderCreate** - Trigger notifikasi ke restaurant
2. **onOrderStatusUpdate** - Trigger notifikasi ke user
3. **updateAnalytics** - Update daily analytics
4. **sendBulkNotifications** - Kirim notifikasi massal
5. **approveRestaurant** - Approve restaurant dan kirim notifikasi
6. **generateDailyReport** - Generate laporan harian untuk admin
