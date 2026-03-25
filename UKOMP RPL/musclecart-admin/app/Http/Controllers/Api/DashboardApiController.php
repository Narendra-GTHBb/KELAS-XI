<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Order;
use App\Models\Product;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Carbon\Carbon;

class DashboardApiController extends Controller
{
    /**
     * Get real-time dashboard statistics
     */
    public function getStats()
    {
        $stats = [
            'total_revenue' => Order::whereIn('status', ['processing', 'shipped', 'delivered', 'success'])
                ->sum('total_amount'),
            'total_orders' => Order::count(),
            'pending_orders' => Order::where('status', 'pending')->count(),
            'low_stock_products' => Product::where('stock_quantity', '<=', 10)->count(),
            'total_customers' => User::where('role', 'customer')->count(),
            'revenue_growth' => $this->calculateRevenueGrowth(),
            'orders_growth' => $this->calculateOrdersGrowth(),
            'customers_growth' => $this->calculateCustomersGrowth(),
        ];

        return response()->json([
            'success' => true,
            'data' => $stats
        ]);
    }

    /**
     * Get revenue chart data
     */
    public function getRevenueChart(Request $request)
    {
        $period = $request->input('period', '7days');
        
        $data = $this->getRevenueData($period);

        return response()->json([
            'success' => true,
            'data' => $data
        ]);
    }

    /**
     * Get order volume chart data
     */
    public function getOrderVolumeChart(Request $request)
    {
        $period = $request->input('period', 'today');
        
        if ($period === 'today') {
            $data = $this->getTodayOrderVolume();
        } else {
            $data = $this->getWeeklyOrderVolume();
        }

        return response()->json([
            'success' => true,
            'data' => $data
        ]);
    }

    /**
     * Get recent orders
     */
    public function getRecentOrders()
    {
        $orders = Order::with(['user'])
            ->orderBy('created_at', 'desc')
            ->limit(5)
            ->get()
            ->map(function ($order) {
                return [
                    'id' => $order->id,
                    'customer_name' => $order->user->name ?? 'Guest',
                    'total_amount' => $order->total_amount,
                    'status' => $order->status,
                    'created_at' => $order->created_at->format('M d, Y'),
                ];
            });

        return response()->json([
            'success' => true,
            'data' => $orders
        ]);
    }

    /**
     * Get top selling products
     */
    public function getTopProducts()
    {
        $products = Product::select('products.*')
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

        return response()->json([
            'success' => true,
            'data' => $products
        ]);
    }

    /**
     * Get low stock products
     */
    public function getLowStockProducts()
    {
        $products = Product::where('stock_quantity', '<=', 10)
            ->orderBy('stock_quantity', 'asc')
            ->limit(10)
            ->get(['id', 'name', 'stock_quantity', 'price', 'image_url']);

        return response()->json([
            'success' => true,
            'data' => $products
        ]);
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
     * Get revenue data for different periods
     */
    private function getRevenueData($period)
    {
        switch ($period) {
            case '7days':
                return $this->getLast7DaysRevenue();
            case '30days':
                return $this->getLast30DaysRevenue();
            case '3months':
                return $this->getLast3MonthsRevenue();
            default:
                return $this->getLast7DaysRevenue();
        }
    }

    /**
     * Get last 7 days revenue
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
     * Get last 30 days revenue
     */
    private function getLast30DaysRevenue()
    {
        $labels = [];
        $data = [];

        for ($i = 29; $i >= 0; $i--) {
            $date = Carbon::now()->subDays($i);
            $labels[] = $date->format('M j');
            
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
     * Get last 3 months revenue
     */
    private function getLast3MonthsRevenue()
    {
        $labels = [];
        $data = [];

        for ($i = 2; $i >= 0; $i--) {
            $date = Carbon::now()->subMonths($i);
            $labels[] = $date->format('M Y');
            
            $revenue = Order::whereIn('status', ['processing', 'shipped', 'delivered', 'success'])
                ->whereMonth('created_at', $date->month)
                ->whereYear('created_at', $date->year)
                ->sum('total_amount');
            
            $data[] = round($revenue, 2);
        }

        return [
            'labels' => $labels,
            'data' => $data
        ];
    }

    /**
     * Get today's order volume by hour
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

    /**
     * Get weekly order volume
     */
    private function getWeeklyOrderVolume()
    {
        $labels = [];
        $data = [];

        for ($i = 6; $i >= 0; $i--) {
            $date = Carbon::now()->subDays($i);
            $labels[] = $date->format('D');
            
            $count = Order::whereDate('created_at', $date)->count();
            $data[] = $count;
        }

        return [
            'labels' => $labels,
            'data' => $data
        ];
    }
}
