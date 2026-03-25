@extends('admin.layouts.app')

@section('title', 'Product Detail - MuscleCart Admin')
@section('header', 'Product Detail')

@section('content')
<div class="max-w-3xl">
    <div class="bg-white rounded-lg shadow">
        <div class="p-6 border-b">
            <div class="flex items-center justify-between">
                <h3 class="text-lg font-semibold text-gray-800">{{ $product->name }}</h3>
                <div class="flex gap-2">
                    <a href="{{ route('admin.products.edit', $product) }}" class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg transition text-sm">
                        <i class="fas fa-edit mr-1"></i> Edit
                    </a>
                    <a href="{{ route('admin.products.index') }}" class="text-gray-600 hover:text-gray-800 px-4 py-2">
                        <i class="fas fa-arrow-left mr-1"></i> Back
                    </a>
                </div>
            </div>
        </div>
        <div class="p-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
                <!-- Image -->
                <div>
                    <div class="w-full h-64 bg-gray-100 rounded-lg flex items-center justify-center overflow-hidden">
                        @if($product->full_image_url)
                            <img src="{{ $product->full_image_url }}" alt="{{ $product->name }}" class="w-full h-full object-cover">
                        @else
                            <i class="fas fa-box text-gray-300 text-6xl"></i>
                        @endif
                    </div>
                </div>

                <!-- Details -->
                <div class="space-y-4">
                    <div>
                        <p class="text-sm text-gray-500">Category</p>
                        <p class="font-medium">{{ $product->category->name ?? '-' }}</p>
                    </div>
                    <div>
                        <p class="text-sm text-gray-500">Price</p>
                        <p class="text-2xl font-bold text-orange-600">Rp {{ number_format($product->price, 0, ',', '.') }}</p>
                    </div>
                    <div>
                        <p class="text-sm text-gray-500">Stock</p>
                        <p class="font-medium {{ $product->stock_quantity <= 10 ? 'text-red-600' : 'text-green-600' }}">
                            {{ $product->stock_quantity }} units
                            @if($product->stock_quantity <= 10)
                                <span class="text-xs bg-red-100 text-red-800 px-2 py-1 rounded ml-2">Low Stock</span>
                            @endif
                        </p>
                    </div>
                    @if($product->brand)
                    <div>
                        <p class="text-sm text-gray-500">Brand</p>
                        <p class="font-medium">{{ $product->brand }}</p>
                    </div>
                    @endif
                    @if($product->weight)
                    <div>
                        <p class="text-sm text-gray-500">Weight</p>
                        <p class="font-medium">{{ $product->weight }} kg</p>
                    </div>
                    @endif
                    <div>
                        <p class="text-sm text-gray-500">Status</p>
                        <div class="flex gap-2 mt-1">
                            <span class="inline-block px-2 py-1 text-xs rounded {{ $product->is_active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800' }}">
                                {{ $product->is_active ? 'Active' : 'Inactive' }}
                            </span>
                            @if($product->is_featured)
                                <span class="inline-block px-2 py-1 text-xs rounded bg-yellow-100 text-yellow-800">Featured</span>
                            @endif
                        </div>
                    </div>
                </div>
            </div>

            <!-- Description -->
            <div class="mt-8">
                <h4 class="text-sm font-medium text-gray-500 mb-2">Description</h4>
                <p class="text-gray-700 leading-relaxed">{{ $product->description }}</p>
            </div>

            <!-- Timestamps -->
            <div class="mt-8 pt-4 border-t text-sm text-gray-500">
                <p>Created: {{ $product->created_at ? $product->created_at->format('d M Y H:i') : '-' }}</p>
                <p>Updated: {{ $product->updated_at ? $product->updated_at->format('d M Y H:i') : '-' }}</p>
            </div>
        </div>
    </div>
</div>
@endsection
