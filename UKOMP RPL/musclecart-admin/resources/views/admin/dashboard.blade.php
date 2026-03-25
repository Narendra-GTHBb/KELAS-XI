@extends('admin.layouts.app')

@section('title', 'Dashboard - MuscleCart Admin')

@section('content')
<!-- Page Header -->
<div class="mb-8">
    <h1 class="text-3xl font-bold text-gray-900 font-inter">Dashboard</h1>
    <p class="text-gray-600 font-inter mt-1">Welcome back! Here's what's happening with MuscleCart today.</p>
</div>
<!-- Stats Cards -->
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-6 mb-8">
    <!-- Total Revenue -->
    <div class="bg-white rounded-xl shadow-sm border metric-card">
        <div class="p-6">
            <div class="flex items-center justify-between">
                <div>
                    <div class="flex items-center mb-2">
                        <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center mr-3">
                            <i class="fas fa-dollar-sign text-blue-600"></i>
                        </div>
                        <span id="revenue-growth" class="text-xs font-semibold {{ ($stats['revenue_growth'] ?? 0) >= 0 ? 'text-green-600 bg-green-50' : 'text-red-600 bg-red-50' }} px-2 py-1 rounded-full font-inter">
                            {{ ($stats['revenue_growth'] ?? 0) >= 0 ? '+' : '' }}{{ number_format($stats['revenue_growth'] ?? 0, 1) }}%
                        </span>
                    </div>
                    <p class="text-gray-500 text-sm font-medium font-inter uppercase tracking-wider">TOTAL REVENUE</p>
                    <p id="total-revenue" class="text-2xl font-bold text-gray-800 mt-1 font-inter">Rp{{ number_format($stats['total_revenue'] ?? 0, 0, ',', '.') }}</p>
                </div>
            </div>
        </div>
    </div>

    <!-- Total Orders -->
    <div class="bg-white rounded-xl shadow-sm border metric-card">
        <div class="p-6">
            <div class="flex items-center justify-between">
                <div>
                    <div class="flex items-center mb-2">
                        <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center mr-3">
                            <i class="fas fa-shopping-cart text-blue-600"></i>
                        </div>
                        <span id="orders-growth" class="text-xs font-semibold {{ ($stats['orders_growth'] ?? 0) >= 0 ? 'text-green-600 bg-green-50' : 'text-red-600 bg-red-50' }} px-2 py-1 rounded-full font-inter">
                            {{ ($stats['orders_growth'] ?? 0) >= 0 ? '+' : '' }}{{ number_format($stats['orders_growth'] ?? 0, 1) }}%
                        </span>
                    </div>
                    <p class="text-gray-500 text-sm font-medium font-inter uppercase tracking-wider">TOTAL ORDERS</p>
                    <p id="total-orders" class="text-2xl font-bold text-gray-800 mt-1 font-inter">{{ $stats['total_orders'] ?? 0 }}</p>
                </div>
            </div>
        </div>
    </div>

    <!-- Pending Orders -->
    <div class="bg-white rounded-xl shadow-sm border metric-card">
        <div class="p-6">
            <div class="flex items-center justify-between">
                <div>
                    <div class="flex items-center mb-2">
                        <div class="w-10 h-10 bg-orange-100 rounded-lg flex items-center justify-center mr-3">
                            <i class="fas fa-clock text-orange-600"></i>
                        </div>
                        <span class="text-xs font-semibold text-gray-500 bg-gray-50 px-2 py-1 rounded-full font-inter">0%</span>
                    </div>
                    <p class="text-gray-500 text-sm font-medium font-inter uppercase tracking-wider">PENDING ORDERS</p>
                    <p id="pending-orders" class="text-2xl font-bold text-gray-800 mt-1 font-inter">{{ $stats['pending_orders'] ?? 0 }}</p>
                </div>
            </div>
        </div>
    </div>

    <!-- Low Stock -->
    <div class="bg-white rounded-xl shadow-sm border metric-card">
        <div class="p-6">
            <div class="flex items-center justify-between">
                <div>
                    <div class="flex items-center mb-2">
                        <div class="w-10 h-10 bg-red-100 rounded-lg flex items-center justify-center mr-3">
                            <i class="fas fa-exclamation-triangle text-red-600"></i>
                        </div>
                        <span class="text-xs font-semibold {{ ($stats['low_stock_products'] ?? 0) > 0 ? 'text-red-600 bg-red-50' : 'text-gray-500 bg-gray-50' }} px-2 py-1 rounded-full font-inter">
                            {{ ($stats['low_stock_products'] ?? 0) > 0 ? 'Alert' : 'OK' }}
                        </span>
                    </div>
                    <p class="text-gray-500 text-sm font-medium font-inter uppercase tracking-wider">LOW STOCK</p>
                    <p id="low-stock" class="text-2xl font-bold text-gray-800 mt-1 font-inter">{{ $stats['low_stock_products'] ?? 0 }}</p>
                </div>
            </div>
        </div>
    </div>

    <!-- Total Customers -->
    <div class="bg-white rounded-xl shadow-sm border metric-card">
        <div class="p-6">
            <div class="flex items-center justify-between">
                <div>
                    <div class="flex items-center mb-2">
                        <div class="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center mr-3">
                            <i class="fas fa-users text-green-600"></i>
                        </div>
                        <span id="customers-growth" class="text-xs font-semibold {{ ($stats['customers_growth'] ?? 0) >= 0 ? 'text-green-600 bg-green-50' : 'text-red-600 bg-red-50' }} px-2 py-1 rounded-full font-inter">
                            {{ ($stats['customers_growth'] ?? 0) >= 0 ? '+' : '' }}{{ number_format($stats['customers_growth'] ?? 0, 1) }}%
                        </span>
                    </div>
                    <p class="text-gray-500 text-sm font-medium font-inter uppercase tracking-wider">TOTAL CUSTOMERS</p>
                    <p id="total-customers" class="text-2xl font-bold text-gray-800 mt-1 font-inter">{{ $stats['total_customers'] ?? 0 }}</p>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Charts Section -->
<div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
    <!-- Revenue Over Time -->
    <div class="bg-white rounded-xl shadow-sm border">
        <div class="p-6 border-b border-gray-100">
            <div class="flex items-center justify-between">
                <div>
                    <h3 class="text-lg font-semibold text-gray-800 font-inter">Revenue Over Time</h3>
                    <p class="text-sm text-gray-500 font-inter">Earnings performance this week</p>
                </div>
                <select id="revenue-period-select" onchange="onRevenuePeriodChange(this.value)" class="text-sm border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 font-inter">
                    <option value="7days">Last 7 Days</option>
                    <option value="30days">Last 30 Days</option>
                    <option value="3months">Last 3 Months</option>
                </select>
            </div>
        </div>
        <div class="p-6">
            <div class="h-64 flex items-center justify-center">
                <canvas id="revenueChart" width="400" height="200"></canvas>
            </div>
        </div>
    </div>

    <!-- Order Volume -->
    <div class="bg-white rounded-xl shadow-sm border">
        <div class="p-6 border-b border-gray-100">
            <div class="flex items-center justify-between">
                <div>
                    <h3 class="text-lg font-semibold text-gray-800 font-inter">Order Volume</h3>
                    <p class="text-sm text-gray-500 font-inter">Orders distributed by hours</p>
                </div>
                <div class="flex items-center space-x-2">
                    <span class="text-sm font-medium text-red-500 font-inter">-4.1% today</span>
                </div>
            </div>
        </div>
        <div class="p-6">
            <div class="h-64 flex items-center justify-center">
                <canvas id="orderVolumeChart" width="400" height="200"></canvas>
            </div>
        </div>
    </div>
</div>

<!-- Recent Orders -->
<div class="bg-white rounded-xl shadow-sm border">
    <div class="p-6 border-b border-gray-100">
        <div class="flex items-center justify-between">
            <h3 class="text-lg font-semibold text-gray-800 font-inter">Recent Orders</h3>
            <a href="{{ route('admin.orders.index') }}" class="text-blue-600 hover:text-blue-800 text-sm font-medium font-inter">View All Orders</a>
        </div>
    </div>
    <div class="overflow-x-auto">
        <table id="recent-orders-table" class="w-full">
            <thead>
                <tr class="border-b border-gray-100">
                    <th class="text-left py-4 px-6 text-sm font-semibold text-gray-500 uppercase tracking-wider font-inter">ORDER ID</th>
                    <th class="text-left py-4 px-6 text-sm font-semibold text-gray-500 uppercase tracking-wider font-inter">CUSTOMER</th>
                    <th class="text-left py-4 px-6 text-sm font-semibold text-gray-500 uppercase tracking-wider font-inter">TOTAL AMOUNT</th>
                    <th class="text-left py-4 px-6 text-sm font-semibold text-gray-500 uppercase tracking-wider font-inter">STATUS</th>
                    <th class="text-left py-4 px-6 text-sm font-semibold text-gray-500 uppercase tracking-wider font-inter">DATE</th>
                    <th class="text-left py-4 px-6 text-sm font-semibold text-gray-500 uppercase tracking-wider font-inter">ACTION</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
                @if(isset($recent_orders) && $recent_orders->count() > 0)
                    @foreach($recent_orders as $order)
                        <tr class="hover:bg-gray-50 transition-colors">
                            <td class="py-4 px-6">
                                <div class="flex items-center">
                                    <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center mr-3">
                                        <i class="fas fa-hashtag text-blue-600 text-xs"></i>
                                    </div>
                                    <span class="font-semibold text-gray-800 font-inter">#{{ $order->id ?? '8821' }}</span>
                                </div>
                            </td>
                            <td class="py-4 px-6">
                                <div class="flex items-center">
                                    <img src="https://ui-avatars.com/api/?name={{ $order->user->name ?? 'John Doe' }}&background=3b82f6&color=fff" alt="User" class="w-8 h-8 rounded-full mr-3">
                                    <span class="font-semibold text-gray-800 font-inter">{{ $order->user->name ?? 'John Doe' }}</span>
                                </div>
                            </td>
                            <td class="py-4 px-6">
                                <span class="font-semibold text-gray-800 font-inter">Rp{{ number_format(($order->total_amount ?? 0), 0, ',', '.') }}</span>
                            </td>
                            <td class="py-4 px-6">
                                <span class="inline-block px-3 py-1 text-xs font-semibold rounded-full font-inter
                                    @if(($order->status ?? 'SUCCESS') == 'PENDING') bg-yellow-100 text-yellow-800
                                    @elseif(($order->status ?? 'SUCCESS') == 'PROCESSING') bg-blue-100 text-blue-800
                                    @elseif(($order->status ?? 'SUCCESS') == 'SHIPPED') bg-purple-100 text-purple-800
                                    @elseif(($order->status ?? 'SUCCESS') == 'SUCCESS' || ($order->status ?? 'SUCCESS') == 'DELIVERED') bg-green-100 text-green-800
                                    @elseif(($order->status ?? 'SUCCESS') == 'CANCELLED') bg-red-100 text-red-800
                                    @else bg-gray-100 text-gray-800
                                    @endif">
                                    {{ $order->status ?? 'Success' }}
                                </span>
                            </td>
                            <td class="py-4 px-6">
                                <span class="text-gray-600 text-sm font-inter">{{ $order->created_at ? $order->created_at->format('M d, Y') : 'Oct 24, 2023' }}</span>
                            </td>
                            <td class="py-4 px-6">
                                <div class="flex items-center space-x-2">
                                    <button class="text-gray-400 hover:text-gray-600">
                                        <i class="fas fa-ellipsis-h"></i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    @endforeach
                @else
                    <!-- Sample Data untuk Demo -->
                    <tr class="hover:bg-gray-50 transition-colors">
                        <td class="py-4 px-6">
                            <div class="flex items-center">
                                <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center mr-3">
                                    <i class="fas fa-hashtag text-blue-600 text-xs"></i>
                                </div>
                                <span class="font-semibold text-gray-800 font-inter">#8821</span>
                            </div>
                        </td>
                        <td class="py-4 px-6">
                            <div class="flex items-center">
                                <img src="https://ui-avatars.com/api/?name=John+Doe&background=3b82f6&color=fff" alt="User" class="w-8 h-8 rounded-full mr-3">
                                <span class="font-semibold text-gray-800 font-inter">John Doe</span>
                            </div>
                        </td>
                        <td class="py-4 px-6">
                            <span class="font-semibold text-gray-800 font-inter">Rp120.000</span>
                        </td>
                        <td class="py-4 px-6">
                            <span class="inline-block px-3 py-1 text-xs font-medium rounded-full bg-green-100 text-green-800">Success</span>
                        </td>
                        <td class="py-4 px-6">
                            <span class="text-gray-600 text-sm font-inter">Oct 24, 2023</span>
                        </td>
                        <td class="py-4 px-6">
                            <button class="text-gray-400 hover:text-gray-600">
                                <i class="fas fa-ellipsis-h"></i>
                            </button>
                        </td>
                    </tr>
                    <tr class="hover:bg-gray-50 transition-colors">
                        <td class="py-4 px-6">
                            <div class="flex items-center">
                                <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center mr-3">
                                    <i class="fas fa-hashtag text-blue-600 text-xs"></i>
                                </div>
                                <span class="font-semibold text-gray-800 font-inter">#8820</span>
                            </div>
                        </td>
                        <td class="py-4 px-6">
                            <div class="flex items-center">
                                <img src="https://ui-avatars.com/api/?name=Sarah+Miller&background=f59e0b&color=fff" alt="User" class="w-8 h-8 rounded-full mr-3">
                                <span class="font-semibold text-gray-800 font-inter">Sarah Miller</span>
                            </div>
                        </td>
                        <td class="py-4 px-6">
                            <span class="font-semibold text-gray-800 font-inter">Rp345.500</span>
                        </td>
                        <td class="py-4 px-6">
                            <span class="inline-block px-3 py-1 text-xs font-semibold rounded-full bg-yellow-100 text-yellow-800 font-inter">Pending</span>
                        </td>
                        <td class="py-4 px-6">
                            <span class="text-gray-600 text-sm font-inter">Oct 23, 2023</span>
                        </td>
                        <td class="py-4 px-6">
                            <button class="text-gray-400 hover:text-gray-600">
                                <i class="fas fa-ellipsis-h"></i>
                            </button>
                        </td>
                    </tr>
                    <tr class="hover:bg-gray-50 transition-colors">
                        <td class="py-4 px-6">
                            <div class="flex items-center">
                                <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center mr-3">
                                    <i class="fas fa-hashtag text-blue-600 text-xs"></i>
                                </div>
                                <span class="font-semibold text-gray-800 font-inter">#8819</span>
                            </div>
                        </td>
                        <td class="py-4 px-6">
                            <div class="flex items-center">
                                <img src="https://ui-avatars.com/api/?name=Mike+Ross&background=ef4444&color=fff" alt="User" class="w-8 h-8 rounded-full mr-3">
                                <span class="font-semibold text-gray-800 font-inter">Mike Ross</span>
                            </div>
                        </td>
                        <td class="py-4 px-6">
                            <span class="font-semibold text-gray-800 font-inter">Rp89.000</span>
                        </td>
                        <td class="py-4 px-6">
                            <span class="inline-block px-3 py-1 text-xs font-semibold rounded-full bg-red-100 text-red-800 font-inter">Cancelled</span>
                        </td>
                        <td class="py-4 px-6">
                            <span class="text-gray-600 text-sm font-inter">Oct 23, 2023</span>
                        </td>
                        <td class="py-4 px-6">
                            <button class="text-gray-400 hover:text-gray-600">
                                <i class="fas fa-ellipsis-h"></i>
                            </button>
                        </td>
                    </tr>
                    <tr class="hover:bg-gray-50 transition-colors">
                        <td class="py-4 px-6">
                            <div class="flex items-center">
                                <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center mr-3">
                                    <i class="fas fa-hashtag text-blue-600 text-xs"></i>
                                </div>
                                <span class="font-semibold text-gray-800 font-inter">#8818</span>
                            </div>
                        </td>
                        <td class="py-4 px-6">
                            <div class="flex items-center">
                                <img src="https://ui-avatars.com/api/?name=Elena+Gilbert&background=10b981&color=fff" alt="User" class="w-8 h-8 rounded-full mr-3">
                                <span class="font-semibold text-gray-800 font-inter">Elena Gilbert</span>
                            </div>
                        </td>
                        <td class="py-4 px-6">
                            <span class="font-semibold text-gray-800 font-inter">Rp560.250</span>
                        </td>
                        <td class="py-4 px-6">
                            <span class="inline-block px-3 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800 font-inter">Success</span>
                        </td>
                        <td class="py-4 px-6">
                            <span class="text-gray-600 text-sm font-inter">Oct 22, 2023</span>
                        </td>
                        <td class="py-4 px-6">
                            <button class="text-gray-400 hover:text-gray-600">
                                <i class="fas fa-ellipsis-h"></i>
                            </button>
                        </td>
                    </tr>
                @endif
            </tbody>
        </table>
    </div>
</div>

<script>
// Dashboard Real-time Update System
let revenueChart, orderVolumeChart;

document.addEventListener('DOMContentLoaded', function() {
    // Initialize charts with server data
    initializeCharts();
    
    // Setup auto-refresh for dashboard stats
    setInterval(updateDashboardStats, 30000); // Update every 30 seconds
    
    // Setup auto-refresh for charts
    setInterval(updateCharts, 60000); // Update charts every 60 seconds
    
    // Setup auto-refresh for recent orders
    setInterval(updateRecentOrders, 45000); // Update orders every 45 seconds
});

/**
 * Initialize charts with initial data from server
 */
function initializeCharts() {
    const revenueData = @json($revenueChartData ?? ['labels' => [], 'data' => []]);
    const orderVolumeData = @json($orderVolumeChartData ?? ['labels' => [], 'data' => []]);
    
    // Revenue Chart
    const revenueCtx = document.getElementById('revenueChart').getContext('2d');
    revenueChart = new Chart(revenueCtx, {
        type: 'line',
        data: {
            labels: revenueData.labels,
            datasets: [{
                label: 'Revenue',
                data: revenueData.data,
                borderColor: '#3B82F6',
                backgroundColor: 'rgba(59, 130, 246, 0.1)',
                fill: true,
                tension: 0.4,
                pointBackgroundColor: '#3B82F6',
                pointBorderColor: '#ffffff',
                pointBorderWidth: 2,
                pointRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return 'Revenue: Rp' + Math.round(context.parsed.y).toLocaleString('id-ID');
                        }
                    }
                }
            },
            elements: {
                line: {
                    tension: 0.4
                }
            },
            scales: {
                x: {
                    grid: {
                        display: false
                    },
                    border: {
                        display: false
                    }
                },
                y: {
                    grid: {
                        color: '#f3f4f6'
                    },
                    border: {
                        display: false
                    },
                    ticks: {
                        callback: function(value) {
                            if (value >= 1000000) {
                                return 'Rp' + (value / 1000000).toFixed(1) + 'jt';
                            } else if (value >= 1000) {
                                return 'Rp' + (value / 1000) + 'rb';
                            }
                            return 'Rp' + value;
                        }
                    }
                }
            }
        }
    });

    // Order Volume Chart
    const orderVolumeCtx = document.getElementById('orderVolumeChart').getContext('2d');
    orderVolumeChart = new Chart(orderVolumeCtx, {
        type: 'bar',
        data: {
            labels: orderVolumeData.labels,
            datasets: [{
                label: 'Orders',
                data: orderVolumeData.data,
                backgroundColor: '#93C5FD',
                borderRadius: 4,
                borderSkipped: false,
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                x: {
                    grid: {
                        display: false
                    },
                    border: {
                        display: false
                    }
                },
                y: {
                    grid: {
                        color: '#f3f4f6'
                    },
                    border: {
                        display: false
                    },
                    ticks: {
                        stepSize: 1
                    }
                }
            }
        }
    });
}

/**
 * Update dashboard statistics in real-time
 */
async function updateDashboardStats() {
    try {
        const response = await fetch('/admin/api/dashboard/stats');
        const result = await response.json();
        
        if (result.success) {
            const stats = result.data;
            
            // Update revenue (IDR format)
            document.getElementById('total-revenue').textContent = 'Rp' + Math.round(parseFloat(stats.total_revenue)).toLocaleString('id-ID');
            
            // Update revenue growth
            const revenueGrowthEl = document.getElementById('revenue-growth');
            const revenueGrowth = parseFloat(stats.revenue_growth);
            revenueGrowthEl.textContent = (revenueGrowth >= 0 ? '+' : '') + revenueGrowth.toFixed(1) + '%';
            revenueGrowthEl.className = 'text-xs font-semibold px-2 py-1 rounded-full font-inter ' + 
                (revenueGrowth >= 0 ? 'text-green-600 bg-green-50' : 'text-red-600 bg-red-50');
            
            // Update orders
            document.getElementById('total-orders').textContent = stats.total_orders;
            
            const ordersGrowthEl = document.getElementById('orders-growth');
            const ordersGrowth = parseFloat(stats.orders_growth);
            ordersGrowthEl.textContent = (ordersGrowth >= 0 ? '+' : '') + ordersGrowth.toFixed(1) + '%';
            ordersGrowthEl.className = 'text-xs font-semibold px-2 py-1 rounded-full font-inter ' + 
                (ordersGrowth >= 0 ? 'text-green-600 bg-green-50' : 'text-red-600 bg-red-50');
            
            // Update pending orders
            document.getElementById('pending-orders').textContent = stats.pending_orders;
            
            // Update low stock
            document.getElementById('low-stock').textContent = stats.low_stock_products;
            
            // Update customers
            document.getElementById('total-customers').textContent = stats.total_customers;
            
            const customersGrowthEl = document.getElementById('customers-growth');
            const customersGrowth = parseFloat(stats.customers_growth);
            customersGrowthEl.textContent = (customersGrowth >= 0 ? '+' : '') + customersGrowth.toFixed(1) + '%';
            customersGrowthEl.className = 'text-xs font-semibold px-2 py-1 rounded-full font-inter ' + 
                (customersGrowth >= 0 ? 'text-green-600 bg-green-50' : 'text-red-600 bg-red-50');
        }
    } catch (error) {
        console.error('Error updating dashboard stats:', error);
    }
}

/**
 * Update charts with fresh data
 */
async function updateCharts() {
    try {
        // Get revenue chart period
        const revenuePeriod = document.querySelector('#revenue-period-select')?.value || '7days';
        
        // Update revenue chart
        const revenueResponse = await fetch(`/admin/api/dashboard/revenue-chart?period=${revenuePeriod}`);
        const revenueResult = await revenueResponse.json();
        
        if (revenueResult.success && revenueChart) {
            revenueChart.data.labels = revenueResult.data.labels;
            revenueChart.data.datasets[0].data = revenueResult.data.data;
            revenueChart.update();
        }
        
        // Update order volume chart
        const volumeResponse = await fetch('/admin/api/dashboard/order-volume-chart?period=today');
        const volumeResult = await volumeResponse.json();
        
        if (volumeResult.success && orderVolumeChart) {
            orderVolumeChart.data.labels = volumeResult.data.labels;
            orderVolumeChart.data.datasets[0].data = volumeResult.data.data;
            orderVolumeChart.update();
        }
    } catch (error) {
        console.error('Error updating charts:', error);
    }
}

/**
 * Update recent orders table
 */
async function updateRecentOrders() {
    try {
        const response = await fetch('/admin/api/dashboard/recent-orders');
        const result = await response.json();
        
        if (result.success && result.data.length > 0) {
            const tbody = document.querySelector('#recent-orders-table tbody');
            if (tbody) {
                tbody.innerHTML = result.data.map(order => `
                    <tr class="hover:bg-gray-50 transition-colors">
                        <td class="py-4 px-6">
                            <div class="flex items-center">
                                <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center mr-3">
                                    <i class="fas fa-hashtag text-blue-600 text-xs"></i>
                                </div>
                                <span class="font-semibold text-gray-800 font-inter">#${order.id}</span>
                            </div>
                        </td>
                        <td class="py-4 px-6">
                            <div class="flex items-center">
                                <img src="https://ui-avatars.com/api/?name=${encodeURIComponent(order.customer_name)}&background=3b82f6&color=fff" alt="User" class="w-8 h-8 rounded-full mr-3">
                                <span class="font-semibold text-gray-800 font-inter">${order.customer_name}</span>
                            </div>
                        </td>
                        <td class="py-4 px-6">
                            <span class="font-semibold text-gray-800 font-inter">Rp${Math.round(parseFloat(order.total_amount)).toLocaleString('id-ID')}</span>
                        </td>
                        <td class="py-4 px-6">
                            <span class="inline-block px-3 py-1 text-xs font-semibold rounded-full font-inter ${getStatusClass(order.status)}">
                                ${order.status}
                            </span>
                        </td>
                        <td class="py-4 px-6">
                            <span class="text-gray-600 text-sm font-inter">${order.created_at}</span>
                        </td>
                        <td class="py-4 px-6">
                            <button class="text-gray-400 hover:text-gray-600">
                                <i class="fas fa-ellipsis-h"></i>
                            </button>
                        </td>
                    </tr>
                `).join('');
            }
        }
    } catch (error) {
        console.error('Error updating recent orders:', error);
    }
}

/**
 * Get status badge class based on order status
 */
function getStatusClass(status) {
    const statusLower = status.toLowerCase();
    if (statusLower === 'pending') return 'bg-yellow-100 text-yellow-800';
    if (statusLower === 'processing') return 'bg-blue-100 text-blue-800';
    if (statusLower === 'shipped') return 'bg-purple-100 text-purple-800';
    if (statusLower === 'success' || statusLower === 'delivered') return 'bg-green-100 text-green-800';
    if (statusLower === 'cancelled') return 'bg-red-100 text-red-800';
    return 'bg-gray-100 text-gray-800';
}

/**
 * Handle revenue chart period change
 */
function onRevenuePeriodChange(period) {
    fetch(`/admin/api/dashboard/revenue-chart?period=${period}`)
        .then(response => response.json())
        .then(result => {
            if (result.success && revenueChart) {
                revenueChart.data.labels = result.data.labels;
                revenueChart.data.datasets[0].data = result.data.data;
                revenueChart.update();
            }
        })
        .catch(error => console.error('Error updating revenue chart:', error));
}
</script>
@endsection
