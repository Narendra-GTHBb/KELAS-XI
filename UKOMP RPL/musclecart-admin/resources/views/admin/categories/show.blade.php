@extends('admin.layouts.app')

@section('title', 'Category Detail - MuscleCart Admin')
@section('header', 'Category Detail')

@section('content')
<div class="max-w-3xl">
    <div class="bg-white rounded-lg shadow">
        <div class="p-6 border-b">
            <div class="flex items-center justify-between">
                <h3 class="text-lg font-semibold text-gray-800">{{ $category->name }}</h3>
                <div class="flex gap-2">
                    <a href="{{ route('admin.categories.edit', $category) }}" class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg transition text-sm">
                        <i class="fas fa-edit mr-1"></i> Edit
                    </a>
                    <a href="{{ route('admin.categories.index') }}" class="text-gray-600 hover:text-gray-800 px-4 py-2">
                        <i class="fas fa-arrow-left mr-1"></i> Back
                    </a>
                </div>
            </div>
        </div>
        <div class="p-6">
            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div>
                    <div class="w-full h-40 bg-gray-100 rounded-lg flex items-center justify-center overflow-hidden">
                        @if($category->image)
                            <img src="{{ asset('storage/' . $category->image) }}" alt="{{ $category->name }}" class="w-full h-full object-cover">
                        @else
                            <i class="fas fa-tags text-gray-300 text-4xl"></i>
                        @endif
                    </div>
                </div>
                <div class="md:col-span-2 space-y-4">
                    <div>
                        <p class="text-sm text-gray-500">Description</p>
                        <p class="text-gray-700">{{ $category->description ?: 'No description' }}</p>
                    </div>
                    <div>
                        <p class="text-sm text-gray-500">Status</p>
                        <span class="inline-block px-2 py-1 text-xs rounded {{ $category->is_active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800' }}">
                            {{ $category->is_active ? 'Active' : 'Inactive' }}
                        </span>
                    </div>
                    <div>
                        <p class="text-sm text-gray-500">Total Products</p>
                        <p class="text-2xl font-bold text-gray-800">{{ $category->products->count() }}</p>
                    </div>
                </div>
            </div>

            @if($category->products->count() > 0)
            <div class="mt-8">
                <h4 class="text-sm font-medium text-gray-500 mb-3">Products in this category</h4>
                <table class="w-full">
                    <thead>
                        <tr class="text-left text-gray-600 border-b text-sm">
                            <th class="pb-2">Name</th>
                            <th class="pb-2">Price</th>
                            <th class="pb-2">Stock</th>
                            <th class="pb-2">Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        @foreach($category->products as $product)
                        <tr class="border-b hover:bg-gray-50 text-sm">
                            <td class="py-2">{{ $product->name }}</td>
                            <td class="py-2">Rp {{ number_format($product->price, 0, ',', '.') }}</td>
                            <td class="py-2">{{ $product->stock_quantity }}</td>
                            <td class="py-2">
                                <span class="px-2 py-1 text-xs rounded {{ $product->is_active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800' }}">
                                    {{ $product->is_active ? 'Active' : 'Inactive' }}
                                </span>
                            </td>
                        </tr>
                        @endforeach
                    </tbody>
                </table>
            </div>
            @endif
        </div>
    </div>
</div>
@endsection
