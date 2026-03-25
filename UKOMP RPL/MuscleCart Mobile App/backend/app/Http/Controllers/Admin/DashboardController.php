<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Category;
use App\Models\Order;
use App\Models\Product;
use App\Models\User;
use Illuminate\Http\Request;

class DashboardController extends Controller
{
    public function index()
    {
        $stats = [
            'total_products' => Product::count(),
            'total_categories' => Category::count(),
            'total_orders' => Order::count(),
            'total_customers' => User::where('role', 'customer')->count(),
            'pending_orders' => Order::where('status', 'pending')->count(),
            'total_revenue' => Order::where('payment_status', 'paid')->sum('total_amount'),
            'low_stock_products' => Product::where('stock_quantity', '<=', 10)->count(),
        ];

        // Recent orders
        $recent_orders = Order::with(['user', 'orderItems.product'])
            ->orderBy('created_at', 'desc')
            ->limit(5)
            ->get();

        // Popular products (most ordered)
        $popular_products = Product::withCount(['orderItems' => function($query) {
            $query->whereHas('order', function($q) {
                $q->where('status', '!=', 'cancelled');
            });
        }])
        ->orderBy('order_items_count', 'desc')
        ->limit(5)
        ->get();

        return view('admin.dashboard', compact('stats', 'recent_orders', 'popular_products'));
    }
}
