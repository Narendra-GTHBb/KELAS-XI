<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\ProductController;
use App\Http\Controllers\Api\CategoryController;
use App\Http\Controllers\Api\CartController;
use App\Http\Controllers\Api\OrderController;
use App\Http\Controllers\Api\DashboardApiController;

use App\Http\Controllers\Api\ShippingController;
use App\Http\Controllers\Api\ReviewController;
use App\Http\Controllers\Api\VoucherController;
use App\Http\Controllers\Api\NotificationController;
use App\Http\Controllers\Api\PointsController;

// Public API routes
Route::prefix('v1')->group(function () {
    // Authentication
    Route::post('/register', [AuthController::class, 'register']);
    Route::post('/login', [AuthController::class, 'login']);
    Route::post('/auth/google', [AuthController::class, 'loginWithGoogle']);

    // Public product data
    Route::get('/products', [ProductController::class, 'index']);
    Route::get('/products/{id}', [ProductController::class, 'show']);
    Route::get('/categories', [CategoryController::class, 'index']);
    Route::get('/categories/{id}/products', [CategoryController::class, 'products']);

    // Reviews — publik bisa baca
    Route::get('/products/{productId}/reviews', [ReviewController::class, 'index']);

    // Voucher — public validation (no auth needed)
    Route::post('/vouchers/apply', [VoucherController::class, 'apply']);

    // Shipping — public (no auth needed to browse couriers/costs)
    Route::get('/shipping/provinces', [ShippingController::class, 'provinces']);
    Route::get('/shipping/cities', [ShippingController::class, 'cities']);
    Route::get('/shipping/postal-code', [ShippingController::class, 'postalCode']);
    Route::post('/shipping/cost', [ShippingController::class, 'cost']);
});

// Protected API routes (require authentication)
Route::prefix('v1')->middleware('auth:sanctum')->group(function () {
    // User profile
    Route::get('/user', [AuthController::class, 'user']);
    Route::put('/user/profile', [AuthController::class, 'updateProfile']);
    Route::post('/user/fcm-token', [AuthController::class, 'registerFcmToken']);
    Route::post('/logout', [AuthController::class, 'logout']);
    
    // Cart management
    Route::get('/cart', [CartController::class, 'index']);
    Route::post('/cart/add', [CartController::class, 'add']);
    Route::put('/cart/update/{id}', [CartController::class, 'update']);
    Route::delete('/cart/remove/{id}', [CartController::class, 'remove']);
    Route::delete('/cart/clear', [CartController::class, 'clear']);
    
    // Order management
    Route::get('/orders', [OrderController::class, 'index']);
    Route::post('/orders', [OrderController::class, 'store']);
    Route::get('/orders/{id}', [OrderController::class, 'show']);
    Route::put('/orders/{id}/cancel', [OrderController::class, 'cancel']);
    Route::put('/orders/{id}/confirm-received', [OrderController::class, 'confirmReceived']);

    // Reviews — butuh auth untuk submit/cek
    Route::post('/reviews', [ReviewController::class, 'store']);
    Route::get('/reviews/check', [ReviewController::class, 'check']);

    // Loyalty Points
    Route::get('/points', [PointsController::class, 'index']);
    Route::post('/points/check', [PointsController::class, 'check']);

    // Notifications
    Route::get('/notifications', [NotificationController::class, 'index']);
    Route::get('/notifications/unread-count', [NotificationController::class, 'unreadCount']);
    Route::post('/notifications/read-all', [NotificationController::class, 'readAll']);
    Route::put('/notifications/{id}/read', [NotificationController::class, 'read']);

    // Dashboard real-time data
    Route::prefix('dashboard')->group(function () {
        Route::get('/stats', [DashboardApiController::class, 'getStats']);
        Route::get('/revenue-chart', [DashboardApiController::class, 'getRevenueChart']);
        Route::get('/order-volume-chart', [DashboardApiController::class, 'getOrderVolumeChart']);
        Route::get('/recent-orders', [DashboardApiController::class, 'getRecentOrders']);
        Route::get('/top-products', [DashboardApiController::class, 'getTopProducts']);
        Route::get('/low-stock-products', [DashboardApiController::class, 'getLowStockProducts']);
    });
});