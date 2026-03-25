@extends('admin.layouts.app')

@section('title', 'Customers')

@section('content')
@include('admin.components.delete-confirmation-modal')

<style>
/* Action dropdown with fixed positioning */
[id^="dropdown-customer-"] {
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
    border: 1px solid #e5e7eb;
    background: white;
}

/* Ensure dropdown is always on top */
[id^="dropdown-customer-"]:not(.hidden) {
    z-index: 9999 !important;
}

/* Smooth transition for dropdown */
[id^="dropdown-customer-"] {
    transition: opacity 0.15s ease-in-out;
}

/* Remove any overflow restrictions that might clip the dropdown */
tbody,
tbody tr,
tbody tr td {
    overflow: visible !important;
}
</style>

<div class="p-6 bg-gray-50 min-h-screen">
    <!-- Header Section -->
    <div class="mb-8">
        <div class="flex justify-between items-start mb-2">
            <div>
                <h1 class="text-3xl font-bold text-gray-900 font-inter">Customer List Overview</h1>
                <p class="text-gray-600 font-inter mt-1">View, manage, and monitor your e-commerce fitness community members.</p>
            </div>
        </div>
    </div>

    <!-- Statistics Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <!-- Total Customers -->
        <div class="bg-white p-6 rounded-xl border border-gray-200">
            <div class="flex items-center justify-between mb-4">
                <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                    <i class="fas fa-users text-blue-600 text-xl"></i>
                </div>
            </div>
            <div>
                <p class="text-gray-500 font-inter text-sm font-medium">Total Customers</p>
                <p class="text-2xl font-bold text-gray-900 font-inter mt-1">{{ number_format($stats['total_customers']) }}</p>
            </div>
        </div>

        <!-- Active Customers -->
        <div class="bg-white p-6 rounded-xl border border-gray-200">
            <div class="flex items-center justify-between mb-4">
                <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                    <i class="fas fa-user-check text-green-600 text-xl"></i>
                </div>
            </div>
            <div>
                <p class="text-gray-500 font-inter text-sm font-medium">Active Customers</p>
                <p class="text-2xl font-bold text-gray-900 font-inter mt-1">{{ number_format($stats['active_customers']) }}</p>
            </div>
        </div>

        <!-- New This Month -->
        <div class="bg-white p-6 rounded-xl border border-gray-200">
            <div class="flex items-center justify-between mb-4">
                <div class="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center">
                    <i class="fas fa-user-plus text-yellow-600 text-xl"></i>
                </div>
            </div>
            <div>
                <p class="text-gray-500 font-inter text-sm font-medium">New This Month</p>
                <p class="text-2xl font-bold text-gray-900 font-inter mt-1">{{ number_format($stats['new_this_month']) }}</p>
            </div>
        </div>

        <!-- Blocked -->
        <div class="bg-white p-6 rounded-xl border border-gray-200">
            <div class="flex items-center justify-between mb-4">
                <div class="w-12 h-12 bg-red-100 rounded-lg flex items-center justify-center">
                    <i class="fas fa-user-slash text-red-600 text-xl"></i>
                </div>
            </div>
            <div>
                <p class="text-gray-500 font-inter text-sm font-medium">Blocked</p>
                <p class="text-2xl font-bold text-gray-900 font-inter mt-1">{{ number_format($stats['blocked_customers']) }}</p>
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
                       placeholder="Search customer's name, email..." 
                       class="block w-full pl-10 pr-3 py-2.5 border border-gray-300 rounded-lg leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-blue-500 focus:border-blue-500 font-inter text-sm">
            </div>

            <!-- Filter and Export -->
            <div class="flex items-center space-x-4">
                <!-- Status Filter -->
                <div class="relative">
                    <select class="appearance-none bg-white border border-gray-300 rounded-lg px-4 py-2.5 pr-8 text-sm font-inter text-gray-700 focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-blue-500">
                        <option>All Statuses</option>
                        <option>Active</option>
                        <option>Blocked</option>
                    </select>
                    <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                        <i class="fas fa-chevron-down text-gray-400 text-xs"></i>
                    </div>
                </div>

                <!-- Export Button -->
                <button class="flex items-center px-4 py-2.5 bg-blue-600 text-white rounded-lg font-semibold font-inter text-sm hover:bg-blue-700 transition-colors">
                    <i class="fas fa-download mr-2"></i>
                    Export Data
                </button>
            </div>
        </div>
    </div>

    <!-- Customers Table -->
    <div class="bg-white rounded-xl border border-gray-200 overflow-hidden">
        <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">CUSTOMER</th>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">REG. DATE</th>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">ORDERS</th>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">TOTAL SPENT</th>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">STATUS</th>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">ACTIONS</th>
                    </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                    @forelse($customers as $customer)
                    <tr class="hover:bg-gray-50">
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="flex items-center">
                                @if($customer->avatar)
                                <img class="h-10 w-10 rounded-full object-cover" src="{{ asset('storage/' . $customer->avatar) }}" alt="{{ $customer->name }}">
                                @else
                                <div class="h-10 w-10 bg-gradient-to-br from-blue-400 to-blue-600 rounded-full flex items-center justify-center text-white font-semibold">
                                    {{ substr($customer->name, 0, 1) }}
                                </div>
                                @endif
                                <div class="ml-4">
                                    <div class="text-sm font-medium text-gray-900 font-inter">{{ $customer->name }}</div>
                                    <div class="text-sm text-gray-500 font-inter">{{ $customer->email }}</div>
                                </div>
                            </div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600 font-inter">{{ $customer->created_at->format('M d, Y') }}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-inter font-semibold">{{ $customer->orders_count }}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-semibold text-blue-600 font-inter">Rp{{ number_format($customer->total_spent ?? 0, 0, ',', '.') }}</td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            @if($customer->is_active)
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800 font-inter">
                                Active
                            </span>
                            @else
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800 font-inter">
                                Blocked
                            </span>
                            @endif
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                            <div class="relative">
                                <button onclick="event.stopPropagation(); toggleCustomerDropdown('dropdown-customer-{{ $customer->id }}', this)" class="text-gray-400 hover:text-gray-600 p-2 rounded-lg hover:bg-gray-100 transition-colors">
                                    <i class="fas fa-ellipsis-v"></i>
                                </button>
                                <div id="dropdown-customer-{{ $customer->id }}" class="hidden fixed bg-white rounded-lg shadow-2xl border border-gray-200" style="min-width: 200px; z-index: 9999;">
                                    <div class="py-1">
                                        <a href="{{ route('admin.customers.edit', $customer) }}" class="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-600 font-inter">
                                            <i class="fas fa-edit mr-3 text-blue-500"></i>
                                            Edit Customer
                                        </a>
                                        @if($customer->is_active)
                                        <button 
                                            onclick="confirmBanCustomer(this)" 
                                            data-url="{{ route('admin.customers.destroy', $customer) }}"
                                            data-name="{{ $customer->name }}"
                                            data-email="{{ $customer->email }}"
                                            data-action="ban"
                                            class="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-red-50 hover:text-red-600 font-inter">
                                            <i class="fas fa-ban mr-3 text-red-500"></i>
                                            Ban Customer
                                        </button>
                                        @else
                                        <button 
                                            onclick="confirmBanCustomer(this)" 
                                            data-url="{{ route('admin.customers.destroy', $customer) }}"
                                            data-name="{{ $customer->name }}"
                                            data-email="{{ $customer->email }}"
                                            data-action="unban"
                                            class="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-green-50 hover:text-green-600 font-inter">
                                            <i class="fas fa-check-circle mr-3 text-green-500"></i>
                                            Unban Customer
                                        </button>
                                        @endif
                                    </div>
                                </div>
                            </div>
                        </td>
                    </tr>
                    @empty
                    <tr>
                        <td colspan="6" class="px-6 py-12 text-center">
                            <div class="flex flex-col items-center justify-center">
                                <i class="fas fa-users text-gray-300 text-5xl mb-4"></i>
                                <p class="text-gray-500 font-inter text-lg font-medium">No customers found</p>
                                <p class="text-gray-400 font-inter text-sm mt-1">Customers will appear here once they register</p>
                            </div>
                        </td>
                    </tr>
                    @endforelse
                </tbody>
            </table>
        </div>

        <!-- Pagination -->
        <div class="px-6 py-4 border-t border-gray-200">
            {{ $customers->links('vendor.pagination.tailwind') }}
        </div>
    </div>
</div>

<script>
// Dropdown functions for actions
function toggleCustomerDropdown(dropdownId, buttonElement) {
    const dropdown = document.getElementById(dropdownId);
    const allDropdowns = document.querySelectorAll('[id^="dropdown-customer-"]');
    
    if (!dropdown) return;
    
    // Close all other dropdowns
    allDropdowns.forEach(dd => {
        if (dd.id !== dropdownId) {
            dd.classList.add('hidden');
        }
    });
    
    // Toggle the clicked dropdown
    const isHidden = dropdown.classList.contains('hidden');
    
    if (isHidden) {
        // Show dropdown and calculate position
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
    const target = event.target;
    const isDropdownButton = target.closest('button[onclick*="toggleCustomerDropdown"]');
    const isDropdownContent = target.closest('[id^="dropdown-customer-"]');
    
    if (!isDropdownButton && !isDropdownContent) {
        document.querySelectorAll('[id^="dropdown-customer-"]').forEach(dropdown => {
            dropdown.classList.add('hidden');
        });
    }
});

// Close dropdowns on scroll
window.addEventListener('scroll', function() {
    document.querySelectorAll('[id^="dropdown-customer-"]').forEach(dropdown => {
        dropdown.classList.add('hidden');
    });
}, true);

// Close dropdowns on window resize
window.addEventListener('resize', function() {
    document.querySelectorAll('[id^="dropdown-customer-"]').forEach(dropdown => {
        dropdown.classList.add('hidden');
    });
});

// Confirm ban/unban customer
function confirmBanCustomer(buttonElement) {
    // Close any open dropdowns
    document.querySelectorAll('[id^="dropdown-customer-"]').forEach(dropdown => {
        dropdown.classList.add('hidden');
    });
    
    // Get data from button attributes
    const url = buttonElement.getAttribute('data-url');
    const name = buttonElement.getAttribute('data-name');
    const email = buttonElement.getAttribute('data-email');
    const action = buttonElement.getAttribute('data-action');
    
    const isBan = action === 'ban';
    const title = isBan ? 'Ban Customer?' : 'Unban Customer?';
    const message = isBan 
        ? 'Are you sure you want to ban this customer? They will not be able to access their account.'
        : 'Are you sure you want to unban this customer? They will be able to access their account again.';
    
    // Show confirmation dialog
    if (confirm(title + '\n\n' + message + '\n\nCustomer: ' + name + ' (' + email + ')')) {
        // Create form and submit
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = url;
        
        // CSRF token
        const csrfInput = document.createElement('input');
        csrfInput.type = 'hidden';
        csrfInput.name = '_token';
        csrfInput.value = document.querySelector('meta[name="csrf-token"]').getAttribute('content');
        form.appendChild(csrfInput);
        
        // Method spoofing for DELETE
        const methodInput = document.createElement('input');
        methodInput.type = 'hidden';
        methodInput.name = '_method';
        methodInput.value = 'DELETE';
        form.appendChild(methodInput);
        
        document.body.appendChild(form);
        form.submit();
    }
}

// Notification function (if not already defined)
if (typeof showNotification === 'undefined') {
    window.showNotification = function(message, type = 'success') {
        const notification = document.createElement('div');
        notification.className = `fixed top-4 right-4 px-6 py-3 rounded-lg text-white font-medium font-inter z-50 transform transition-all duration-300 translate-x-full ${
            type === 'success' ? 'bg-green-500' : 'bg-red-500'
        }`;
        notification.innerHTML = `<i class="fas fa-${type === 'success' ? 'check' : 'exclamation-circle'} mr-2"></i>${message}`;
        
        document.body.appendChild(notification);
        
        // Animate in
        setTimeout(() => {
            notification.classList.remove('translate-x-full');
        }, 10);
        
        // Remove after 3 seconds
        setTimeout(() => {
            notification.classList.add('translate-x-full');
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, 3000);
    };
}
</script>
@endsection