<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="csrf-token" content="{{ csrf_token() }}">
    <title>@yield('title', 'MuscleCart Admin')</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        * {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
        }
        .sidebar-bg {
            background: linear-gradient(180deg, #1e3a8a 0%, #1e40af 100%);
        }
        .metric-card {
            transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
        }
        .metric-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
        }
        .font-inter {
            font-family: 'Inter', sans-serif;
        }
        
        /* Modal styles */
        .modal-backdrop {
            backdrop-filter: blur(8px);
            background-color: rgba(31, 41, 55, 0.4);
        }
        
        .modal-content {
            transform: scale(0.95);
            transition: transform 0.2s ease-out;
        }
        
        .modal-content.show {
            transform: scale(1);
        }
        
        /* Form focus states */
        .form-input:focus {
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
        }
        
        /* Loading animation */
        @keyframes spin {
            to { transform: rotate(360deg); }
        }
        
        .animate-spin {
            animation: spin 1s linear infinite;
        }
    </style>
</head>
<body class="bg-gray-50">
    <div class="min-h-screen">
        <!-- Header Navigation -->
        <header class="bg-white shadow-sm border-b border-gray-200">
            <!-- Top Bar -->
            <div class="px-6 py-4 border-b border-gray-100">
                <div class="flex justify-between items-center">
                    <!-- Logo -->
                    <a href="{{ route('admin.dashboard') }}" class="flex items-center hover:opacity-80 transition-opacity cursor-pointer">
                        <div class="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center mr-3">
                            <i class="fas fa-dumbbell text-white text-sm"></i>
                        </div>
                        <h1 class="text-xl font-bold text-gray-800 font-inter">MuscleCart</h1>
                    </a>
                    
                    <div class="flex items-center space-x-4">
                        <!-- Global Search -->
                        <div class="relative">
                            <input type="text" placeholder="Global search..." class="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-gray-50 font-inter w-80">
                            <i class="fas fa-search absolute left-3 top-3 text-gray-400 text-sm"></i>
                        </div>
                        <!-- Notifications -->
                        <button class="relative p-2 text-gray-400 hover:text-gray-600 transition-colors">
                            <i class="fas fa-bell text-lg"></i>
                            <span class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center font-inter font-medium">3</span>
                        </button>
                        <!-- Settings -->
                        <button class="p-2 text-gray-400 hover:text-gray-600 transition-colors">
                            <i class="fas fa-cog text-lg"></i>
                        </button>
                        <!-- User Profile -->
                        <div class="flex items-center cursor-pointer hover:bg-gray-50 rounded-lg p-2 transition-colors" onclick="toggleUserDropdown()">
                            <div class="relative">
                                <img src="https://ui-avatars.com/api/?name={{ Auth::user()->name }}&background=3b82f6&color=fff" alt="User" class="w-10 h-10 rounded-full border-2 border-gray-200">
                                <div class="absolute -bottom-1 -right-1 w-4 h-4 bg-green-500 border-2 border-white rounded-full"></div>
                            </div>
                        </div>
                        
                        <!-- Logout Button -->
                        <form action="{{ route('logout') }}" method="POST" class="inline">
                            @csrf
                            <button type="submit" class="p-2 text-gray-400 hover:text-red-600 transition-colors" title="Logout">
                                <i class="fas fa-sign-out-alt text-lg"></i>
                            </button>
                        </form>
                    </div>
                </div>
            </div>
            
            <!-- Navigation Tabs -->
            <div class="px-6">
                <nav class="flex space-x-8">
                    <a href="{{ route('admin.dashboard') }}" class="py-4 px-1 border-b-2 font-medium text-sm font-inter transition-colors {{ request()->routeIs('admin.dashboard') ? 'border-blue-500 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300' }}">
                        Dashboard
                    </a>
                    <a href="{{ route('admin.products.index') }}" class="py-4 px-1 border-b-2 font-medium text-sm font-inter transition-colors {{ request()->routeIs('admin.products.*') ? 'border-blue-500 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300' }}">
                        Products
                    </a>
                    <a href="{{ route('admin.categories.index') }}" class="py-4 px-1 border-b-2 font-medium text-sm font-inter transition-colors {{ request()->routeIs('admin.categories.*') ? 'border-blue-500 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300' }}">
                        Categories
                    </a>
                    <a href="{{ route('admin.orders.index') }}" class="py-4 px-1 border-b-2 font-medium text-sm font-inter transition-colors {{ request()->routeIs('admin.orders.*') ? 'border-blue-500 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300' }}">
                        Orders
                    </a>
                    <a href="{{ route('admin.customers.index') }}" class="py-4 px-1 border-b-2 font-medium text-sm font-inter transition-colors {{ request()->routeIs('admin.customers.*') ? 'border-blue-500 text-blue-600' : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300' }}">
                        Customers
                    </a>
                </nav>
            </div>
        </header>

        <!-- Main Content -->
        <div class="flex-1">
            <!-- Page Content -->
            <main class="p-8 bg-gray-50">
                @if(session('success'))
                    <div class="bg-green-50 border border-green-200 text-green-800 px-4 py-3 rounded-lg mb-6 shadow-sm">
                        <div class="flex items-center">
                            <i class="fas fa-check-circle text-green-500 mr-3"></i>
                            <span class="font-inter">{{ session('success') }}</span>
                        </div>
                    </div>
                @endif

                @if(session('error'))
                    <div class="bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded-lg mb-6 shadow-sm">
                        <div class="flex items-center">
                            <i class="fas fa-exclamation-circle text-red-500 mr-3"></i>
                            <span class="font-inter">{{ session('error') }}</span>
                        </div>
                    </div>
                @endif

                @yield('content')
            </main>
        </div>
        
        <!-- Logout Modal/Dropdown (Hidden by default) -->
        <div id="logoutDropdown" class="hidden fixed top-16 right-6 bg-white rounded-lg shadow-lg border border-gray-200 z-50">
            <div class="p-4">
                <div class="flex items-center mb-3">
                    <img src="https://ui-avatars.com/api/?name={{ Auth::user()->name }}&background=3b82f6&color=fff" alt="User" class="w-10 h-10 rounded-full mr-3">
                    <div>
                        <p class="text-sm font-semibold text-gray-800 font-inter">{{ Auth::user()->name }}</p>
                        <p class="text-xs text-gray-500 font-inter">Head Admin</p>
                    </div>
                </div>
                <hr class="my-2">
                <form method="POST" action="{{ route('logout') }}">
                    @csrf
                    <button type="submit" class="flex items-center w-full px-3 py-2 text-red-600 hover:bg-red-50 transition-colors rounded-lg font-inter text-sm">
                        <i class="fas fa-sign-out-alt mr-2"></i>
                        Logout
                    </button>
                </form>
            </div>
        </div>
    </div>
    
    <script>
        // Toggle user dropdown
        function toggleUserDropdown() {
            const dropdown = document.getElementById('logoutDropdown');
            dropdown.classList.toggle('hidden');
        }
        
        document.addEventListener('DOMContentLoaded', function() {
            const dropdown = document.getElementById('logoutDropdown');
            
            // Close dropdown when clicking outside
            document.addEventListener('click', function(e) {
                if (!e.target.closest('.cursor-pointer') && !e.target.closest('#logoutDropdown')) {
                    dropdown.classList.add('hidden');
                }
            });
        });
    </script>
</body>
</html>
