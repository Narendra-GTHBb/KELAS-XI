<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="csrf-token" content="{{ csrf_token() }}">
    <title>Shopping Cart - MuscleCart</title>
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
                    <a href="{{ url('/cart') }}" class="text-orange-600 font-semibold">Cart</a>
                </div>
                <div class="flex items-center space-x-4">
                    <a href="{{ url('/admin/dashboard') }}" class="bg-orange-600 text-white px-4 py-2 rounded-lg hover:bg-orange-700 transition">
                        Dashboard
                    </a>
                </div>
            </div>
        </div>
    </nav>

    <div class="container mx-auto px-4 py-8">
        <!-- Page Header -->
        <div class="mb-8">
            <h1 class="text-3xl font-bold text-gray-800 mb-2">Shopping Cart</h1>
            <p class="text-gray-600">Review your items before checkout</p>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <!-- Cart Items -->
            <div class="lg:col-span-2">
                <div id="cart-items" class="space-y-4">
                    <!-- Cart items will be loaded here -->
                    <div class="flex items-center justify-center py-16">
                        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-orange-600"></div>
                    </div>
                </div>

                <!-- Empty Cart Message -->
                <div id="empty-cart" class="hidden bg-white rounded-lg shadow-md p-12 text-center">
                    <i class="fas fa-shopping-cart text-gray-300 text-6xl mb-4"></i>
                    <h3 class="text-xl font-semibold text-gray-800 mb-2">Your cart is empty</h3>
                    <p class="text-gray-600 mb-6">Start shopping to add items to your cart</p>
                    <a href="{{ url('/shop') }}" class="inline-block bg-orange-600 text-white px-8 py-3 rounded-lg hover:bg-orange-700 transition">
                        Continue Shopping
                    </a>
                </div>
            </div>

            <!-- Order Summary -->
            <div class="lg:col-span-1">
                <div id="order-summary" class="bg-white rounded-lg shadow-md p-6 sticky top-24">
                    <h2 class="text-xl font-bold text-gray-800 mb-6">Order Summary</h2>
                    
                    <div class="space-y-4 mb-6">
                        <div class="flex justify-between">
                            <span class="text-gray-600">Subtotal</span>
                            <span class="font-semibold" id="summary-subtotal">Rp 0</span>
                        </div>
                        <div class="flex justify-between">
                            <span class="text-gray-600">Total Items</span>
                            <span class="font-semibold" id="summary-items">0</span>
                        </div>
                        <div class="border-t pt-4">
                            <div class="flex justify-between text-lg">
                                <span class="font-bold text-gray-800">Total</span>
                                <span class="font-bold text-orange-600" id="summary-total">Rp 0</span>
                            </div>
                        </div>
                    </div>

                    <button onclick="checkout()" class="w-full bg-orange-600 text-white py-3 rounded-lg hover:bg-orange-700 transition font-semibold mb-3">
                        <i class="fas fa-shopping-bag mr-2"></i> Proceed to Checkout
                    </button>
                    <a href="{{ url('/shop') }}" class="block text-center text-orange-600 hover:text-orange-700 transition">
                        <i class="fas fa-arrow-left mr-2"></i> Continue Shopping
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- Toast Notification -->
    <div id="toast" class="fixed top-20 right-4 bg-green-500 text-white px-6 py-4 rounded-lg shadow-lg transform translate-x-full transition-transform duration-300 z-50">
        <div class="flex items-center space-x-3">
            <i class="fas fa-check-circle text-2xl"></i>
            <div>
                <p class="font-semibold" id="toast-title">Success!</p>
                <p class="text-sm" id="toast-message">Action completed</p>
            </div>
        </div>
    </div>

    <script>
        const csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');
        let cartData = null;

        // Load cart items on page load
        document.addEventListener('DOMContentLoaded', loadCart);

        // Show toast notification
        function showToast(title, message, type = 'success') {
            const toast = document.getElementById('toast');
            const toastTitle = document.getElementById('toast-title');
            const toastMessage = document.getElementById('toast-message');
            
            toastTitle.textContent = title;
            toastMessage.textContent = message;
            
            if (type === 'success') {
                toast.className = toast.className.replace(/bg-\w+-500/, 'bg-green-500');
            } else if (type === 'error') {
                toast.className = toast.className.replace(/bg-\w+-500/, 'bg-red-500');
            }
            
            toast.classList.remove('translate-x-full');
            
            setTimeout(() => {
                toast.classList.add('translate-x-full');
            }, 3000);
        }

        // Load cart
        async function loadCart() {
            try {
                const response = await fetch('/api/cart', {
                    headers: {
                        'Accept': 'application/json',
                        'X-CSRF-TOKEN': csrfToken
                    }
                });

                if (response.ok) {
                    const data = await response.json();
                    cartData = data.data;
                    renderCart();
                } else {
                    showToast('Error', 'Failed to load cart', 'error');
                }
            } catch (error) {
                console.error('Error:', error);
                showToast('Error', 'Something went wrong', 'error');
            }
        }

        // Render cart
        function renderCart() {
            const cartItemsContainer = document.getElementById('cart-items');
            const emptyCart = document.getElementById('empty-cart');
            
            if (!cartData || cartData.items.length === 0) {
                cartItemsContainer.innerHTML = '';
                emptyCart.classList.remove('hidden');
                return;
            }

            emptyCart.classList.add('hidden');
            
            cartItemsContainer.innerHTML = cartData.items.map(item => `
                <div class="bg-white rounded-lg shadow-md p-6">
                    <div class="flex gap-6">
                        <div class="w-24 h-24 flex-shrink-0">
                            ${item.product && item.product.image ? 
                                `<img src="${item.product.image}" alt="${item.product.name}" class="w-full h-full object-cover rounded-lg">` :
                                `<div class="w-full h-full bg-gray-200 rounded-lg flex items-center justify-center">
                                    <i class="fas fa-image text-gray-400"></i>
                                </div>`
                            }
                        </div>
                        
                        <div class="flex-1">
                            <div class="flex justify-between mb-2">
                                <div>
                                    <h3 class="text-lg font-semibold text-gray-800">${item.product.name}</h3>
                                    ${item.product.category ? `<p class="text-sm text-gray-500">${item.product.category}</p>` : ''}
                                </div>
                                <button onclick="removeFromCart(${item.id}, '${item.product.name}')" 
                                        class="text-red-500 hover:text-red-700 transition">
                                    <i class="fas fa-trash-alt"></i>
                                </button>
                            </div>
                            
                            <p class="text-xl font-bold text-orange-600 mb-4">
                                Rp ${new Intl.NumberFormat('id-ID').format(item.product.price)}
                            </p>
                            
                            <div class="flex items-center justify-between">
                                <div class="flex items-center border border-gray-300 rounded-lg">
                                    <button onclick="updateQuantity(${item.id}, ${item.quantity - 1}, '${item.product.name}')" 
                                            class="px-3 py-1 hover:bg-gray-100 transition ${item.quantity <= 1 ? 'opacity-50 cursor-not-allowed' : ''}"
                                            ${item.quantity <= 1 ? 'disabled' : ''}>
                                        <i class="fas fa-minus text-gray-600"></i>
                                    </button>
                                    <span class="px-4 py-1 border-x border-gray-300 font-semibold">${item.quantity}</span>
                                    <button onclick="updateQuantity(${item.id}, ${item.quantity + 1}, '${item.product.name}')" 
                                            class="px-3 py-1 hover:bg-gray-100 transition ${item.quantity >= item.product.stock_quantity ? 'opacity-50 cursor-not-allowed' : ''}"
                                            ${item.quantity >= item.product.stock_quantity ? 'disabled' : ''}>
                                        <i class="fas fa-plus text-gray-600"></i>
                                    </button>
                                </div>
                                
                                <div class="text-right">
                                    <p class="text-sm text-gray-500">Subtotal</p>
                                    <p class="text-lg font-bold text-gray-800">
                                        Rp ${new Intl.NumberFormat('id-ID').format(item.subtotal)}
                                    </p>
                                </div>
                            </div>
                            
                            ${item.product.stock_quantity <= 5 ? 
                                `<p class="text-sm text-red-500 mt-2">
                                    <i class="fas fa-exclamation-circle"></i> Only ${item.product.stock_quantity} left in stock
                                </p>` : ''
                            }
                        </div>
                    </div>
                </div>
            `).join('');

            updateOrderSummary();
        }

        // Update order summary
        function updateOrderSummary() {
            if (!cartData) return;

            document.getElementById('summary-subtotal').textContent = 
                'Rp ' + new Intl.NumberFormat('id-ID').format(cartData.summary.subtotal);
            document.getElementById('summary-items').textContent = cartData.summary.total_items;
            document.getElementById('summary-total').textContent = 
                'Rp ' + new Intl.NumberFormat('id-ID').format(cartData.summary.total);
        }

        // Update quantity
        async function updateQuantity(itemId, newQuantity, productName) {
            if (newQuantity < 1) return;

            try {
                const response = await fetch(`/api/cart/update/${itemId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': csrfToken,
                        'Accept': 'application/json'
                    },
                    body: JSON.stringify({ quantity: newQuantity })
                });

                const data = await response.json();

                if (response.ok && data.status === 'success') {
                    showToast('Updated!', `Quantity updated for ${productName}`, 'success');
                    await loadCart();
                } else {
                    showToast('Error', data.message || 'Failed to update quantity', 'error');
                }
            } catch (error) {
                console.error('Error:', error);
                showToast('Error', 'Something went wrong', 'error');
            }
        }

        // Remove from cart
        async function removeFromCart(itemId, productName) {
            if (!confirm(`Remove ${productName} from cart?`)) return;

            try {
                const response = await fetch(`/api/cart/remove/${itemId}`, {
                    method: 'DELETE',
                    headers: {
                        'X-CSRF-TOKEN': csrfToken,
                        'Accept': 'application/json'
                    }
                });

                const data = await response.json();

                if (response.ok && data.status === 'success') {
                    showToast('Removed!', `${productName} removed from cart`, 'success');
                    await loadCart();
                } else {
                    showToast('Error', data.message || 'Failed to remove item', 'error');
                }
            } catch (error) {
                console.error('Error:', error);
                showToast('Error', 'Something went wrong', 'error');
            }
        }

        // Checkout function (placeholder)
        function checkout() {
            if (!cartData || cartData.items.length === 0) {
                showToast('Error', 'Your cart is empty', 'error');
                return;
            }
            
            showToast('Info', 'Checkout functionality coming soon!', 'success');
            // TODO: Implement checkout
        }
    </script>
</body>
</html>
