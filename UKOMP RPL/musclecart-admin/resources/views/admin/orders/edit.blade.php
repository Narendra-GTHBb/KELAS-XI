@extends('admin.layouts.app')

@section('title', 'Edit Order - MuscleCart Admin')
@section('header', 'Edit Order')

@section('content')
<div class="max-w-2xl">
    <div class="bg-white rounded-lg shadow">
        <div class="p-6 border-b">
            <div class="flex items-center justify-between">
                <h3 class="text-lg font-semibold text-gray-800">Edit Order #{{ $order->id }}</h3>
                <a href="{{ route('admin.orders.index') }}" class="text-gray-600 hover:text-gray-800">
                    <i class="fas fa-arrow-left mr-1"></i> Back to Orders
                </a>
            </div>
        </div>
        <form action="{{ route('admin.orders.update', $order) }}" method="POST" class="p-6 space-y-6">
            @csrf
            @method('PUT')

            <div>
                <label for="status" class="block text-sm font-medium text-gray-700 mb-1">Order Status</label>
                <select name="status" id="status"
                    class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-orange-500 focus:border-transparent">
                    @foreach(['pending', 'confirmed', 'processing', 'shipped', 'delivered', 'cancelled'] as $status)
                        <option value="{{ $status }}" {{ old('status', $order->status) == $status ? 'selected' : '' }}>{{ ucfirst($status) }}</option>
                    @endforeach
                </select>
            </div>

            <div>
                <label for="payment_status" class="block text-sm font-medium text-gray-700 mb-1">Payment Status</label>
                <select name="payment_status" id="payment_status"
                    class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-orange-500 focus:border-transparent">
                    @foreach(['pending', 'paid', 'failed', 'refunded'] as $pstatus)
                        <option value="{{ $pstatus }}" {{ old('payment_status', $order->payment_status) == $pstatus ? 'selected' : '' }}>{{ ucfirst($pstatus) }}</option>
                    @endforeach
                </select>
            </div>

            <div>
                <label for="notes" class="block text-sm font-medium text-gray-700 mb-1">Notes</label>
                <textarea name="notes" id="notes" rows="3"
                    class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-orange-500 focus:border-transparent">{{ old('notes', $order->notes) }}</textarea>
            </div>

            <!-- Order Summary (read-only) -->
            <div class="bg-gray-50 rounded-lg p-4">
                <p class="text-sm text-gray-500 mb-2">Order Summary</p>
                <p class="text-sm">Customer: <span class="font-medium">{{ $order->user->name ?? 'Guest' }}</span></p>
                <p class="text-sm">Total: <span class="font-medium text-orange-600">Rp {{ number_format($order->total_amount, 0, ',', '.') }}</span></p>
                <p class="text-sm">Items: <span class="font-medium">{{ $order->orderItems->count() }}</span></p>
            </div>

            <div class="flex gap-3 pt-4 border-t">
                <button type="submit" class="bg-orange-500 hover:bg-orange-600 text-white px-6 py-2 rounded-lg transition">
                    <i class="fas fa-save mr-2"></i>Update Order
                </button>
                <a href="{{ route('admin.orders.show', $order) }}" class="bg-gray-300 hover:bg-gray-400 text-gray-800 px-6 py-2 rounded-lg transition">
                    Cancel
                </a>
            </div>
        </form>
    </div>
</div>
@endsection
