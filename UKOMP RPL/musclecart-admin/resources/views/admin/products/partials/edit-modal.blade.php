<!-- Edit Product Modal -->
<div id="editProductModal" class="hidden fixed inset-0 z-50 overflow-y-auto">
    <!-- Background blur overlay -->
    <div class="fixed inset-0 modal-backdrop transition-opacity"></div>
    
    <!-- Modal container -->
    <div class="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:block sm:p-0">
        <!-- Modal content -->
        <div id="editModalContent" class="modal-content relative inline-block w-full max-w-4xl px-4 pt-5 pb-4 overflow-hidden text-left align-bottom bg-white shadow-2xl rounded-2xl sm:my-8 sm:align-middle sm:max-w-4xl sm:w-full sm:p-8">
            <!-- Modal header -->
            <div class="flex items-center justify-between mb-8">
                <div class="flex items-center">
                    <div class="w-10 h-10 bg-blue-100 rounded-xl flex items-center justify-center mr-4">
                        <i class="fas fa-edit text-blue-600"></i>
                    </div>
                    <div>
                        <h3 class="text-2xl font-bold text-gray-900 font-inter">Edit Product</h3>
                        <p class="text-sm text-gray-600 font-inter mt-1">Update product information and inventory</p>
                    </div>
                </div>
                <button onclick="closeEditProductModal()" class="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-lg transition-colors">
                    <i class="fas fa-times text-lg"></i>
                </button>
            </div>

            <!-- Modal form -->
            <form id="editProductForm" method="POST" enctype="multipart/form-data">
                @csrf
                @method('PUT')
                
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
                                <label for="edit_product_name" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Product Name <span class="text-red-500">*</span></label>
                                <input type="text" name="name" id="edit_product_name" placeholder="e.g Pro-Series Adjustable Dumbbells" required
                                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm">
                                <span class="edit-product-name-error text-red-500 text-sm hidden"></span>
                            </div>
                            
                            <div>
                                <label for="edit_category_id" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Category <span class="text-red-500">*</span></label>
                                <select name="category_id" id="edit_category_id" required
                                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm">
                                    <option value="">Select Category</option>
                                    @foreach($categories as $category)
                                        <option value="{{ $category->id }}">{{ $category->name }}</option>
                                    @endforeach
                                </select>
                                <span class="edit-category-error text-red-500 text-sm hidden"></span>
                            </div>
                            
                            <div>
                                <label for="edit_price" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Harga (IDR) <span class="text-red-500">*</span></label>
                                <div class="relative">
                                    <span class="absolute left-3 top-3 text-gray-500 font-inter">Rp</span>
                                    <input type="number" name="price" id="edit_price" placeholder="0" min="0" step="1" required
                                        class="w-full border border-gray-300 rounded-lg pl-8 pr-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm">
                                    <span class="edit-price-error text-red-500 text-sm hidden"></span>
                                </div>
                            </div>
                            
                            <div>
                                <label for="edit_brand" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Brand</label>
                                <input type="text" name="brand" id="edit_brand" placeholder="e.g Nike, Adidas"
                                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm">
                            </div>
                            
                            <div>
                                <label for="edit_weight" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Weight</label>
                                <div class="flex gap-2">
                                    <input type="number" name="weight" id="edit_weight" placeholder="0" min="0" step="0.1"
                                        class="flex-1 border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm">
                                    <select name="weight_unit" id="edit_weight_unit" class="border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm">
                                        <option value="gr">gr</option>
                                        <option value="kg" selected>kg</option>
                                    </select>
                                </div>
                            </div>

                            <div class="flex items-center space-x-6">
                                <label class="flex items-center">
                                    <input type="checkbox" name="is_featured" value="1" id="edit_is_featured"
                                        class="w-5 h-5 text-blue-600 border-gray-300 rounded focus:ring-blue-500">
                                    <span class="ml-2 text-sm text-gray-700 font-inter">Featured Product</span>
                                </label>
                                <label class="flex items-center">
                                    <input type="checkbox" name="is_active" value="1" id="edit_is_active"
                                        class="w-5 h-5 text-blue-600 border-gray-300 rounded focus:ring-blue-500">
                                    <span class="ml-2 text-sm text-gray-700 font-inter">Active</span>
                                </label>
                            </div>
                        </div>
                    </div>

                    <!-- Product Image Section -->
                    <div class="bg-gray-50 rounded-xl p-6">
                        <div class="flex items-center mb-6">
                            <div class="w-8 h-8 bg-green-600 rounded-lg flex items-center justify-center mr-3">
                                <i class="fas fa-image text-white text-sm"></i>
                            </div>
                            <h4 class="text-xl font-semibold text-gray-900 font-inter">Product Media</h4>
                        </div>
                        
                        <div>
                            <!-- Current Image Preview -->
                            <div id="edit_current_image_container" class="mb-4">
                                <label class="block text-sm font-medium text-gray-700 mb-2 font-inter">Current Image</label>
                                <div class="flex items-center space-x-4">
                                    <img id="edit_current_image" src="" alt="Current product image" class="w-32 h-32 rounded-lg object-cover border-2 border-gray-200">
                                    <div id="edit_no_current_image" class="hidden w-32 h-32 rounded-lg bg-gray-100 flex items-center justify-center border-2 border-gray-200">
                                        <i class="fas fa-image text-gray-400 text-2xl"></i>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- New Image Upload -->
                            <label for="edit_image" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Upload New Image</label>
                            <input type="file" name="image" id="edit_image" accept="image/png,image/jpeg,image/jpg,image/gif,image/webp"
                                onchange="previewEditImage(this)"
                                class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100">
                            <p class="text-xs text-gray-500 mt-2 font-inter">PNG, JPG, GIF atau WEBP (Max 2MB). Kosongkan jika tidak ingin mengubah gambar.</p>
                            
                            <!-- New Image Preview -->
                            <div id="edit_image_preview_container" class="hidden mt-4">
                                <label class="block text-sm font-medium text-gray-700 mb-2 font-inter">Preview New Image</label>
                                <div class="flex items-center space-x-4">
                                    <img id="edit_image_preview" src="" alt="Image preview" class="w-32 h-32 rounded-lg object-cover border-2 border-blue-500">
                                    <button type="button" onclick="clearEditImagePreview()" class="text-red-600 hover:text-red-800 text-sm font-medium">
                                        <i class="fas fa-times mr-1"></i>Remove
                                    </button>
                                </div>
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
                            <label for="edit_description" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Description <span class="text-red-500">*</span></label>
                            <textarea name="description" id="edit_description" rows="5" placeholder="Enter detailed product specifications, materials, and features" required
                                class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm resize-none"></textarea>
                            <span class="edit-description-error text-red-500 text-sm hidden"></span>
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
                                <label for="edit_stock_quantity" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Current Stock Quantity <span class="text-red-500">*</span></label>
                                <input type="number" name="stock_quantity" id="edit_stock_quantity" placeholder="0" min="0" required
                                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm">
                                <p class="text-xs text-gray-500 mt-1 font-inter">Total units available in warehouse</p>
                                <span class="edit-stock-error text-red-500 text-sm hidden"></span>
                            </div>
                            
                            <div>
                                <label for="edit_low_stock_threshold" class="block text-sm font-medium text-gray-700 mb-2 font-inter">Low Stock Alert Threshold</label>
                                <input type="number" name="low_stock_threshold" id="edit_low_stock_threshold" placeholder="5" min="0" 
                                    class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-sm">
                                <p class="text-xs text-gray-500 mt-1 font-inter">Get notified when stock falls below this number</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Modal footer -->
                <div class="flex items-center justify-end space-x-4 mt-8 pt-6 border-t border-gray-200">
                    <button type="button" onclick="closeEditProductModal()" 
                        class="px-6 py-3 text-gray-700 bg-white border border-gray-300 rounded-lg font-semibold font-inter text-sm hover:bg-gray-50 transition-colors">
                        Cancel
                    </button>
                    <button type="submit" 
                        class="px-8 py-3 bg-blue-600 text-white rounded-lg font-semibold font-inter text-sm hover:bg-blue-700 transition-colors flex items-center">
                        <i class="fas fa-save mr-2"></i>
                        Save Product
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
