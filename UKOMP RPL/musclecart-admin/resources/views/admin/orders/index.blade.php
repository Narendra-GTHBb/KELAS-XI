@extends('admin.layouts.app')

@section('title', 'Orders')

@section('content')
<div class="p-6 bg-gray-50 min-h-screen">
    <!-- Header Section -->
    <div class="mb-8">
        <div class="flex justify-between items-start mb-2">
            <div>
                <h1 class="text-3xl font-bold text-gray-900 font-inter">Orders</h1>
                <p class="text-gray-600 font-inter mt-1">Manage and track your store's sales performance efficiently</p>
            </div>
            <div class="flex space-x-3">
                <button class="flex items-center px-4 py-2.5 bg-white text-gray-700 border border-gray-300 rounded-lg font-semibold font-inter text-sm hover:bg-gray-50 transition-colors">
                    <i class="fas fa-download mr-2"></i>
                    Export
                </button>
                <button class="flex items-center px-4 py-2.5 bg-blue-600 text-white rounded-lg font-semibold font-inter text-sm hover:bg-blue-700 transition-colors">
                    <i class="fas fa-plus mr-2"></i>
                    Create Order
                </button>
            </div>
        </div>
    </div>

    <!-- Statistics Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <!-- Total Orders -->
        <div class="bg-white p-6 rounded-xl border border-gray-200">
            <div class="flex items-center justify-between mb-4">
                <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                    <i class="fas fa-shopping-cart text-blue-600 text-xl"></i>
                </div>
            </div>
            <div>
                <p class="text-gray-500 font-inter text-sm font-medium">Total Orders</p>
                <p class="text-2xl font-bold text-gray-900 font-inter mt-1">{{ number_format($stats['total_orders']) }}</p>
            </div>
        </div>

        <!-- Pending -->
        <div class="bg-white p-6 rounded-xl border border-gray-200">
            <div class="flex items-center justify-between mb-4">
                <div class="w-12 h-12 bg-orange-100 rounded-lg flex items-center justify-center">
                    <i class="fas fa-clock text-orange-600 text-xl"></i>
                </div>
            </div>
            <div>
                <p class="text-gray-500 font-inter text-sm font-medium">Pending</p>
                <p class="text-2xl font-bold text-gray-900 font-inter mt-1">{{ number_format($stats['pending_orders']) }}</p>
            </div>
        </div>

        <!-- Shipped -->
        <div class="bg-white p-6 rounded-xl border border-gray-200">
            <div class="flex items-center justify-between mb-4">
                <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                    <i class="fas fa-truck text-blue-600 text-xl"></i>
                </div>
            </div>
            <div>
                <p class="text-gray-500 font-inter text-sm font-medium">Shipped</p>
                <p class="text-2xl font-bold text-gray-900 font-inter mt-1">{{ number_format($stats['shipped_orders']) }}</p>
            </div>
        </div>

        <!-- Delivered -->
        <div class="bg-white p-6 rounded-xl border border-gray-200">
            <div class="flex items-center justify-between mb-4">
                <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                    <i class="fas fa-check-circle text-green-600 text-xl"></i>
                </div>
            </div>
            <div>
                <p class="text-gray-500 font-inter text-sm font-medium">Delivered</p>
                <p class="text-2xl font-bold text-gray-900 font-inter mt-1">{{ number_format($stats['delivered_orders']) }}</p>
            </div>
        </div>
    </div>

    <!-- Search and Filters -->
    <div class="bg-white rounded-xl border border-gray-200 p-6 mb-6">
        <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
            <!-- Search Bar -->
            <div class="relative flex-1 max-w-md">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <i class="fas fa-search text-gray-400"></i>
                </div>
                <input type="text" 
                       placeholder="Search order ID or customer..." 
                       class="block w-full pl-10 pr-3 py-2.5 border border-gray-300 rounded-lg leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-blue-500 focus:border-blue-500 font-inter text-sm">
            </div>

            <!-- Filter Tabs and More Filters -->
            <div class="flex items-center space-x-4">
                <!-- Status Tabs -->
                <div class="flex bg-gray-50 rounded-lg p-1">
                    <button class="px-4 py-2 text-sm font-medium font-inter bg-white text-blue-600 rounded-md shadow-sm">All Orders</button>
                    <button class="px-4 py-2 text-sm font-medium font-inter text-gray-600 hover:text-gray-900">Pending</button>
                    <button class="px-4 py-2 text-sm font-medium font-inter text-gray-600 hover:text-gray-900">Shipped</button>
                    <button class="px-4 py-2 text-sm font-medium font-inter text-gray-600 hover:text-gray-900">Completed</button>
                </div>

                <!-- More Filters Button -->
                <button class="flex items-center px-4 py-2.5 bg-white text-gray-700 border border-gray-300 rounded-lg font-semibold font-inter text-sm hover:bg-gray-50 transition-colors">
                    <i class="fas fa-filter mr-2"></i>
                    More Filters
                </button>
            </div>
        </div>
    </div>

    <!-- Orders Table -->
    <div class="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">ORDER ID</th>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">CUSTOMER</th>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">DATE</th>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">TOTAL</th>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">PAYMENT STATUS</th>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">ORDER STATUS</th>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">ACTIONS</th>
                    </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                    @forelse($orders as $order)
                    <tr class="hover:bg-gray-50">
                        <td class="px-6 py-4 whitespace-nowrap">
                            <span class="font-semibold text-blue-600 font-inter">#{{ $order->order_number }}</span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="flex items-center">
                                @if($order->user && $order->user->avatar)
                                <img class="h-8 w-8 rounded-full object-cover" src="{{ asset('storage/' . $order->user->avatar) }}" alt="{{ $order->user->name }}">
                                @else
                                <div class="h-8 w-8 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white font-semibold text-sm">
                                    {{ substr($order->user->name ?? 'G', 0, 1) }}
                                </div>
                                @endif
                                <span class="ml-3 font-medium text-gray-900 font-inter">{{ $order->user->name ?? 'Guest' }}</span>
                            </div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-gray-600 font-inter">{{ $order->created_at->format('M d, Y') }}</td>
                        <td class="px-6 py-4 whitespace-nowrap font-semibold text-gray-900 font-inter">Rp{{ number_format($order->total_amount, 0, ',', '.') }}</td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            @php
                            $paymentStatusClasses = [
                                'pending' => 'bg-orange-100 text-orange-800',
                                'authorized' => 'bg-blue-100 text-blue-800',
                                'paid' => 'bg-green-100 text-green-800',
                                'success' => 'bg-green-100 text-green-800',
                                'failed' => 'bg-red-100 text-red-800',
                                'refunded' => 'bg-red-100 text-red-800',
                            ];
                            $paymentDotClasses = [
                                'pending' => 'bg-orange-500',
                                'authorized' => 'bg-blue-500',
                                'paid' => 'bg-green-500',
                                'success' => 'bg-green-500',
                                'failed' => 'bg-red-500',
                                'refunded' => 'bg-red-500',
                            ];
                            @endphp
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium {{ $paymentStatusClasses[$order->payment_status] ?? 'bg-gray-100 text-gray-800' }} font-inter">
                                <span class="w-1.5 h-1.5 {{ $paymentDotClasses[$order->payment_status] ?? 'bg-gray-500' }} rounded-full mr-1.5"></span>
                                {{ ucfirst($order->payment_status) }}
                            </span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            @php
                            $statusClasses = [
                                'pending' => 'bg-orange-100 text-orange-800',
                                'processing' => 'bg-blue-100 text-blue-800',
                                'shipped' => 'bg-blue-100 text-blue-800',
                                'delivered' => 'bg-green-100 text-green-800',
                                'completed' => 'bg-green-100 text-green-800',
                                'cancelled' => 'bg-gray-100 text-gray-800',
                            ];
                            @endphp
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium {{ $statusClasses[$order->status] ?? 'bg-gray-100 text-gray-800' }} font-inter">
                                {{ ucfirst($order->status) }}
                            </span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap relative">
                            <button onclick="event.stopPropagation(); toggleActionDropdown('dropdown-{{ $order->id }}', this)" class="text-gray-400 hover:text-gray-600">
                                <i class="fas fa-ellipsis-v"></i>
                            </button>
                            <div id="dropdown-{{ $order->id }}" class="hidden bg-white rounded-lg shadow-lg border border-gray-200 py-1 w-48 z-[9999]" style="position: fixed;">
                                <a href="{{ route('admin.orders.show', $order) }}" class="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                                    <i class="fas fa-eye mr-2 text-blue-500"></i>
                                    View Details
                                </a>
                                <a href="{{ route('admin.orders.show', $order) }}" class="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                                    <i class="fas fa-edit mr-2 text-green-500"></i>
                                    Manage Order
                                </a>
                            </div>
                        </td>
                    </tr>
                    @empty
                    <tr>
                        <td colspan="7" class="px-6 py-12 text-center">
                            <div class="flex flex-col items-center justify-center">
                                <i class="fas fa-shopping-cart text-gray-300 text-5xl mb-4"></i>
                                <p class="text-gray-500 font-inter text-lg font-medium">No orders found</p>
                                <p class="text-gray-400 font-inter text-sm mt-1">Orders will appear here once customers make purchases</p>
                            </div>
                        </td>
                    </tr>
                    @endforelse
                </tbody>
            </table>
        </div>

        <!-- Pagination -->
        <div class="px-6 py-4 border-t border-gray-200">
            {{ $orders->links('vendor.pagination.tailwind') }}
        </div>
    </div>
</div>

<script>
function toggleActionDropdown(dropdownId, buttonElement) {
    const dropdown = document.getElementById(dropdownId);
    const allDropdowns = document.querySelectorAll('[id^="dropdown-"]');
    
    if (!dropdown) return;
    
    // Close all other dropdowns
    allDropdowns.forEach(dd => {
        if (dd.id !== dropdownId) {
            dd.classList.add('hidden');
        }
    });
    
    // Toggle current dropdown
    const isHidden = dropdown.classList.contains('hidden');
    
    if (isHidden) {
        // Show dropdown
        dropdown.classList.remove('hidden');
        
        // Get button position
        const buttonRect = buttonElement.getBoundingClientRect();
        const dropdownRect = dropdown.getBoundingClientRect();
        const viewportHeight = window.innerHeight;
        const viewportWidth = window.innerWidth;
        
        // Calculate position
        let top = buttonRect.bottom + 8;
        let left = buttonRect.right - dropdownRect.width;
        
        // Check if dropdown goes below viewport
        if (top + dropdownRect.height > viewportHeight - 20) {
            top = buttonRect.top - dropdownRect.height - 8;
        }
        
        // Check if dropdown goes beyond left edge
        if (left < 10) {
            left = 10;
        }
        
        // Check if dropdown goes beyond right edge
        if (left + dropdownRect.width > viewportWidth - 10) {
            left = viewportWidth - dropdownRect.width - 10;
        }
        
        // Apply position
        dropdown.style.top = top + 'px';
        dropdown.style.left = left + 'px';
    } else {
        dropdown.classList.add('hidden');
    }
}

// Close dropdowns when clicking outside
document.addEventListener('click', function(event) {
    const targetElement = event.target;
    const isButton = targetElement.closest('button[onclick*="toggleActionDropdown"]');
    const isDropdown = targetElement.closest('[id^="dropdown-"]');
    
    if (!isButton && !isDropdown) {
        const allDropdowns = document.querySelectorAll('[id^="dropdown-"]');
        allDropdowns.forEach(dd => dd.classList.add('hidden'));
    }
});

// Close dropdowns on scroll
window.addEventListener('scroll', function() {
    const allDropdowns = document.querySelectorAll('[id^="dropdown-"]');
    allDropdowns.forEach(dd => dd.classList.add('hidden'));
}, true);

// Close dropdowns on window resize
window.addEventListener('resize', function() {
    const allDropdowns = document.querySelectorAll('[id^="dropdown-"]');
    allDropdowns.forEach(dd => dd.classList.add('hidden'));
});
</script>

@endsection
