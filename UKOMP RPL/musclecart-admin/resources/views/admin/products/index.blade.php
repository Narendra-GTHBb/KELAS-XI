@extends('admin.layouts.app')

@section('title', 'Products')

@section('content')
@include('admin.components.delete-confirmation-modal')
@include('admin.products.partials.edit-modal')

<style>
/* Action dropdown with fixed positioning */
[id^="dropdown-product-"] {
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
    border: 1px solid #e5e7eb;
    background: white;
}

/* Ensure dropdown is always on top */
[id^="dropdown-product-"]:not(.hidden) {
    z-index: 9999 !important;
}

/* Smooth transition for dropdown */
[id^="dropdown-product-"] {
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
                <h1 class="text-3xl font-bold text-gray-900 font-inter">Products Inventory</h1>
                <p class="text-gray-600 font-inter mt-1">Manage your professional fitness equipment catalog and stock levels.</p>
            </div>
            <div class="flex space-x-3">
                <button class="flex items-center px-4 py-2.5 bg-white text-gray-700 border border-gray-300 rounded-lg font-semibold font-inter text-sm hover:bg-gray-50 transition-colors">
                    <i class="fas fa-download mr-2"></i>
                    Export CSV
                </button>
                <button onclick="openAddProductModal()" class="flex items-center px-4 py-2.5 bg-blue-600 text-white rounded-lg font-semibold font-inter text-sm hover:bg-blue-700 transition-colors">
                    <i class="fas fa-plus mr-2"></i>
                    Add Product
                </button>
            </div>
        </div>
    </div>

    <!-- Statistics Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <!-- Total Products -->
        <div class="bg-white p-6 rounded-xl border border-gray-200">
            <div class="flex items-center justify-between mb-4">
                <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                    <i class="fas fa-cube text-blue-600 text-xl"></i>
                </div>
            </div>
            <div>
                <p class="text-gray-500 font-inter text-sm font-medium">TOTAL PRODUCTS</p>
                <p class="text-2xl font-bold text-gray-900 font-inter mt-1">{{ number_format($totalProducts) }}</p>
            </div>
        </div>

        <!-- Active -->
        <div class="bg-white p-6 rounded-xl border border-gray-200">
            <div class="flex items-center justify-between mb-4">
                <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                    <i class="fas fa-check-circle text-green-600 text-xl"></i>
                </div>
            </div>
            <div>
                <p class="text-gray-500 font-inter text-sm font-medium">ACTIVE</p>
                <p class="text-2xl font-bold text-gray-900 font-inter mt-1">{{ number_format($activeProducts) }}</p>
            </div>
        </div>

        <!-- Out of Stock -->
        <div class="bg-white p-6 rounded-xl border border-gray-200">
            <div class="flex items-center justify-between mb-4">
                <div class="w-12 h-12 bg-red-100 rounded-lg flex items-center justify-center">
                    <i class="fas fa-exclamation-circle text-red-600 text-xl"></i>
                </div>
            </div>
            <div>
                <p class="text-gray-500 font-inter text-sm font-medium">OUT OF STOCK</p>
                <p class="text-2xl font-bold text-gray-900 font-inter mt-1">{{ number_format($outOfStock) }}</p>
            </div>
        </div>

        <!-- Low Stock -->
        <div class="bg-white p-6 rounded-xl border border-gray-200">
            <div class="flex items-center justify-between mb-4">
                <div class="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center">
                    <i class="fas fa-exclamation-triangle text-yellow-600 text-xl"></i>
                </div>
            </div>
            <div>
                <p class="text-gray-500 font-inter text-sm font-medium">LOW STOCK</p>
                <p class="text-2xl font-bold text-gray-900 font-inter mt-1">{{ number_format($lowStock) }}</p>
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
                       placeholder="Search by name, SKU, or category..." 
                       class="block w-full pl-10 pr-3 py-2.5 border border-gray-300 rounded-lg leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-blue-500 focus:border-blue-500 font-inter text-sm">
            </div>

            <!-- Filters -->
            <div class="flex items-center space-x-4">
                <!-- Category Filter -->
                <div class="relative">
                    <select class="appearance-none bg-white border border-gray-300 rounded-lg px-4 py-2.5 pr-8 text-sm font-inter text-gray-700 focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-blue-500">
                        <option>Category: All</option>
                        <option>Weights</option>
                        <option>Gym Gear</option>
                        <option>Accessories</option>
                        <option>Yoga & Pilates</option>
                    </select>
                    <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                        <i class="fas fa-chevron-down text-gray-400 text-xs"></i>
                    </div>
                </div>

                <!-- Status Filter -->
                <div class="relative">
                    <select class="appearance-none bg-white border border-gray-300 rounded-lg px-4 py-2.5 pr-8 text-sm font-inter text-gray-700 focus:outline-none focus:ring-1 focus:ring-blue-500 focus:border-blue-500">
                        <option>Status: All</option>
                        <option>Active</option>
                        <option>Draft</option>
                        <option>Out of Stock</option>
                    </select>
                    <div class="absolute inset-y-0 right-0 flex items-center px-2 pointer-events-none">
                        <i class="fas fa-chevron-down text-gray-400 text-xs"></i>
                    </div>
                </div>

                <!-- Reset Filters -->
                <button class="text-blue-600 font-medium text-sm font-inter hover:text-blue-800 transition-colors">
                    Reset Filters
                </button>
            </div>
        </div>
    </div>

    <!-- Products Table -->
    <div class="bg-white rounded-xl border border-gray-200">
        <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">PRODUCT INFO</th>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">CATEGORY</th>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">STOCK LEVEL</th>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">PRICE</th>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">STATUS</th>
                        <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider font-inter">ACTIONS</th>
                    </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                    @forelse($products as $product)
                    <tr class="hover:bg-gray-50">
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="flex items-center">
                                @if($product->image_url && Storage::disk('public')->exists('products/' . $product->image_url))
                                    <img src="{{ Storage::url('products/' . $product->image_url) }}" alt="{{ $product->name }}" class="w-12 h-12 rounded-lg object-cover border border-gray-200">
                                @else
                                    <img src="https://ui-avatars.com/api/?name={{ urlencode($product->name) }}&size=128&background=random&color=fff&bold=true" alt="{{ $product->name }}" class="w-12 h-12 rounded-lg object-cover border border-gray-200">
                                @endif
                                <div class="ml-4">
                                    <div class="text-sm font-medium text-gray-900 font-inter">{{ $product->name }}</div>
                                    <div class="text-sm text-gray-500 font-inter">SKU: {{ $product->sku ?? 'N/A' }}</div>
                                </div>
                            </div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600 font-inter">{{ $product->category->name ?? 'N/A' }}</td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="flex items-center">
                                @if($product->stock_quantity == 0)
                                    <span class="w-2 h-2 bg-red-500 rounded-full mr-2"></span>
                                    <span class="text-sm font-medium text-gray-900 font-inter">0 Units</span>
                                @elseif($product->stock_quantity <= ($product->low_stock_threshold ?? 10))
                                    <span class="w-2 h-2 bg-yellow-500 rounded-full mr-2"></span>
                                    <span class="text-sm font-medium text-gray-900 font-inter">{{ $product->stock_quantity }} Units</span>
                                    <span class="ml-2 text-xs bg-yellow-100 text-yellow-800 px-2 py-0.5 rounded font-inter">LOW</span>
                                @else
                                    <span class="w-2 h-2 bg-green-500 rounded-full mr-2"></span>
                                    <span class="text-sm font-medium text-gray-900 font-inter">{{ $product->stock_quantity }} Units</span>
                                @endif
                            </div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-semibold text-gray-900 font-inter">Rp {{ number_format($product->price, 0, ',', '.') }}</td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium {{ $product->is_active ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800' }} font-inter">
                                {{ $product->is_active ? 'Active' : 'Draft' }}
                            </span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                            <div class="relative">
                                <button id="btn-product-{{ $product->id }}" onclick="event.stopPropagation(); toggleActionDropdown('dropdown-product-{{ $product->id }}', this)" class="text-gray-400 hover:text-gray-600 p-2 rounded-lg hover:bg-gray-100 transition-colors">
                                    <i class="fas fa-ellipsis-v"></i>
                                </button>
                                <div id="dropdown-product-{{ $product->id }}" class="hidden fixed bg-white rounded-lg shadow-2xl border border-gray-200" style="min-width: 200px; z-index: 9999;">
                                    <div class="py-1">
                                        <button 
                                            onclick="openEditProductModal(this)" 
                                            data-product-id="{{ $product->id }}"
                                            data-product-name="{{ $product->name }}"
                                            data-product-description="{{ $product->description }}"
                                            data-product-price="{{ round($product->price) }}"
                                            data-product-stock="{{ $product->stock_quantity }}"
                                            data-product-category="{{ $product->category_id }}"
                                            data-product-sku="{{ $product->sku }}"
                                            data-product-brand="{{ $product->brand }}"
                                            data-product-weight="{{ $product->weight }}"
                                            data-product-threshold="{{ $product->low_stock_threshold }}"
                                            data-product-featured="{{ $product->is_featured ? '1' : '0' }}"
                                            data-product-active="{{ $product->is_active ? '1' : '0' }}"
                                            data-product-image="{{ $product->image_url && Storage::disk('public')->exists('products/' . $product->image_url) ? Storage::url('products/' . $product->image_url) : '' }}"
                                            class="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-600 font-inter">
                                            <i class="fas fa-edit mr-3 text-blue-500"></i>
                                            Edit Product
                                        </button>
                                        <button 
                                            onclick="confirmDeleteProduct(this)"
                                            data-delete-url="/admin/products/{{ $product->id }}"
                                            data-product-name="{{ $product->name }}"
                                            data-product-sku="{{ $product->sku ?? 'N/A' }}"
                                            class="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-red-50 hover:text-red-600 font-inter">
                                            <i class="fas fa-trash mr-3 text-red-500"></i>
                                            Delete Product
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </td>
                    </tr>
                    @empty
                    <tr>
                        <td colspan="6" class="py-12 px-6 text-center">
                            <div class="flex flex-col items-center">
                                <div class="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
                                    <i class="fas fa-cube text-gray-400 text-xl"></i>
                                </div>
                                <h3 class="text-lg font-semibold text-gray-900 font-inter mb-2">No products found</h3>
                                <p class="text-gray-600 font-inter mb-4">Get started by adding your first product.</p>
                                <button onclick="openAddProductModal()" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-inter font-medium">
                                    <i class="fas fa-plus mr-2"></i>Add Product
                                </button>
                            </div>
                        </td>
                    </tr>
                    @endforelse
                </tbody>
            </table>
        </div>

        <!-- Pagination -->
        <div class="px-6 py-4 border-t border-gray-200">
            {{ $products->links('vendor.pagination.tailwind') }}
        </div>
    </div>
</div>

<!-- Add Product Modal -->
<div id="addProductModal" class="hidden fixed inset-0 z-50 overflow-y-auto">
    <!-- Background blur overlay -->
    <div class="fixed inset-0 modal-backdrop transition-opacity">
    </div>
    
    <!-- Modal container -->
    <div class="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:block sm:p-0">
        <!-- Modal content -->
        <div id="modalContent" class="modal-content relative inline-block w-full max-w-4xl px-4 pt-5 pb-4 overflow-hidden text-left align-bottom bg-white shadow-2xl rounded-2xl sm:my-8 sm:align-middle sm:max-w-4xl sm:w-full sm:p-8">
            <!-- Modal header -->
            <div class="flex items-center justify-between mb-8">
                <div class="flex items-center">
                    <div class="w-10 h-10 bg-blue-100 rounded-xl flex items-center justify-center mr-4">
                        <i class="fas fa-plus text-blue-600"></i>
                    </div>
                    <div>
                        <h3 class="text-2xl font-bold text-gray-900 font-inter">Create New Product</h3>
                        <p class="text-sm text-gray-600 font-inter mt-1">Add a new fitness product to your inventory</p>
                    </div>
                </div>
                <button onclick="closeAddProductModal()" class="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-lg transition-colors">
                    <i class="fas fa-times text-lg"></i>
                </button>
            </div>

            <!-- Modal form -->
            <form id="addProductForm" action="{{ route('admin.products.store') }}" method="POST" enctype="multipart/form-data">
                @csrf
                
                <div class="space-y-8">
                    <!-- Basic Information Section -->
                    <div class="bg-gray-50 rounded-xl p-6">
                        <div class="flex items-center mb-6">
                            <div class="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center mr-3">
                                <i class="fas fa-info-circle text-white text-sm"></i>
                            </div>
                            <h4 class="text-xl font-semibold text-gray-900 font-inter">Basic Information</h4>
                        </div>
                        
                        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
                            <div class="lg:col-span-2">
                                <label for="product_name" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Product Name <span class="text-red-500">*</span></label>
                                <input type="text" name="name" id="product_name" placeholder="e.g Pro-Series Adjustable Dumbbells" required
                                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm">
                                <span class="name-error text-red-500 text-sm hidden"></span>
                            </div>
                            
                            <div>
                                <label for="category_id" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Category <span class="text-red-500">*</span></label>
                                <select name="category_id" id="category_id" required
                                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm">
                                    <option value="">Select Category</option>
                                    @foreach($categories as $category)
                                        <option value="{{ $category->id }}">{{ $category->name }}</option>
                                    @endforeach
                                </select>
                                <span class="category-id-error text-red-500 text-sm hidden"></span>
                            </div>
                            
                            <div>
                                <label for="price" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Harga (IDR) <span class="text-red-500">*</span></label>
                                <div class="relative">
                                    <span class="absolute left-3 top-3 text-gray-500 font-inter">Rp</span>
                                    <input type="number" name="price" id="price" placeholder="0" min="0" step="1" required
                                        class="w-full border border-gray-300 rounded-lg pl-8 pr-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm">
                                    <span class="price-error text-red-500 text-sm hidden"></span>
                                </div>
                            </div>
                            
                            <div>
                                <label for="brand" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Brand</label>
                                <input type="text" name="brand" id="brand" placeholder="e.g Nike, Adidas"
                                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm">
                            </div>
                            
                            <div>
                                <label for="weight" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Weight</label>
                                <div class="flex gap-2">
                                    <input type="number" name="weight" id="weight" placeholder="0" min="0" step="0.1"
                                        class="flex-1 border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm">
                                    <select name="weight_unit" id="weight_unit" class="border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm">
                                        <option value="gr">gr</option>
                                        <option value="kg" selected>kg</option>
                                    </select>
                                </div>
                            </div>

                            <div class="flex items-center space-x-6">
                                <label class="flex items-center">
                                    <input type="checkbox" name="is_featured" value="1" id="is_featured"
                                        class="w-5 h-5 text-blue-600 border-gray-300 rounded focus:ring-blue-500">
                                    <span class="ml-2 text-sm text-gray-700 font-inter">Featured Product</span>
                                </label>
                                <label class="flex items-center">
                                    <input type="checkbox" name="is_active" value="1" id="is_active" checked
                                        class="w-5 h-5 text-blue-600 border-gray-300 rounded focus:ring-blue-500">
                                    <span class="ml-2 text-sm text-gray-700 font-inter">Active</span>
                                </label>
                            </div>
                        </div>
                    </div>

                    <!-- Product Description Section -->
                    <div class="bg-gray-50 rounded-xl p-6">
                        <div class="flex items-center mb-6">
                            <div class="w-8 h-8 bg-purple-600 rounded-lg flex items-center justify-center mr-3">
                                <i class="fas fa-align-left text-white text-sm"></i>
                            </div>
                            <h4 class="text-xl font-semibold text-gray-900 font-inter">Product Description</h4>
                        </div>
                        
                        <div>
                            <label for="description" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Description <span class="text-red-500">*</span></label>
                            <textarea name="description" id="description" rows="5" placeholder="Enter detailed product specifications, materials, and features" required
                                class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm resize-none"></textarea>
                            <span class="description-error text-red-500 text-sm hidden"></span>
                        </div>
                    </div>

                    <!-- Product Media Section -->
                    <div class="bg-gray-50 rounded-xl p-6">
                        <div class="flex items-center mb-6">
                            <div class="w-8 h-8 bg-green-600 rounded-lg flex items-center justify-center mr-3">
                                <i class="fas fa-image text-white text-sm"></i>
                            </div>
                            <h4 class="text-xl font-semibold text-gray-900 font-inter">Product Media</h4>
                        </div>
                        
                        <div>
                            <label for="image" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Product Image</label>
                            <input type="file" name="image" id="image" accept="image/png,image/jpeg,image/jpg,image/gif,image/webp"
                                class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100">
                            <p class="text-xs text-gray-500 mt-2 font-inter">PNG, JPG, GIF atau WEBP (Max 2MB)</p>
                        </div>
                    </div>

                    <!-- Inventory Tracking Section -->
                    <div class="bg-gray-50 rounded-xl p-6">
                        <div class="flex items-center mb-6">
                            <div class="w-8 h-8 bg-orange-600 rounded-lg flex items-center justify-center mr-3">
                                <i class="fas fa-boxes text-white text-sm"></i>
                            </div>
                            <h4 class="text-xl font-semibold text-gray-900 font-inter">Inventory Tracking</h4>
                        </div>
                        
                        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
                            <div>
                                <label for="stock_quantity" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Current Stock Quantity <span class="text-red-500">*</span></label>
                                <input type="number" name="stock_quantity" id="stock_quantity" placeholder="0" min="0" required
                                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm">
                                <p class="text-xs text-gray-500 mt-1 font-inter">Total units available in warehouse</p>
                                <span class="stock-quantity-error text-red-500 text-sm hidden"></span>
                            </div>
                            
                            <div>
                                <label for="low_stock_threshold" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Low Stock Alert Threshold</label>
                                <input type="number" name="low_stock_threshold" id="low_stock_threshold" placeholder="5" min="0" 
                                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm">
                                <p class="text-xs text-gray-500 mt-1 font-inter">Get notified when stock falls below this number</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Modal footer -->
                <div class="flex items-center justify-end space-x-4 mt-8 pt-6 border-t border-gray-200">
                    <button type="button" onclick="closeAddProductModal()" 
                        class="px-6 py-3 text-gray-700 bg-white border border-gray-300 rounded-lg font-semibold font-inter text-sm hover:bg-gray-50 transition-colors">
                        Cancel
                    </button>
                    <button type="submit" 
                        class="px-8 py-3 bg-blue-600 text-white rounded-lg font-semibold font-inter text-sm hover:bg-blue-700 transition-colors flex items-center">
                        <i class="fas fa-plus mr-2"></i>
                        Add Product
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Success Modal -->
<div id="successModal" class="hidden fixed inset-0 z-[60] overflow-y-auto">
    <!-- Background overlay -->
    <div class="fixed inset-0 bg-gray-900 bg-opacity-75 transition-opacity"></div>
    
    <!-- Modal container -->
    <div class="flex items-center justify-center min-h-screen px-4 py-6">
        <!-- Modal content -->
        <div class="relative bg-white rounded-2xl shadow-2xl max-w-md w-full p-8 transform transition-all">
            <!-- Success Icon -->
            <div class="flex justify-center mb-6">
                <div class="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center">
                    <div class="w-16 h-16 bg-green-500 rounded-full flex items-center justify-center">
                        <i class="fas fa-check text-white text-3xl"></i>
                    </div>
                </div>
            </div>
            
            <!-- Title -->
            <h3 class="text-2xl font-bold text-gray-900 text-center mb-2 font-inter">Product Added Successfully</h3>
            <p class="text-sm text-gray-600 text-center mb-6 font-inter">Your new item is now live and visible to customers in the <span class="font-semibold">MuscleCart</span> store.</p>
            
            <!-- Product Preview Card -->
            <div class="bg-gray-50 rounded-xl p-4 mb-6">
                <div class="flex items-center gap-4">
                    <!-- Product Image -->
                    <div id="success-product-image" class="w-16 h-16 rounded-lg bg-gray-200 flex items-center justify-center overflow-hidden">
                        <i class="fas fa-box text-gray-400 text-2xl"></i>
                    </div>
                    
                    <!-- Product Info -->
                    <div class="flex-1">
                        <h4 id="success-product-name" class="text-base font-semibold text-gray-900 font-inter mb-1">Product Name</h4>
                        <div class="flex items-center gap-2">
                            <span id="success-product-badge" class="px-2 py-1 bg-green-100 text-green-700 text-xs font-semibold rounded font-inter">ACTIVE</span>
                            <span id="success-product-price" class="text-sm font-bold text-blue-600 font-inter">Rp 0</span>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Action Button -->
            <button onclick="closeSuccessModal()" class="w-full px-6 py-3 bg-blue-600 text-white rounded-lg font-semibold font-inter text-sm hover:bg-blue-700 transition-colors">
                Back to Products
            </button>
            
            <!-- Countdown Timer -->
            <p class="text-xs text-gray-500 text-center mt-4 font-inter">
                <i class="fas fa-clock mr-1"></i>
                Redirecting to inventory in <span id="countdown">5</span> seconds...
            </p>
        </div>
    </div>
</div>

<script>
// Modal control functions
function openAddProductModal() {
    const modal = document.getElementById('addProductModal');
    const modalContent = document.getElementById('modalContent');
    
    modal.classList.remove('hidden');
    document.body.style.overflow = 'hidden';
    
    // Add animation
    setTimeout(() => {
        modalContent.classList.add('show');
    }, 10);
    
    loadCategories();
}

function closeAddProductModal() {
    const modal = document.getElementById('addProductModal');
    const modalContent = document.getElementById('modalContent');
    
    modalContent.classList.remove('show');
    
    setTimeout(() => {
        modal.classList.add('hidden');
        document.body.style.overflow = 'auto';
        document.getElementById('addProductForm').reset();
        clearErrors();
    }, 200);
}

// Success Modal functions
let countdownTimer;
function showSuccessModal(product) {
    const modal = document.getElementById('successModal');
    
    // Populate product data
    document.getElementById('success-product-name').textContent = product.name || 'Product Name';
    document.getElementById('success-product-price').textContent = 'Rp ' + (product.price ? new Intl.NumberFormat('id-ID').format(product.price) : '0');
    
    // Set badge based on is_active
    const badge = document.getElementById('success-product-badge');
    if (product.is_active) {
        badge.textContent = 'ACTIVE';
        badge.className = 'px-2 py-1 bg-green-100 text-green-700 text-xs font-semibold rounded font-inter';
    } else {
        badge.textContent = 'INACTIVE';
        badge.className = 'px-2 py-1 bg-gray-100 text-gray-700 text-xs font-semibold rounded font-inter';
    }
    
    // Set product image
    const imageContainer = document.getElementById('success-product-image');
    if (product.image_url) {
        imageContainer.innerHTML = `<img src="/storage/products/${product.image_url}" alt="${product.name}" class="w-full h-full object-cover rounded-lg">`;
    } else {
        // Use UI Avatars as fallback
        const initials = product.name.split(' ').map(word => word[0]).join('').substring(0, 2).toUpperCase();
        const colors = ['3B82F6', 'EF4444', '10B981', 'F59E0B', '8B5CF6', 'EC4899'];
        const randomColor = colors[Math.floor(Math.random() * colors.length)];
        imageContainer.innerHTML = `<img src="https://ui-avatars.com/api/?name=${encodeURIComponent(initials)}&background=${randomColor}&color=fff&size=128&bold=true" alt="${product.name}" class="w-full h-full object-cover rounded-lg">`;
    }
    
    // Show modal
    modal.classList.remove('hidden');
    
    // Start countdown
    let countdown = 5;
    document.getElementById('countdown').textContent = countdown;
    
    clearInterval(countdownTimer);
    countdownTimer = setInterval(() => {
        countdown--;
        document.getElementById('countdown').textContent = countdown;
        
        if (countdown <= 0) {
            clearInterval(countdownTimer);
            closeSuccessModal();
        }
    }, 1000);
}

function closeSuccessModal() {
    clearInterval(countdownTimer);
    const modal = document.getElementById('successModal');
    modal.classList.add('hidden');
    document.body.style.overflow = 'auto';
    window.location.reload();
}

// Edit Product Modal functions
function openEditProductModal(button) {
    // Get data from button attributes
    const productId = button.getAttribute('data-product-id');
    const productName = button.getAttribute('data-product-name');
    const productDescription = button.getAttribute('data-product-description');
    const productPrice = button.getAttribute('data-product-price');
    const productStock = button.getAttribute('data-product-stock');
    const productCategory = button.getAttribute('data-product-category');
    const productBrand = button.getAttribute('data-product-brand');
    const productWeight = button.getAttribute('data-product-weight');
    const productThreshold = button.getAttribute('data-product-threshold');
    const productImage = button.getAttribute('data-product-image');
    const isFeatured = button.getAttribute('data-product-featured') === '1';
    const isActive = button.getAttribute('data-product-active') === '1';
    
    // Close any open dropdowns
    document.querySelectorAll('[id^="dropdown-product-"]').forEach(dropdown => {
        dropdown.classList.add('hidden');
    });
    
    // Set form action URL
    const form = document.getElementById('editProductForm');
    form.action = '/admin/products/' + productId;
    
    // Populate form fields
    document.getElementById('edit_product_name').value = productName || '';
    document.getElementById('edit_description').value = productDescription || '';
    document.getElementById('edit_price').value = productPrice || '';
    document.getElementById('edit_stock_quantity').value = productStock || '';
    document.getElementById('edit_category_id').value = productCategory || '';
    document.getElementById('edit_brand').value = productBrand || '';
    
    // Display current product image
    const currentImageEl = document.getElementById('edit_current_image');
    const noImageEl = document.getElementById('edit_no_current_image');
    if (productImage && productImage.trim() !== '') {
        // Add timestamp to prevent caching
        currentImageEl.src = productImage + '?t=' + new Date().getTime();
        currentImageEl.classList.remove('hidden');
        noImageEl.classList.add('hidden');
    } else {
        currentImageEl.classList.add('hidden');
        noImageEl.classList.remove('hidden');
    }
    
    // Reset image preview
    clearEditImagePreview();
    
    // Smart weight display - show in grams if less than 1 kg, otherwise in kg
    if (productWeight) {
        const weightInKg = parseFloat(productWeight);
        if (weightInKg < 1) {
            // Convert to grams for better readability
            document.getElementById('edit_weight').value = Math.round(weightInKg * 1000);
            document.getElementById('edit_weight_unit').value = 'gr';
        } else {
            // Keep in kg
            document.getElementById('edit_weight').value = weightInKg;
            document.getElementById('edit_weight_unit').value = 'kg';
        }
    } else {
        document.getElementById('edit_weight').value = '';
        document.getElementById('edit_weight_unit').value = 'kg';
    }
    
    document.getElementById('edit_low_stock_threshold').value = productThreshold || '';
    document.getElementById('edit_is_featured').checked = isFeatured;
    document.getElementById('edit_is_active').checked = isActive;
    
    // Show modal
    const modal = document.getElementById('editProductModal');
    const modalContent = document.getElementById('editModalContent');
    
    modal.classList.remove('hidden');
    document.body.style.overflow = 'hidden';
    
    // Add animation
    setTimeout(() => {
        modalContent.classList.add('show');
    }, 10);
}

function closeEditProductModal() {
    const modal = document.getElementById('editProductModal');
    const modalContent = document.getElementById('editModalContent');
    
    modalContent.classList.remove('show');
    
    setTimeout(() => {
        modal.classList.add('hidden');
        document.body.style.overflow = 'auto';
        document.getElementById('editProductForm').reset();
        clearEditErrors();
        clearEditImagePreview();
    }, 200);
}

function clearEditErrors() {
    document.querySelectorAll('[class*="edit-"][class*="-error"]').forEach(el => {
        el.classList.add('hidden');
        el.textContent = '';
    });
}

// Preview image when file is selected in edit modal
function previewEditImage(input) {
    const previewContainer = document.getElementById('edit_image_preview_container');
    const preview = document.getElementById('edit_image_preview');
    
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        
        reader.onload = function(e) {
            preview.src = e.target.result;
            previewContainer.classList.remove('hidden');
        }
        
        reader.readAsDataURL(input.files[0]);
    } else {
        clearEditImagePreview();
    }
}

// Clear image preview in edit modal
function clearEditImagePreview() {
    const input = document.getElementById('edit_image');
    const previewContainer = document.getElementById('edit_image_preview_container');
    const preview = document.getElementById('edit_image_preview');
    
    if (input) input.value = '';
    if (preview) preview.src = '';
    if (previewContainer) previewContainer.classList.add('hidden');
}

// Load categories dynamically
async function loadCategories() {
    try {
        const response = await fetch('/admin/api/categories');
        const categories = await response.json();
        const select = document.getElementById('category_id');
        
        select.innerHTML = '<option value="">Select Category</option>';
        categories.forEach(category => {
            select.innerHTML += `<option value="${category.id}">${category.name}</option>`;
        });
    } catch (error) {
        console.error('Error loading categories:', error);
        // Fallback - show message if categories can't be loaded
        const select = document.getElementById('category_id');
        select.innerHTML = '<option value="">Unable to load categories</option>';
    }
}

// Form validation and error handling
function clearErrors() {
    document.querySelectorAll('[class*="-error"]').forEach(el => {
        el.classList.add('hidden');
        el.textContent = '';
    });
    document.querySelectorAll('input, select, textarea').forEach(el => {
        el.classList.remove('border-red-500');
    });
}

function showFieldError(fieldName, message) {
    const errorElement = document.querySelector(`.${fieldName.replace(/_/g, '-')}-error`);
    const inputElement = document.querySelector(`[name="${fieldName}"]`);
    
    console.log('Showing error for field:', fieldName, 'Error class:', fieldName.replace(/_/g, '-') + '-error');
    
    if (errorElement && inputElement) {
        errorElement.textContent = message;
        errorElement.classList.remove('hidden');
        inputElement.classList.add('border-red-500');
        
        // Scroll to first error
        const firstError = document.querySelector('.border-red-500');
        if (inputElement === firstError) {
            inputElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    } else {
        console.warn('Error element or input not found for field:', fieldName);
    }
}

// Handle form submission
document.getElementById('addProductForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    clearErrors();
    
    const formData = new FormData(this);
    const submitButton = this.querySelector('button[type="submit"]');
    const originalText = submitButton.innerHTML;
    
    console.log('=== FORM SUBMISSION STARTED ===');
    console.log('Form action:', this.action);
    console.log('Form method:', this.method);
    console.log('FormData entries:');
    for (let [key, value] of formData.entries()) {
        if (value instanceof File) {
            console.log(key + ': [FILE]', value.name, value.size, 'bytes');
        } else {
            console.log(key + ':', value);
        }
    }
    
    // Show loading state
    submitButton.innerHTML = '<i class="fas fa-spinner animate-spin mr-2"></i>Adding Product...';
    submitButton.disabled = true;
    
    try {
        console.log('Sending fetch request...');
        const response = await fetch(this.action, {
            method: 'POST',
            body: formData,
            headers: {
                'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content'),
                'Accept': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            }
        });
        
        console.log('Response received');
        console.log('Response status:', response.status);
        console.log('Response ok:', response.ok);
        
        // Get response text
        const responseText = await response.text();
        console.log('Response text length:', responseText.length);
        console.log('Response text:', responseText.substring(0, 500)); // First 500 chars
        
        // Try to parse as JSON
        let result;
        try {
            result = JSON.parse(responseText);
            console.log('Parsed JSON successfully:', result);
        } catch (jsonError) {
            console.error('JSON parse error:', jsonError);
            alert('ERROR: Server returned invalid JSON response.\n\nStatus: ' + response.status + '\n\nResponse preview:\n' + responseText.substring(0, 200));
            submitButton.innerHTML = originalText;
            submitButton.disabled = false;
            return;
        }
        
        if (response.ok && result.success) {
            console.log('SUCCESS! Product created:', result.product);
            closeAddProductModal();
            // Show success modal with product data
            showSuccessModal(result.product);
        } else {
            console.log('ERROR Response');
            // Handle validation errors
            if (result.errors) {
                console.log('Validation errors found:', result.errors);
                let errorMsg = 'Validation Errors:\n';
                Object.keys(result.errors).forEach(field => {
                    const errors = result.errors[field];
                    errorMsg += `\n${field}: ${errors.join(', ')}`;
                    showFieldError(field, errors[0]);
                });
                alert(errorMsg);
            } else {
                alert('Error: ' + (result.message || 'Unknown error occurred'));
            }
        }
    } catch (error) {
        console.error('CATCH ERROR:', error);
        console.error('Error stack:', error.stack);
        alert('Network Error:\n' + error.message + '\n\nCheck browser console (F12) for details.');
    } finally {
        console.log('=== FORM SUBMISSION ENDED ===');
        // Reset button state
        submitButton.innerHTML = originalText;
        submitButton.disabled = false;
    }
});

// Close modal when clicking outside
document.getElementById('addProductModal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeAddProductModal();
    }
});

// Close edit modal when clicking outside
document.getElementById('editProductModal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeEditProductModal();
    }
});

// Handle Edit Product form submission
document.getElementById('editProductForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    clearEditErrors();
    
    const formData = new FormData(this);
    const submitButton = this.querySelector('button[type="submit"]');
    const originalText = submitButton.innerHTML;
    
    // Debug: log form data
    console.log('Form action:', this.action);
    console.log('Form data entries:');
    for (let [key, value] of formData.entries()) {
        console.log(key, value);
    }
    
    // Show loading state
    submitButton.innerHTML = '<i class="fas fa-spinner animate-spin mr-2"></i>Updating...';
    submitButton.disabled = true;
    
    try {
        const response = await fetch(this.action, {
            method: 'POST',
            body: formData,
            headers: {
                'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content'),
                'Accept': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            }
        });
        
        console.log('Response status:', response.status);
        const result = await response.json();
        console.log('Response result:', result);
        
        if (response.ok) {
            // Success - show success message and refresh page
            closeEditProductModal();
            
            // Create success notification
            const notification = document.createElement('div');
            notification.className = 'fixed top-4 right-4 bg-green-500 text-white px-6 py-3 rounded-lg shadow-lg z-50';
            notification.innerHTML = '<i class="fas fa-check mr-2"></i>Product updated successfully!';
            document.body.appendChild(notification);
            
            // Remove notification and refresh after delay
            setTimeout(() => {
                notification.remove();
                window.location.reload();
            }, 2000);
        } else {
            // Handle validation errors
            if (result.errors) {
                console.error('Validation errors:', result.errors);
                Object.keys(result.errors).forEach(field => {
                    const errorEl = document.querySelector(`.edit-${field}-error`);
                    if (errorEl) {
                        errorEl.textContent = result.errors[field][0];
                        errorEl.classList.remove('hidden');
                    }
                });
            } else {
                console.error('Error:', result.message);
                alert('An error occurred: ' + (result.message || 'Please check your input and try again.'));
            }
        }
    } catch (error) {
        console.error('Error submitting form:', error);
        alert('A network error occurred. Please check your connection and try again.');
    } finally {
        // Reset button state
        submitButton.innerHTML = originalText;
        submitButton.disabled = false;
    }
});

// Close modal when clicking outside

// Close modal with Escape key
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape' && !document.getElementById('addProductModal').classList.contains('hidden')) {
        closeAddProductModal();
    }
});

// Price input - no auto-formatting to avoid adding unwanted decimals

// Character counter for description
document.getElementById('description').addEventListener('input', function(e) {
    const maxLength = 1000;
    const currentLength = e.target.value.length;
    let counter = document.getElementById('descriptionCounter');
    
    if (!counter) {
        counter = document.createElement('div');
        counter.id = 'descriptionCounter';
        counter.className = 'text-xs text-gray-500 mt-1 font-inter';
        e.target.parentNode.appendChild(counter);
    }
    
    counter.textContent = `${currentLength}/${maxLength} characters`;
    
    if (currentLength > maxLength) {
        counter.className = 'text-xs text-red-500 mt-1 font-inter';
        e.target.value = e.target.value.substring(0, maxLength);
    } else {
        counter.className = 'text-xs text-gray-500 mt-1 font-inter';
    }
});

// Dropdown functions for actions  
function toggleActionDropdown(dropdownId, buttonElement) {
    event.stopPropagation(); // Prevent event from bubbling
    
    // Close all other dropdowns
    document.querySelectorAll('[id^="dropdown-product-"]').forEach(dropdown => {
        if (dropdown.id !== dropdownId) {
            dropdown.classList.add('hidden');
        }
    });
    
    // Toggle the clicked dropdown
    const dropdown = document.getElementById(dropdownId);
    if (dropdown) {
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
            let top = buttonRect.bottom + 8; // 8px below button
            let left = buttonRect.right - dropdownRect.width; // Align to right of button
            
            // Check if dropdown goes below viewport
            if (top + dropdownRect.height > viewportHeight) {
                // Position above button instead
                top = buttonRect.top - dropdownRect.height - 8;
            }
            
            // Check if dropdown goes beyond left edge
            if (left < 10) {
                left = 10; // 10px from left edge
            }
            
            // Check if dropdown goes beyond right edge
            if (left + dropdownRect.width > viewportWidth - 10) {
                left = viewportWidth - dropdownRect.width - 10; // 10px from right edge
            }
            
            // Apply position
            dropdown.style.top = top + 'px';
            dropdown.style.left = left + 'px';
        } else {
            // Hide dropdown
            dropdown.classList.add('hidden');
        }
    }
}

// Close dropdowns when clicking outside
document.addEventListener('click', function(event) {
    // Close all dropdowns
    document.querySelectorAll('[id^="dropdown-product-"]').forEach(dropdown => {
        dropdown.classList.add('hidden');
    });
});

// Close dropdowns on scroll
window.addEventListener('scroll', function() {
    document.querySelectorAll('[id^="dropdown-product-"]').forEach(dropdown => {
        dropdown.classList.add('hidden');
    });
}, true);

// Close dropdowns on window resize
window.addEventListener('resize', function() {
    document.querySelectorAll('[id^="dropdown-product-"]').forEach(dropdown => {
        dropdown.classList.add('hidden');
    });
});

// Confirm delete product function
function confirmDeleteProduct(button) {
    const deleteUrl = button.getAttribute('data-delete-url');
    const productName = button.getAttribute('data-product-name');
    const productSku = button.getAttribute('data-product-sku');
    
    // Close any open dropdowns
    document.querySelectorAll('[id^="dropdown-product-"]').forEach(dropdown => {
        dropdown.classList.add('hidden');
    });
    
    // Create config object for delete modal
    const config = {
        title: 'Delete Product?',
        message: 'You are about to permanently delete this product from your inventory. This action will also remove it from your online storefront and cannot be undone.',
        deleteUrl: deleteUrl,
        itemName: productName,
        itemDetails: 'SKU: ' + productSku,
        iconClass: 'fas fa-cube text-gray-600',
        iconBgClass: 'bg-gray-200'
    };
    
    // Open delete confirmation modal
    openDeleteModal(config);
}

// Confirm delete function
function confirmDelete(deleteUrl, itemName, itemDetails) {
    // Close any open dropdowns
    document.querySelectorAll('[id^="dropdown-"]').forEach(dropdown => {
        dropdown.classList.add('hidden');
    });
    
    // Create config object
    const config = {
        title: 'Delete Product?',
        message: 'You are about to permanently delete this product from your inventory. This action will also remove it from your online storefront and cannot be undone.',
        deleteUrl: deleteUrl,
        itemName: itemName,
        itemDetails: itemDetails,
        iconClass: 'fas fa-cube text-gray-600',
        iconBgClass: 'bg-gray-200'
    };
    
    // Open delete confirmation modal
    openDeleteModal(config);
}
</script>
@endsection
