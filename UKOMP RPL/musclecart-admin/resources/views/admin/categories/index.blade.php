@extends('admin.layouts.app')

@section('title', 'Categories - MuscleCart Admin')

@section('content')
@include('admin.components.delete-confirmation-modal')

<style>
/* Action dropdown with fixed positioning */
[id^="dropdown-category-"] {
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
    border: 1px solid #e5e7eb;
    background: white;
}

/* Ensure dropdown is always on top */
[id^="dropdown-category-"]:not(.hidden) {
    z-index: 9999 !important;
}

/* Smooth transition for dropdown */
[id^="dropdown-category-"] {
    transition: opacity 0.15s ease-in-out;
}

/* Remove any overflow restrictions that might clip the dropdown */
tbody,
tbody tr,
tbody tr td {
    overflow: visible !important;
}
</style>

<!-- Page Header -->
<div class="mb-8">
    <h1 class="text-3xl font-bold text-gray-900 font-inter">Categories</h1>
    <p class="text-gray-600 font-inter mt-1">Manage your store's product organization and hierarchies.</p>
</div>

<!-- Summary Cards -->
<div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
    <!-- Total Categories -->
    <div class="bg-white rounded-xl shadow-sm border p-6">
        <div class="flex items-center">
            <div class="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center">
                <i class="fas fa-th-large text-blue-600 text-xl"></i>
            </div>
            <div class="ml-4">
                <p class="text-2xl font-bold text-gray-900 font-inter">{{ $totalCategories }}</p>
                <p class="text-sm font-medium text-gray-600 font-inter">TOTAL CATEGORIES</p>
            </div>
        </div>
    </div>
    
    <!-- Active Categories -->
    <div class="bg-white rounded-xl shadow-sm border p-6">
        <div class="flex items-center">
            <div class="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center">
                <i class="fas fa-check-circle text-green-600 text-xl"></i>
            </div>
            <div class="ml-4">
                <p class="text-2xl font-bold text-gray-900 font-inter">{{ $activeCategories }}</p>
                <p class="text-sm font-medium text-gray-600 font-inter">ACTIVE CATEGORIES</p>
            </div>
        </div>
    </div>
    
    <!-- Top Product Count -->
    <div class="bg-white rounded-xl shadow-sm border p-6">
        <div class="flex items-center">
            <div class="w-12 h-12 bg-orange-100 rounded-xl flex items-center justify-center">
                <i class="fas fa-trophy text-orange-600 text-xl"></i>
            </div>
            <div class="ml-4">
                <p class="text-2xl font-bold text-gray-900 font-inter">{{ $maxProductCount }}</p>
                <p class="text-sm font-medium text-gray-600 font-inter">TOP PRODUCT COUNT</p>
            </div>
        </div>
    </div>
</div>

<!-- Search and Filter Row -->
<div class="flex justify-between items-center mb-6">
    <div class="flex items-center space-x-4">
        <!-- Search Bar -->
        <div class="relative">
            <input type="text" placeholder="Search categories by name, description..." class="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white font-inter w-80">
            <i class="fas fa-search absolute left-3 top-3 text-gray-400 text-sm"></i>
        </div>
        
        <!-- Status Filter -->
        <div class="relative">
            <select class="appearance-none bg-white border border-gray-300 rounded-lg px-4 py-2 pr-8 focus:outline-none focus:ring-2 focus:ring-blue-500 font-inter">
                <option>All Statuses</option>
                <option>Active</option>
                <option>Inactive</option>
            </select>
            <i class="fas fa-chevron-down absolute right-3 top-3 text-gray-400 text-xs"></i>
        </div>
        
        <!-- Filters Button -->
        <button class="flex items-center px-4 py-2 border border-gray-300 rounded-lg bg-white hover:bg-gray-50 font-inter">
            <i class="fas fa-filter mr-2 text-gray-500"></i>
            Filters
        </button>
    </div>
    
    <!-- Add New Category Button -->
    <button onclick="openCreateModal()" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-inter font-medium flex items-center">
        <i class="fas fa-plus mr-2"></i>
        Add New Category
    </button>
</div>

<!-- Categories Table -->
<div class="bg-white rounded-xl shadow-sm border overflow-hidden">
    <div class="overflow-x-auto">
        <table class="w-full">
            <thead class="bg-gray-50 border-b border-gray-200">
                <tr>
                    <th class="text-left py-4 px-6 text-sm font-semibold text-gray-700 uppercase tracking-wider font-inter">Category Name</th>
                    <th class="text-left py-4 px-6 text-sm font-semibold text-gray-700 uppercase tracking-wider font-inter">Description</th>
                    <th class="text-left py-4 px-6 text-sm font-semibold text-gray-700 uppercase tracking-wider font-inter">Total Products</th>
                    <th class="text-left py-4 px-6 text-sm font-semibold text-gray-700 uppercase tracking-wider font-inter">Status</th>
                    <th class="text-left py-4 px-6 text-sm font-semibold text-gray-700 uppercase tracking-wider font-inter">Created Date</th>
                    <th class="text-left py-4 px-6 text-sm font-semibold text-gray-700 uppercase tracking-wider font-inter">Actions</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-gray-100">
                @forelse($categories as $category)
                <tr class="hover:bg-gray-50 transition-colors">
                    <td class="py-4 px-6">
                        <span class="font-semibold text-gray-900 font-inter">{{ $category->name }}</span>
                    </td>
                    <td class="py-4 px-6">
                        <span class="text-gray-600 font-inter">{{ Str::limit($category->description ?? 'No description available', 50) }}</span>
                    </td>
                    <td class="py-4 px-6">
                        <span class="font-semibold text-gray-900 font-inter">{{ $category->products_count ?? 0 }} Products</span>
                    </td>
                    <td class="py-4 px-6">
                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium {{ $category->is_active ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800' }} font-inter">
                            {{ $category->is_active ? 'Active' : 'Inactive' }}
                        </span>
                    </td>
                    <td class="py-4 px-6">
                        <span class="text-gray-600 font-inter">{{ $category->created_at->format('M d, Y') }}</span>
                    </td>
                    <td class="py-4 px-6">
                        <div class="relative">
                            <button id="btn-category-{{ $category->id }}" onclick="event.stopPropagation(); toggleActionDropdown('dropdown-category-{{ $category->id }}', this)" class="text-gray-400 hover:text-gray-600 p-2 rounded-lg hover:bg-gray-100 transition-colors">
                                <i class="fas fa-ellipsis-v"></i>
                            </button>
                            <div id="dropdown-category-{{ $category->id }}" class="hidden fixed bg-white rounded-lg shadow-2xl border border-gray-200" style="min-width: 200px; z-index: 9999;">
                                <div class="py-1">
                                    <button 
                                        onclick="openEditModal(this)" 
                                        data-category-id="{{ $category->id }}"
                                        data-category-name="{{ $category->name }}"
                                        data-category-description="{{ $category->description }}"
                                        data-category-active="{{ $category->is_active ? '1' : '0' }}"
                                        class="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-600 font-inter">
                                        <i class="fas fa-edit mr-3 text-blue-500"></i>
                                        Edit Category
                                    </button>
                                    <button onclick="confirmDeleteCategory('/admin/categories/{{ $category->id }}', '{{ $category->name }}', '{{ $category->products_count ?? 0 }} Products')" class="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-red-50 hover:text-red-600 font-inter">
                                        <i class="fas fa-trash mr-3 text-red-500"></i>
                                        Delete Category
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
                                <i class="fas fa-tag text-gray-400 text-xl"></i>
                            </div>
                            <h3 class="text-lg font-semibold text-gray-900 font-inter mb-2">No categories found</h3>
                            <p class="text-gray-600 font-inter mb-4">Get started by creating your first product category.</p>
                            <button onclick="openCreateModal()" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-inter font-medium">
                                <i class="fas fa-plus mr-2"></i>Add Category
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
        <div class="flex items-center justify-between">
            <div class="flex items-center text-sm text-gray-600 font-inter">
                @if($categories->hasPages())
                    Showing {{ $categories->firstItem() }} to {{ $categories->lastItem() }} of {{ $categories->total() }} results
                @else
                    Showing {{ $categories->count() }} of {{ $categories->total() }} results
                @endif
            </div>
            @if($categories->hasPages())
            <div>
                {{ $categories->links('vendor.pagination.tailwind') }}
            </div>
            @endif
        </div>
    </div>
</div>

<!-- Create Category Modal -->
<div id="createCategoryModal" class="fixed inset-0 bg-gray-900 bg-opacity-50 hidden z-50 flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-md max-h-[90vh] overflow-y-auto animate-fade-in">
        <!-- Modal Header -->
        <div class="flex items-start justify-between p-6 border-b border-gray-100">
            <div>
                <h2 class="text-xl font-bold text-gray-900 font-inter">Create New Category</h2>
                <p class="text-sm text-gray-500 font-inter mt-1">Add a new segment to your product hierarchy</p>
            </div>
            <button onclick="closeCreateModal()" class="text-gray-400 hover:text-gray-600 p-1 rounded-lg hover:bg-gray-100 transition-colors ml-4">
                <i class="fas fa-times text-lg"></i>
            </button>
        </div>

        <!-- Modal Body -->
        <form id="createCategoryForm" action="{{ route('admin.categories.store') }}" method="POST" enctype="multipart/form-data" class="p-6 space-y-6">
            @csrf
            
            <!-- Category Name -->
            <div>
                <label for="category_name" class="block text-sm font-semibold text-gray-700 mb-2 font-inter uppercase tracking-wider">Category Name</label>
                <input type="text" 
                       name="name" 
                       id="category_name" 
                       placeholder="e.g. Strength Equipment"
                       class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-gray-700 placeholder-gray-400"
                       required>
                <div class="text-red-500 text-sm mt-1 hidden" id="name_error"></div>
            </div>

            <!-- Description -->
            <div>
                <label for="category_description" class="block text-sm font-semibold text-gray-700 mb-2 font-inter uppercase tracking-wider">Description</label>
                <textarea name="description" 
                         id="category_description" 
                         rows="4"
                         placeholder="Briefly describe what products belong in this category..."
                         class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none font-inter text-gray-700 placeholder-gray-400"></textarea>
                <div class="text-red-500 text-sm mt-1 hidden" id="description_error"></div>
            </div>

            <!-- Active Status Toggle -->
            <div class="flex items-center justify-between p-4 bg-blue-50 rounded-xl border border-blue-100">
                <div class="flex items-center">
                    <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center mr-3">
                        <i class="fas fa-eye text-blue-600"></i>
                    </div>
                    <div>
                        <p class="font-semibold text-gray-900 font-inter text-sm">Active Status</p>
                        <p class="text-xs text-gray-500 font-inter">Category will be visible on storefront</p>
                    </div>
                </div>
                <label class="relative inline-flex cursor-pointer">
                    <input type="hidden" name="is_active" value="0">
                    <input type="checkbox" 
                           name="is_active" 
                           value="1" 
                           checked
                           class="sr-only peer" 
                           id="active_status">
                    <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 dark:peer-focus:ring-blue-800 rounded-full peer dark:bg-gray-700 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all dark:border-gray-600 peer-checked:bg-blue-600"></div>
                </label>
            </div>
        </form>

        <!-- Modal Footer -->
        <div class="flex items-center justify-end space-x-3 p-6 border-t border-gray-100">
            <button type="button" 
                    onclick="closeCreateModal()" 
                    class="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 font-medium font-inter transition-colors">
                Cancel
            </button>
            <button type="submit" 
                    form="createCategoryForm"
                    class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-medium font-inter transition-colors">
                Save Category
            </button>
        </div>
    </div>
</div>

<!-- Edit Category Modal -->
<div id="editCategoryModal" class="fixed inset-0 bg-gray-900 bg-opacity-50 hidden z-50 flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-md max-h-[90vh] overflow-y-auto animate-fade-in">
        <!-- Modal Header -->
        <div class="flex items-start justify-between p-6 border-b border-gray-100">
            <div>
                <h2 class="text-xl font-bold text-gray-900 font-inter">Edit Category</h2>
                <p class="text-sm text-gray-500 font-inter mt-1">Update category information</p>
            </div>
            <button onclick="closeEditModal()" class="text-gray-400 hover:text-gray-600 p-1 rounded-lg hover:bg-gray-100 transition-colors ml-4">
                <i class="fas fa-times text-lg"></i>
            </button>
        </div>

        <!-- Modal Body -->
        <form id="editCategoryForm" method="POST" enctype="multipart/form-data" class="p-6 space-y-6">
            @csrf
            @method('PUT')
            
            <!-- Category Name -->
            <div>
                <label for="edit_category_name" class="block text-sm font-semibold text-gray-700 mb-2 font-inter uppercase tracking-wider">Category Name</label>
                <input type="text" 
                       name="name" 
                       id="edit_category_name" 
                       placeholder="e.g. Strength Equipment"
                       class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-gray-700 placeholder-gray-400"
                       required>
                <div class="text-red-500 text-sm mt-1 hidden" id="edit_name_error"></div>
            </div>

            <!-- Description -->
            <div>
                <label for="edit_category_description" class="block text-sm font-semibold text-gray-700 mb-2 font-inter uppercase tracking-wider">Description</label>
                <textarea name="description" 
                         id="edit_category_description" 
                         rows="4"
                         placeholder="Briefly describe what products belong in this category..."
                         class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none font-inter text-gray-700 placeholder-gray-400"></textarea>
                <div class="text-red-500 text-sm mt-1 hidden" id="edit_description_error"></div>
            </div>

            <!-- Active Status Toggle -->
            <div class="flex items-center justify-between p-4 bg-blue-50 rounded-xl border border-blue-100">
                <div class="flex items-center">
                    <div class="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center mr-3">
                        <i class="fas fa-eye text-blue-600"></i>
                    </div>
                    <div>
                        <p class="text-sm font-semibold text-gray-900 font-inter">Active Status</p>
                        <p class="text-xs text-gray-500 font-inter">Category will be visible on storefront</p>
                    </div>
                </div>
                <label class="toggle-switch">
                    <input type="checkbox" name="is_active" id="edit_active_status" value="1" checked>
                    <span class="slider"></span>
                </label>
            </div>

            <!-- Modal Footer -->
            <div class="flex items-center justify-end space-x-3 pt-4 border-t border-gray-100">
                <button 
                    type="button"
                    onclick="closeEditModal()"
                    class="px-6 py-2 bg-white border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 font-medium font-inter transition-colors">
                    Cancel
                </button>
                <button 
                    type="submit"
                    form="editCategoryForm"
                    class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-medium font-inter transition-colors">
                    Update Category
                </button>
            </div>
        </form>
    </div>
</div>

<style>
@keyframes fade-in {
    from {
        opacity: 0;
        transform: scale(0.95) translateY(-10px);
    }
    to {
        opacity: 1;
        transform: scale(1) translateY(0);
    }
}

.animate-fade-in {
    animation: fade-in 0.2s ease-out;
}

/* Custom toggle switch styling */
.toggle-switch input {
    display: none;
}

.toggle-switch input + .slider {
    position: relative;
    display: inline-block;
    width: 44px;
    height: 24px;
    background: #e2e8f0;
    border-radius: 12px;
    cursor: pointer;
    transition: background 0.3s;
}

.toggle-switch input + .slider:before {
    content: '';
    position: absolute;
    top: 2px;
    left: 2px;
    width: 20px;
    height: 20px;
    background: white;
    border-radius: 50%;
    transition: transform 0.3s;
    box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.toggle-switch input:checked + .slider {
    background: #3b82f6;
}

.toggle-switch input:checked + .slider:before {
    transform: translateX(20px);
}
</style>

<script>
// Modal functions
function openCreateModal() {
    document.getElementById('createCategoryModal').classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

function closeCreateModal() {
    document.getElementById('createCategoryModal').classList.add('hidden');
    document.body.style.overflow = 'auto';
    
    // Reset form
    document.getElementById('createCategoryForm').reset();
    document.getElementById('active_status').checked = true;
    
    // Clear errors
    document.querySelectorAll('[id$="_error"]').forEach(error => {
        error.classList.add('hidden');
        error.textContent = '';
    });
}

// Edit modal functions
function openEditModal(button) {
    // Get data from button attributes
    const categoryId = button.getAttribute('data-category-id');
    const categoryName = button.getAttribute('data-category-name');
    const categoryDescription = button.getAttribute('data-category-description');
    const isActive = button.getAttribute('data-category-active') === '1';
    
    // Close any open dropdowns
    document.querySelectorAll('[id^="dropdown-category-"]').forEach(dropdown => {
        dropdown.classList.add('hidden');
    });
    
    // Set form action URL
    const form = document.getElementById('editCategoryForm');
    form.action = '/admin/categories/' + categoryId;
    
    // Populate form fields
    document.getElementById('edit_category_name').value = categoryName;
    document.getElementById('edit_category_description').value = categoryDescription || '';
    document.getElementById('edit_active_status').checked = isActive;
    
    // Show modal
    document.getElementById('editCategoryModal').classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}

function closeEditModal() {
    document.getElementById('editCategoryModal').classList.add('hidden');
    document.body.style.overflow = 'auto';
    
    // Reset form
    document.getElementById('editCategoryForm').reset();
    document.getElementById('edit_active_status').checked = true;
    
    // Clear errors
    document.querySelectorAll('[id^="edit_"][id$="_error"]').forEach(error => {
        error.classList.add('hidden');
        error.textContent = '';
    });
}

// Close modal when clicking outside
document.getElementById('createCategoryModal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeCreateModal();
    }
});

// Close edit modal when clicking outside
document.getElementById('editCategoryModal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeEditModal();
    }
});

// Form submission with validation
document.getElementById('createCategoryForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const formData = new FormData(this);
    const submitButton = document.querySelector('button[form="createCategoryForm"]');
    const originalText = submitButton.textContent;
    
    // Show loading state
    submitButton.disabled = true;
    submitButton.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i>Saving...';
    
    // Clear previous errors
    document.querySelectorAll('[id$="_error"]').forEach(error => {
        error.classList.add('hidden');
        error.textContent = '';
    });

    fetch(this.action, {
        method: 'POST',
        body: formData,
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content'),
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => Promise.reject(data));
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            closeCreateModal();
            showNotification('Category created successfully!', 'success');
            // Refresh page after short delay
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        }
    })
    .catch(data => {
        if (data.errors) {
            // Show validation errors
            Object.keys(data.errors).forEach(field => {
                const errorElement = document.getElementById(field + '_error');
                if (errorElement) {
                    errorElement.textContent = data.errors[field][0];
                    errorElement.classList.remove('hidden');
                }
            });
        } else {
            showNotification(data.message || 'An error occurred. Please try again.', 'error');
        }
    })
    .finally(() => {
        // Reset button state
        submitButton.disabled = false;
        submitButton.textContent = originalText;
    });
});

// Edit form submission with validation
document.getElementById('editCategoryForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const formData = new FormData(this);
    const submitButton = document.querySelector('button[form="editCategoryForm"]');
    const originalText = submitButton.textContent;
    
    // Show loading state
    submitButton.disabled = true;
    submitButton.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i>Updating...';
    
    // Clear previous errors
    document.querySelectorAll('[id^="edit_"][id$="_error"]').forEach(error => {
        error.classList.add('hidden');
        error.textContent = '';
    });

    fetch(this.action, {
        method: 'POST',
        body: formData,
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content'),
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => Promise.reject(data));
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            closeEditModal();
            showNotification('Category updated successfully!', 'success');
            // Refresh page after short delay
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        }
    })
    .catch(data => {
        if (data.errors) {
            // Show validation errors
            Object.keys(data.errors).forEach(field => {
                const errorElement = document.getElementById('edit_' + field + '_error');
                if (errorElement) {
                    errorElement.textContent = data.errors[field][0];
                    errorElement.classList.remove('hidden');
                }
            });
        } else {
            showNotification(data.message || 'An error occurred. Please try again.', 'error');
        }
    })
    .finally(() => {
        // Reset button state
        submitButton.disabled = false;
        submitButton.textContent = originalText;
    });
});

// Notification function
function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = `fixed top-4 right-4 px-6 py-3 rounded-lg text-white font-medium font-inter z-50 ${
        type === 'success' ? 'bg-green-500' : 'bg-red-500'
    }`;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Dropdown functions for actions
function toggleActionDropdown(dropdownId, buttonElement) {
    event.stopPropagation(); // Prevent event from bubbling
    
    // Close all other dropdowns
    document.querySelectorAll('[id^="dropdown-category-"]').forEach(dropdown => {
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
    document.querySelectorAll('[id^="dropdown-category-"]').forEach(dropdown => {
        dropdown.classList.add('hidden');
    });
});

// Close dropdowns on scroll
window.addEventListener('scroll', function() {
    document.querySelectorAll('[id^="dropdown-category-"]').forEach(dropdown => {
        dropdown.classList.add('hidden');
    });
}, true);

// Close dropdowns on window resize
window.addEventListener('resize', function() {
    document.querySelectorAll('[id^="dropdown-category-"]').forEach(dropdown => {
        dropdown.classList.add('hidden');
    });
});

// Confirm delete function
function confirmDeleteCategory(deleteUrl, itemName, itemDetails) {
    // Close any open dropdowns
    document.querySelectorAll('[id^="dropdown-category-"]').forEach(dropdown => {
        dropdown.classList.add('hidden');
    });
    
    // Create config object
    const config = {
        title: 'Delete Category?',
        message: 'You are about to permanently delete this category. This will also delete all products in this category.',
        deleteUrl: deleteUrl,
        itemName: itemName,
        itemDetails: itemDetails,
        iconClass: 'fas fa-tag text-gray-600',
        iconBgClass: 'bg-gray-200'
    };
    
    // Open delete confirmation modal
    openDeleteModal(config);
}
</script>
@endsection
