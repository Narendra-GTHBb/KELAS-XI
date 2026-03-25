<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="csrf-token" content="{{ csrf_token() }}">
    <title>{{ $product->name }} - MuscleCart</title>
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

    <!-- Breadcrumb -->
    <div class="bg-white border-b">
        <div class="container mx-auto px-4 py-4">
            <nav class="flex text-sm">
                <a href="{{ url('/shop') }}" class="text-gray-500 hover:text-orange-600">Shop</a>
                <span class="mx-2 text-gray-400">/</span>
                <a href="{{ url('/shop?category=' . $product->category_id) }}" class="text-gray-500 hover:text-orange-600">
                    {{ $product->category->name ?? 'Products' }}
                </a>
                <span class="mx-2 text-gray-400">/</span>
                <span class="text-gray-800">{{ $product->name }}</span>
            </nav>
        </div>
    </div>

    <div class="container mx-auto px-4 py-8">
        <!-- Product Detail Section -->
        <div class="bg-white rounded-lg shadow-lg p-8 mb-8">
            <div class="grid grid-cols-1 lg:grid-cols-2 gap-12">
                <!-- Product Image -->
                <div class="space-y-4">
                    <div class="relative">
                        @if($product->full_image_url)
                            <img src="{{ $product->full_image_url }}" 
                                 alt="{{ $product->name }}" 
                                 class="w-full h-96 object-cover rounded-lg shadow-md">
                        @else
                            <div class="w-full h-96 bg-gray-200 rounded-lg flex items-center justify-center">
                                <i class="fas fa-image text-gray-400 text-6xl"></i>
                            </div>
                        @endif
                        
                        @if($product->is_featured)
                            <span class="absolute top-4 left-4 bg-yellow-500 text-white text-sm font-semibold px-3 py-1 rounded-full">
                                <i class="fas fa-star mr-1"></i> Featured
                            </span>
                        @endif
                        
                        @if($product->stock_quantity <= 5 && $product->stock_quantity > 0)
                            <span class="absolute top-4 right-4 bg-red-500 text-white text-sm font-semibold px-3 py-1 rounded-full">
                                Only {{ $product->stock_quantity }} left!
                            </span>
                        @endif
                    </div>
                </div>

                <!-- Product Information -->
                <div class="space-y-6">
                    <div>
                        <span class="inline-block bg-orange-100 text-orange-600 text-sm font-medium px-3 py-1 rounded-full mb-3">
                            {{ $product->category->name ?? 'No Category' }}
                        </span>
                        <h1 class="text-3xl md:text-4xl font-bold text-gray-800 mb-2">{{ $product->name }}</h1>
                        @if($product->brand)
                            <p class="text-gray-600">Brand: <span class="font-semibold">{{ $product->brand }}</span></p>
                        @endif
                    </div>

                    <div class="border-t border-b py-4">
                        <p class="text-4xl font-bold text-orange-600">
                            Rp {{ number_format($product->price, 0, ',', '.') }}
                        </p>
                    </div>

                    @if($product->description)
                        <div>
                            <h3 class="text-lg font-semibold text-gray-800 mb-2">Description</h3>
                            <p class="text-gray-600 leading-relaxed">{{ $product->description }}</p>
                        </div>
                    @endif

                    <!-- Product Details -->
                    <div class="grid grid-cols-2 gap-4 bg-gray-50 p-4 rounded-lg">
                        <div>
                            <p class="text-sm text-gray-500">Stock Status</p>
                            <p class="font-semibold {{ $product->stock_quantity > 0 ? 'text-green-600' : 'text-red-600' }}">
                                @if($product->stock_quantity > 0)
                                    <i class="fas fa-check-circle mr-1"></i> In Stock ({{ $product->stock_quantity }} units)
                                @else
                                    <i class="fas fa-times-circle mr-1"></i> Out of Stock
                                @endif
                            </p>
                        </div>
                        
                        @if($product->weight)
                            <div>
                                <p class="text-sm text-gray-500">Weight</p>
                                <p class="font-semibold text-gray-800">{{ $product->weight }} kg</p>
                            </div>
                        @endif
                    </div>

                    <!-- Quantity and Add to Cart -->
                    @auth
                        @if($product->stock_quantity > 0)
                            <div class="space-y-4">
                                <div>
                                    <label class="block text-sm font-medium text-gray-700 mb-2">Quantity</label>
                                    <div class="flex items-center space-x-4">
                                        <div class="flex items-center border border-gray-300 rounded-lg">
                                            <button onclick="decreaseQuantity()" class="px-4 py-2 hover:bg-gray-100 transition">
                                                <i class="fas fa-minus text-gray-600"></i>
                                            </button>
                                            <input type="number" id="quantity" value="1" min="1" max="{{ $product->stock_quantity }}" 
                                                   class="w-20 text-center border-x border-gray-300 py-2 focus:outline-none">
                                            <button onclick="increaseQuantity({{ $product->stock_quantity }})" class="px-4 py-2 hover:bg-gray-100 transition">
                                                <i class="fas fa-plus text-gray-600"></i>
                                            </button>
                                        </div>
                                        <span class="text-sm text-gray-500">Max: {{ $product->stock_quantity }} units</span>
                                    </div>
                                </div>

                                <div class="flex gap-4">
                                    <button onclick="addToCart({{ $product->id }}, '{{ $product->name }}')" 
                                            class="flex-1 bg-orange-600 text-white px-8 py-4 rounded-lg hover:bg-orange-700 transition text-lg font-semibold">
                                        <i class="fas fa-cart-plus mr-2"></i> Add to Cart
                                    </button>
                                </div>
                            </div>
                        @else
                            <div class="bg-red-50 border border-red-200 rounded-lg p-4 text-center">
                                <i class="fas fa-exclamation-circle text-red-500 text-2xl mb-2"></i>
                                <p class="text-red-700 font-semibold">This product is currently out of stock</p>
                            </div>
                        @endif
                    @else
                        <div class="bg-blue-50 border border-blue-200 rounded-lg p-4 text-center">
                            <i class="fas fa-info-circle text-blue-500 text-2xl mb-2"></i>
                            <p class="text-gray-700 mb-3">Please login to add products to cart</p>
                            <a href="{{ route('login') }}" class="inline-block bg-orange-600 text-white px-8 py-3 rounded-lg hover:bg-orange-700 transition font-semibold">
                                Login to Purchase
                            </a>
                        </div>
                    @endauth
                </div>
            </div>
        </div>

        <!-- Related Products -->
        @if($relatedProducts->count() > 0)
            <div class="mb-8">
                <h2 class="text-2xl font-bold text-gray-800 mb-6">Related Products</h2>
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                    @foreach($relatedProducts as $relatedProduct)
                        <div class="bg-white rounded-lg shadow-md hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1">
                            <div class="relative">
                                @if($relatedProduct->full_image_url)
                                    <img src="{{ $relatedProduct->full_image_url }}" 
                                         alt="{{ $relatedProduct->name }}" 
                                         class="w-full h-48 object-cover rounded-t-lg">
                                @else
                                    <div class="w-full h-48 bg-gray-200 rounded-t-lg flex items-center justify-center">
                                        <i class="fas fa-image text-gray-400 text-3xl"></i>
                                    </div>
                                @endif
                            </div>
                            
                            <div class="p-4">
                                <h3 class="text-lg font-semibold text-gray-800 mb-2 line-clamp-2">
                                    {{ $relatedProduct->name }}
                                </h3>
                                <p class="text-xl font-bold text-orange-600 mb-3">
                                    Rp {{ number_format($relatedProduct->price, 0, ',', '.') }}
                                </p>
                                <a href="{{ url('/shop/product/' . $relatedProduct->id) }}" 
                                   class="block text-center bg-orange-600 text-white px-4 py-2 rounded-lg hover:bg-orange-700 transition">
                                    View Details
                                </a>
                            </div>
                        </div>
                    @endforeach
                </div>
            </div>
        @endif
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

        // Quantity controls
        function increaseQuantity(max) {
            const input = document.getElementById('quantity');
            const currentValue = parseInt(input.value);
            if (currentValue < max) {
                input.value = currentValue + 1;
            }
        }

        function decreaseQuantity() {
            const input = document.getElementById('quantity');
            const currentValue = parseInt(input.value);
            if (currentValue > 1) {
                input.value = currentValue - 1;
            }
        }

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
            const quantity = parseInt(document.getElementById('quantity').value);
            
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
                        quantity: quantity
                    })
                });

                const data = await response.json();

                if (response.ok && data.status === 'success') {
                    showToast('Added to Cart!', `${quantity}x ${productName} has been added to your cart`, 'success');
                    updateCartBadge();
                    // Reset quantity to 1
                    document.getElementById('quantity').value = 1;
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
