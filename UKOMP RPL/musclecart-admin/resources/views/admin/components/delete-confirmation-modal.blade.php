{{-- Delete Confirmation Modal Component --}}
<div id="deleteConfirmationModal" class="hidden fixed inset-0 z-50 overflow-y-auto bg-gray-900 bg-opacity-50 flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-xl w-full max-w-md transform transition-all duration-300 scale-95 opacity-0" id="deleteModalContent">
        <!-- Modal Header -->
        <div class="flex items-center justify-center p-6 border-b border-gray-100">
            <div class="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mb-4">
                <i class="fas fa-exclamation-triangle text-red-600 text-2xl"></i>
            </div>
        </div>

        <!-- Modal Body -->
        <div class="p-6 text-center">
            <h3 class="text-xl font-bold text-gray-900 font-inter mb-2" id="deleteModalTitle">Delete Item?</h3>
            <p class="text-gray-600 font-inter mb-6" id="deleteModalMessage">
                You are about to permanently delete this item from your inventory. 
                This action will also remove them from your online storefront and cannot be undone.
            </p>
            
            <!-- Item Info (will be populated dynamically) -->
            <div id="deleteItemInfo" class="bg-gray-50 rounded-lg p-4 mb-6 hidden">
                <div class="flex items-center justify-center">
                    <div id="deleteItemIcon" class="w-10 h-10 bg-gray-200 rounded-lg flex items-center justify-center mr-3">
                        <i class="fas fa-cube text-gray-600"></i>
                    </div>
                    <div>
                        <div class="font-medium text-gray-900 font-inter" id="deleteItemName"></div>
                        <div class="text-sm text-gray-500 font-inter" id="deleteItemDetails"></div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Footer -->
        <div class="flex items-center justify-center space-x-4 p-6 border-t border-gray-100">
            <button type="button" 
                    onclick="closeDeleteModal()" 
                    class="px-6 py-3 text-gray-700 bg-white border border-gray-300 rounded-lg font-semibold font-inter text-sm hover:bg-gray-50 transition-colors">
                Cancel
            </button>
            <form id="deleteForm" method="POST" class="inline">
                @csrf
                @method('DELETE')
                <button type="submit" 
                        id="deleteButton"
                        class="px-6 py-3 bg-red-600 text-white rounded-lg font-semibold font-inter text-sm hover:bg-red-700 transition-colors">
                    Delete Forever
                </button>
            </form>
        </div>
    </div>
</div>

<script>
// Global delete modal functions
window.openDeleteModal = function(config) {
    const modal = document.getElementById('deleteConfirmationModal');
    const modalContent = document.getElementById('deleteModalContent');
    const form = document.getElementById('deleteForm');
    
    // Set modal content
    document.getElementById('deleteModalTitle').textContent = config.title || 'Delete Item?';
    document.getElementById('deleteModalMessage').textContent = config.message || 'This action cannot be undone.';
    
    // Set form action
    form.action = config.deleteUrl;
    
    // Set item info if provided
    const itemInfo = document.getElementById('deleteItemInfo');
    if (config.itemName) {
        itemInfo.classList.remove('hidden');
        document.getElementById('deleteItemName').textContent = config.itemName;
        document.getElementById('deleteItemDetails').textContent = config.itemDetails || '';
        
        // Set icon if provided
        const iconElement = document.getElementById('deleteItemIcon');
        if (config.iconClass) {
            iconElement.innerHTML = `<i class="${config.iconClass}"></i>`;
            iconElement.className = `w-10 h-10 ${config.iconBgClass || 'bg-gray-200'} rounded-lg flex items-center justify-center mr-3`;
        }
    } else {
        itemInfo.classList.add('hidden');
    }
    
    // Show modal
    modal.classList.remove('hidden');
    document.body.style.overflow = 'hidden';
    
    // Animate in
    setTimeout(() => {
        modalContent.classList.remove('scale-95', 'opacity-0');
        modalContent.classList.add('scale-100', 'opacity-100');
    }, 10);
};

window.closeDeleteModal = function() {
    const modal = document.getElementById('deleteConfirmationModal');
    const modalContent = document.getElementById('deleteModalContent');
    
    // Animate out
    modalContent.classList.add('scale-95', 'opacity-0');
    modalContent.classList.remove('scale-100', 'opacity-100');
    
    setTimeout(() => {
        modal.classList.add('hidden');
        document.body.style.overflow = 'auto';
    }, 200);
};

// Handle delete form submission
document.getElementById('deleteForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const deleteButton = document.getElementById('deleteButton');
    const originalText = deleteButton.textContent;
    
    // Show loading state
    deleteButton.disabled = true;
    deleteButton.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i>Deleting...';
    
    // Submit form via fetch
    fetch(this.action, {
        method: 'POST',
        body: new FormData(this),
        headers: {
            'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content'),
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        closeDeleteModal();
        
        // Show success notification
        showNotification('Item deleted successfully!', 'success');
        
        // Reload page after short delay
        setTimeout(() => {
            window.location.reload();
        }, 1000);
    })
    .catch(error => {
        console.error('Error:', error);
        showNotification('Failed to delete item. Please try again.', 'error');
    })
    .finally(() => {
        // Reset button state
        deleteButton.disabled = false;
        deleteButton.textContent = originalText;
    });
});

// Close modal when clicking outside
document.getElementById('deleteConfirmationModal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeDeleteModal();
    }
});

// Close modal with Escape key
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape' && !document.getElementById('deleteConfirmationModal').classList.contains('hidden')) {
        closeDeleteModal();
    }
});

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

<style>
#deleteModalContent {
    transition: all 0.2s ease-out;
}
</style>