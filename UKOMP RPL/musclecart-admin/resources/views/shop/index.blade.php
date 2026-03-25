<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="csrf-token" content="{{ csrf_token() }}">
    <title>Shop - MuscleCart</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body class="bg-gray-50">
    <!-- Navigation -->
    <nav class="bg-white shadow-lg sticky top-0 z-50">
        <div class="container mx-auto px-4 py-4">
            <div class="flex items-center justify-between">
                <div class="flex items-center space-x-8">
                    <a href="{{ url('/shop') }}" class="text-2xl font-bold text-orange-600">
                        <i class="fas fa-dumbbell"></i> MuscleCart
                    </a>
                    <a href="{{ url('/shop') }}" class="text-gray-700 hover:text-orange-600 transition">Shop</a>
                </div>
                <div class="flex items-center space-x-4">
                    @auth
                        <a href="{{ url('/cart') }}" class="relative text-gray-700 hover:text-orange-600 transition">
                            <i class="fas fa-shopping-cart text-xl"></i>
                            <span id="cart-badge" class="absolute -top-2 -right-2 bg-orange-600 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center hidden">0</span>
                        </a>
                        <a href="{{ url('/admin/dashboard') }}" class="bg-orange-600 text-white px-4 py-2 rounded-lg hover:bg-orange-700 transition">
                            Dashboard
                        </a>
                    @else
                        <a href="{{ route('login') }}" class="text-gray-700 hover:text-orange-600 transition">Login</a>
                        <a href="{{ url('/shop') }}" class="bg-orange-600 text-white px-4 py-2 rounded-lg hover:bg-orange-700 transition">
                            Shop Now
                        </a>
                    @endauth
                </div>
            </div>
        </div>
    </nav>

    <!-- Hero Section -->
    <div class="bg-gradient-to-r from-orange-500 to-orange-600 text-white py-16">
        <div class="container mx-auto px-4 text-center">
            <h1 class="text-4xl md:text-5xl font-bold mb-4">Welcome to MuscleCart Shop</h1>
            <p class="text-xl mb-8">Premium supplements for your fitness journey</p>
        </div>
    </div>

    <div class="container mx-auto px-4 py-8">
        <!-- Filters Section -->
        <div class="bg-white rounded-lg shadow-md p-6 mb-8">
            <form method="GET" action="{{ url('/shop') }}" class="flex flex-wrap gap-4 items-end">
                <!-- Search -->
                <div class="flex-1 min-w-[250px]">
                    <label class="block text-sm font-medium text-gray-700 mb-2">Search Products</label>
                    <input type="text" name="search" value="{{ request('search') }}" 
                           placeholder="Search by name..." 
                           class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent">
                </div>

                <!-- Category Filter -->
                <div class="w-full md:w-48">
                    <label class="block text-sm font-medium text-gray-700 mb-2">Category</label>
                    <select name="category" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent">
                        <option value="">All Categories</option>
                        @foreach($categories as $category)
                            <option value="{{ $category->id }}" {{ request('category') == $category->id ? 'selected' : '' }}>
                                {{ $category->name }}
                            </option>
                        @endforeach
                    </select>
                </div>

                <!-- Sort -->
                <div class="w-full md:w-48">
                    <label class="block text-sm font-medium text-gray-700 mb-2">Sort By</label>
                    <select name="sort" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-orange-500 focus:border-transparent">
                        <option value="newest" {{ request('sort') == 'newest' ? 'selected' : '' }}>Newest</option>
                        <option value="price_low" {{ request('sort') == 'price_low' ? 'selected' : '' }}>Price: Low to High</option>
                        <option value="price_high" {{ request('sort') == 'price_high' ? 'selected' : '' }}>Price: High to Low</option>
                        <option value="name" {{ request('sort') == 'name' ? 'selected' : '' }}>Name: A-Z</option>
                    </select>
                </div>

                <!-- Buttons -->
                <div class="flex gap-2">
                    <button type="submit" class="bg-orange-600 text-white px-6 py-2 rounded-lg hover:bg-orange-700 transition">
                        <i class="fas fa-filter mr-2"></i>Filter
                    </button>
                    <a href="{{ url('/shop') }}" class="bg-gray-200 text-gray-700 px-6 py-2 rounded-lg hover:bg-gray-300 transition">
                        Clear
                    </a>
                </div>
            </form>
        </div>

        <!-- Products Grid -->
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mb-8">
            @forelse($products as $product)
                <div class="bg-white rounded-lg shadow-md hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1">
                    <div class="relative">
                        @if($product->full_image_url)
                            <img src="{{ $product->full_image_url }}" 
                                 alt="{{ $product->name }}" 
                                 class="w-full h-64 object-cover rounded-t-lg">
                        @else
                            <div class="w-full h-64 bg-gray-200 rounded-t-lg flex items-center justify-center">
                                <i class="fas fa-image text-gray-400 text-4xl"></i>
                            </div>
                        @endif
                        
                        @if($product->stock_quantity <= 5)
                            <span class="absolute top-2 right-2 bg-red-500 text-white text-xs px-2 py-1 rounded">
                                Low Stock
                            </span>
                        @endif
                    </div>
                    
                    <div class="p-4">
                        <div class="mb-2">
                            <span class="text-xs text-gray-500">{{ $product->category->name ?? 'No Category' }}</span>
                        </div>
                        <h3 class="text-lg font-semibold text-gray-800 mb-2 line-clamp-2">{{ $product->name }}</h3>
                        
                        @if($product->description)
                            <p class="text-sm text-gray-600 mb-3 line-clamp-2">{{ $product->description }}</p>
                        @endif
                        
                        <div class="flex items-center justify-between mb-4">
                            <span class="text-2xl font-bold text-orange-600">
                                Rp {{ number_format($product->price, 0, ',', '.') }}
                            </span>
                            <span class="text-sm text-gray-500">
                                Stock: {{ $product->stock_quantity }}
                            </span>
                        </div>
                        
                        <div class="flex gap-2">
                            <a href="{{ url('/shop/product/' . $product->id) }}" 
                               class="flex-1 text-center bg-gray-100 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-200 transition">
                                <i class="fas fa-eye mr-1"></i> View
                            </a>
                            
                            @auth
                                @if($product->stock_quantity > 0)
                                    <button onclick="addToCart({{ $product->id }}, '{{ $product->name }}')" 
                                            class="flex-1 bg-orange-600 text-white px-4 py-2 rounded-lg hover:bg-orange-700 transition">
                                        <i class="fas fa-cart-plus mr-1"></i> Add
                                    </button>
                                @else
                                    <button disabled 
                                            class="flex-1 bg-gray-300 text-gray-500 px-4 py-2 rounded-lg cursor-not-allowed">
                                        Out of Stock
                                    </button>
                                @endif
                            @else
                                <a href="{{ route('login') }}" 
                                   class="flex-1 text-center bg-orange-600 text-white px-4 py-2 rounded-lg hover:bg-orange-700 transition">
                                    <i class="fas fa-cart-plus mr-1"></i> Add
                                </a>
                            @endauth
                        </div>
                    </div>
                </div>
            @empty
                <div class="col-span-full text-center py-16">
                    <i class="fas fa-box-open text-6xl text-gray-300 mb-4"></i>
                    <p class="text-xl text-gray-500">No products found</p>
                </div>
            @endforelse
        </div>

        <!-- Pagination -->
        <div class="flex justify-center">
            {{ $products->links() }}
        </div>
    </div>

    <!-- Toast Notification -->
    <div id="toast" class="fixed top-20 right-4 bg-green-500 text-white px-6 py-4 rounded-lg shadow-lg transform translate-x-full transition-transform duration-300 z-50">
        <div class="flex items-center space-x-3">
            <i class="fas fa-check-circle text-2xl"></i>
            <div>
                <p class="font-semibold" id="toast-title">Success!</p>
                <p class="text-sm" id="toast-message">Product added to cart</p>
            </div>
        </div>
    </div>

    <script>
        // Set CSRF token for all AJAX requests
        const csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');

        // Show toast notification
        function showToast(title, message, type = 'success') {
            const toast = document.getElementById('toast');
            const toastTitle = document.getElementById('toast-title');
            const toastMessage = document.getElementById('toast-message');
            
            toastTitle.textContent = title;
            toastMessage.textContent = message;
            
            // Set color based on type
            if (type === 'success') {
                toast.className = toast.className.replace(/bg-\w+-500/, 'bg-green-500');
            } else if (type === 'error') {
                toast.className = toast.className.replace(/bg-\w+-500/, 'bg-red-500');
            }
            
            // Show toast
            toast.classList.remove('translate-x-full');
            
            // Hide after 3 seconds
            setTimeout(() => {
                toast.classList.add('translate-x-full');
            }, 3000);
        }

        // Add to cart function
        async function addToCart(productId, productName) {
            try {
                const response = await fetch('/api/cart/add', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': csrfToken,
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify({
                        product_id: productId,
                        quantity: 1
                    })
                });

                const data = await response.json();

                if (response.ok && data.status === 'success') {
                    showToast('Added to Cart!', `${productName} has been added to your cart`, 'success');
                    updateCartBadge();
                } else {
                    showToast('Error', data.message || 'Failed to add product to cart', 'error');
                }
            } catch (error) {
                console.error('Error:', error);
                showToast('Error', 'Something went wrong. Please try again.', 'error');
            }
        }

        // Update cart badge
        async function updateCartBadge() {
            try {
                const response = await fetch('/api/cart', {
                    headers: {
                        'Accept': 'application/json',
                        'X-CSRF-TOKEN': csrfToken
                    }
                });

                if (response.ok) {
                    const data = await response.json();
                    const badge = document.getElementById('cart-badge');
                    const totalItems = data.data.summary.total_items;
                    
                    if (totalItems > 0) {
                        badge.textContent = totalItems;
                        badge.classList.remove('hidden');
                    } else {
                        badge.classList.add('hidden');
                    }
                }
            } catch (error) {
                console.error('Error updating cart badge:', error);
            }
        }

        // Update cart badge on page load
        @auth
        document.addEventListener('DOMContentLoaded', updateCartBadge);
        @endauth
    </script>
</body>
</html>
