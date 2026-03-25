<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Auth\LoginController;
use App\Http\Controllers\Admin\DashboardController;
use App\Http\Controllers\Admin\ProductController;
use App\Http\Controllers\Admin\CategoryController;
use App\Http\Controllers\Admin\OrderController;
use App\Http\Controllers\Admin\UserController;
use App\Http\Controllers\Admin\CustomerController;
use App\Http\Controllers\Api\DashboardApiController;
// Public routes
Route::get('/', function () {
    return redirect('/admin/dashboard');
});

// Authentication routes
Route::get('/login', [LoginController::class, 'showLoginForm'])->name('login');
Route::post('/login', [LoginController::class, 'login']);
Route::post('/logout', [LoginController::class, 'logout'])->name('logout');

// Admin routes (protected by auth middleware)
Route::prefix('admin')->name('admin.')->middleware(['auth'])->group(function () {
    // Dashboard
    Route::get('/', [DashboardController::class, 'index'])->name('dashboard');
    Route::get('/dashboard', [DashboardController::class, 'index'])->name('dashboard.index');
    
    // Products management
    Route::resource('products', ProductController::class);
    
    // Categories management  
    Route::resource('categories', CategoryController::class);
    
    // Orders management
    Route::resource('orders', OrderController::class);
    
    // Customers management
    Route::resource('customers', CustomerController::class);
    
    // Users management
    Route::resource('users', UserController::class);
    
    // API routes for modal
    Route::get('/api/categories', [CategoryController::class, 'apiIndex'])->name('api.categories');
    
    // Dashboard real-time API routes
    Route::prefix('api/dashboard')->name('api.dashboard.')->group(function () {
        Route::get('/stats', [DashboardApiController::class, 'getStats'])->name('stats');
        Route::get('/revenue-chart', [DashboardApiController::class, 'getRevenueChart'])->name('revenue-chart');
        Route::get('/order-volume-chart', [DashboardApiController::class, 'getOrderVolumeChart'])->name('order-volume-chart');
        Route::get('/recent-orders', [DashboardApiController::class, 'getRecentOrders'])->name('recent-orders');
        Route::get('/top-products', [DashboardApiController::class, 'getTopProducts'])->name('top-products');
        Route::get('/low-stock-products', [DashboardApiController::class, 'getLowStockProducts'])->name('low-stock-products');
    });
});
