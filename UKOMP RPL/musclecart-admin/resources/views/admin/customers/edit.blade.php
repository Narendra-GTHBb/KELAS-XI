@extends('admin.layouts.app')

@section('title', 'Edit Customer - MuscleCart Admin')

@section('content')
<div class="p-6 bg-gray-50 min-h-screen">
    <div class="max-w-3xl mx-auto">
        <!-- Header -->
        <div class="mb-6">
            <div class="flex justify-between items-center">
                <div>
                    <h1 class="text-3xl font-bold text-gray-900 font-inter">Edit Customer</h1>
                    <p class="text-gray-600 font-inter mt-1">Update customer information</p>
                </div>
                <a href="{{ route('admin.customers.index') }}" class="flex items-center px-4 py-2 bg-white text-gray-700 border border-gray-300 rounded-lg font-semibold font-inter text-sm hover:bg-gray-50 transition-colors">
                    <i class="fas fa-arrow-left mr-2"></i>
                    Back to Customers
                </a>
            </div>
        </div>

        <!-- Edit Form -->
        <div class="bg-white rounded-xl border border-gray-200 p-8">
            <form action="{{ route('admin.customers.update', $customer) }}" method="POST">
                @csrf
                @method('PUT')

                <div class="space-y-6">
                    <!-- Name -->
                    <div>
                        <label for="name" class="block text-sm font-semibold text-gray-700 mb-2 font-inter uppercase tracking-wider">
                            Full Name <span class="text-red-500">*</span>
                        </label>
                        <input type="text" 
                               name="name" 
                               id="name" 
                               value="{{ old('name', $customer->name) }}"
                               required
                               class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-gray-700 @error('name') border-red-500 @enderror">
                        @error('name')
                            <p class="text-red-500 text-sm mt-1 font-inter">{{ $message }}</p>
                        @enderror
                    </div>

                    <!-- Email -->
                    <div>
                        <label for="email" class="block text-sm font-semibold text-gray-700 mb-2 font-inter uppercase tracking-wider">
                            Email Address <span class="text-red-500">*</span>
                        </label>
                        <input type="email" 
                               name="email" 
                               id="email" 
                               value="{{ old('email', $customer->email) }}"
                               required
                               class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-gray-700 @error('email') border-red-500 @enderror">
                        @error('email')
                            <p class="text-red-500 text-sm mt-1 font-inter">{{ $message }}</p>
                        @enderror
                    </div>

                    <!-- Phone (Optional) -->
                    <div>
                        <label for="phone" class="block text-sm font-semibold text-gray-700 mb-2 font-inter uppercase tracking-wider">
                            Phone Number
                        </label>
                        <input type="tel" 
                               name="phone" 
                               id="phone" 
                               value="{{ old('phone', $customer->phone ?? '') }}"
                               class="w-full border border-gray-300 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent font-inter text-gray-700">
                    </div>
                </div>

                <!-- Form Actions -->
                <div class="flex items-center justify-end space-x-4 mt-8 pt-6 border-t border-gray-200">
                    <a href="{{ route('admin.customers.index') }}" class="px-6 py-3 border border-gray-300 text-gray-700 rounded-lg font-semibold font-inter text-sm hover:bg-gray-50 transition-colors">
                        Cancel
                    </a>
                    <button type="submit" class="px-8 py-3 bg-blue-600 text-white rounded-lg font-semibold font-inter text-sm hover:bg-blue-700 transition-colors flex items-center">
                        <i class="fas fa-save mr-2"></i>
                        Update Customer
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>
@endsection
