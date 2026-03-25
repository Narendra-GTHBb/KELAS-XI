<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Category;
use App\Models\Order;
use App\Models\Product;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Carbon\Carbon;

class DashboardController extends Controller
{
    public function index()
    {
        // Calculate revenue with proper status filtering
        $totalRevenue = Order::whereIn('status', ['processing', 'shipped', 'delivered', 'success'])
            ->sum('total_amount');

        $stats = [
            'total_products' => Product::count(),
            'total_categories' => Category::count(),
            'total_orders' => Order::count(),
            'total_customers' => User::where('role', 'customer')->count(),
            'pending_orders' => Order::where('status', 'pending')->count(),
            'total_revenue' => $totalRevenue,
            'low_stock_products' => Product::where('stock_quantity', '<=', 10)->count(),
            'revenue_growth' => $this->calculateRevenueGrowth(),
            'orders_growth' => $this->calculateOrdersGrowth(),
            'customers_growth' => $this->calculateCustomersGrowth(),
        ];

        // Recent orders
        $recent_orders = Order::with(['user', 'orderItems.product'])
            ->orderBy('created_at', 'desc')
            ->limit(5)
            ->get();

        // Popular products (most ordered)
        $popular_products = Product::select('products.*')
            ->join('order_items', 'products.id', '=', 'order_items.product_id')
            ->join('orders', 'order_items.order_id', '=', 'orders.id')
            ->where('orders.status', '!=', 'cancelled')
            ->select('products.id', 'products.name', 'products.price', 'products.image_url', 
                    DB::raw('SUM(order_items.quantity) as total_sold'),
                    DB::raw('SUM(order_items.quantity * order_items.price) as total_revenue'))
            ->groupBy('products.id', 'products.name', 'products.price', 'products.image_url')
            ->orderBy('total_sold', 'desc')
            ->limit(5)
            ->get();

        // Chart data for initial page load
        $revenueChartData = $this->getLast7DaysRevenue();
        $orderVolumeChartData = $this->getTodayOrderVolume();

        return view('admin.dashboard', compact(
            'stats', 
            'recent_orders', 
            'popular_products',
            'revenueChartData',
            'orderVolumeChartData'
        ));
    }

    /**
     * Calculate revenue growth percentage
     */
    private function calculateRevenueGrowth()
    {
        $currentMonth = Order::whereIn('status', ['processing', 'shipped', 'delivered', 'success'])
            ->whereMonth('created_at', Carbon::now()->month)
            ->whereYear('created_at', Carbon::now()->year)
            ->sum('total_amount');

        $lastMonth = Order::whereIn('status', ['processing', 'shipped', 'delivered', 'success'])
            ->whereMonth('created_at', Carbon::now()->subMonth()->month)
            ->whereYear('created_at', Carbon::now()->subMonth()->year)
            ->sum('total_amount');

        if ($lastMonth > 0) {
            return round((($currentMonth - $lastMonth) / $lastMonth) * 100, 1);
        }

        return $currentMonth > 0 ? 100 : 0;
    }

    /**
     * Calculate orders growth percentage
     */
    private function calculateOrdersGrowth()
    {
        $currentMonth = Order::whereMonth('created_at', Carbon::now()->month)
            ->whereYear('created_at', Carbon::now()->year)
            ->count();

        $lastMonth = Order::whereMonth('created_at', Carbon::now()->subMonth()->month)
            ->whereYear('created_at', Carbon::now()->subMonth()->year)
            ->count();

        if ($lastMonth > 0) {
            return round((($currentMonth - $lastMonth) / $lastMonth) * 100, 1);
        }

        return $currentMonth > 0 ? 100 : 0;
    }

    /**
     * Calculate customers growth percentage
     */
    private function calculateCustomersGrowth()
    {
        $currentMonth = User::where('role', 'customer')
            ->whereMonth('created_at', Carbon::now()->month)
            ->whereYear('created_at', Carbon::now()->year)
            ->count();

        $lastMonth = User::where('role', 'customer')
            ->whereMonth('created_at', Carbon::now()->subMonth()->month)
            ->whereYear('created_at', Carbon::now()->subMonth()->year)
            ->count();

        if ($lastMonth > 0) {
            return round((($currentMonth - $lastMonth) / $lastMonth) * 100, 1);
        }

        return $currentMonth > 0 ? 100 : 0;
    }

    /**
     * Get last 7 days revenue for chart
     */
    private function getLast7DaysRevenue()
    {
        $labels = [];
        $data = [];

        for ($i = 6; $i >= 0; $i--) {
            $date = Carbon::now()->subDays($i);
            $labels[] = $date->format('D');
            
            $revenue = Order::whereIn('status', ['processing', 'shipped', 'delivered', 'success'])
                ->whereDate('created_at', $date)
                ->sum('total_amount');
            
            $data[] = round($revenue, 2);
        }

        return [
            'labels' => $labels,
            'data' => $data
        ];
    }

    /**
     * Get today's order volume by hour intervals
     */
    private function getTodayOrderVolume()
    {
        $labels = [];
        $data = [];

        // Group by 4-hour intervals
        $intervals = [
            ['start' => 0, 'end' => 3, 'label' => '00:00'],
            ['start' => 4, 'end' => 7, 'label' => '04:00'],
            ['start' => 8, 'end' => 11, 'label' => '08:00'],
            ['start' => 12, 'end' => 15, 'label' => '12:00'],
            ['start' => 16, 'end' => 19, 'label' => '16:00'],
            ['start' => 20, 'end' => 23, 'label' => '20:00'],
        ];

        foreach ($intervals as $interval) {
            $labels[] = $interval['label'];
            
            $count = Order::whereDate('created_at', Carbon::today())
                ->whereTime('created_at', '>=', sprintf('%02d:00:00', $interval['start']))
                ->whereTime('created_at', '<=', sprintf('%02d:59:59', $interval['end']))
                ->count();
            
            $data[] = $count;
        }

        return [
            'labels' => $labels,
            'data' => $data
        ];
    }
}

