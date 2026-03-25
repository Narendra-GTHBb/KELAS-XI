@extends('admin.layouts.app')

@section('title', 'User Detail - MuscleCart Admin')
@section('header', 'User Detail')

@section('content')
<div class="max-w-3xl">
    <div class="bg-white rounded-lg shadow">
        <div class="p-6 border-b">
            <div class="flex items-center justify-between">
                <h3 class="text-lg font-semibold text-gray-800">{{ $user->name }}</h3>
                <div class="flex gap-2">
                    <a href="{{ route('admin.users.edit', $user) }}" class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg transition text-sm">
                        <i class="fas fa-edit mr-1"></i> Edit
                    </a>
                    <a href="{{ route('admin.users.index') }}" class="text-gray-600 hover:text-gray-800 px-4 py-2">
                        <i class="fas fa-arrow-left mr-1"></i> Back
                    </a>
                </div>
            </div>
        </div>
        <div class="p-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div class="space-y-4">
                    <div>
                        <p class="text-sm text-gray-500">Email</p>
                        <p class="font-medium">{{ $user->email }}</p>
                    </div>
                    <div>
                        <p class="text-sm text-gray-500">Phone</p>
                        <p class="font-medium">{{ $user->phone ?? '-' }}</p>
                    </div>
                    <div>
                        <p class="text-sm text-gray-500">Role</p>
                        <span class="inline-block px-2 py-1 text-xs rounded {{ $user->role == 'admin' ? 'bg-red-100 text-red-800' : 'bg-blue-100 text-blue-800' }}">
                            {{ ucfirst($user->role) }}
                        </span>
                    </div>
                </div>
                <div class="space-y-4">
                    <div>
                        <p class="text-sm text-gray-500">Address</p>
                        <p class="font-medium">{{ $user->address ?? '-' }}</p>
                    </div>
                    <div>
                        <p class="text-sm text-gray-500">Status</p>
                        <span class="inline-block px-2 py-1 text-xs rounded {{ $user->is_active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800' }}">
                            {{ $user->is_active ? 'Active' : 'Inactive' }}
                        </span>
                    </div>
                    <div>
                        <p class="text-sm text-gray-500">Total Orders</p>
                        <p class="text-2xl font-bold text-gray-800">{{ $user->orders->count() }}</p>
                    </div>
                </div>
            </div>

            @if($user->orders->count() > 0)
            <div class="mt-8">
                <h4 class="text-sm font-medium text-gray-500 mb-3">Recent Orders</h4>
                <table class="w-full">
                    <thead>
                        <tr class="text-left text-gray-600 border-b text-sm">
                            <th class="pb-2">Order #</th>
                            <th class="pb-2">Total</th>
                            <th class="pb-2">Status</th>
                            <th class="pb-2">Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        @foreach($user->orders->take(10) as $order)
                        <tr class="border-b hover:bg-gray-50 text-sm">
                            <td class="py-2">
                                <a href="{{ route('admin.orders.show', $order) }}" class="text-blue-600 hover:underline">#{{ $order->id }}</a>
                            </td>
                            <td class="py-2">Rp {{ number_format($order->total_amount, 0, ',', '.') }}</td>
                            <td class="py-2">
                                <span class="px-2 py-1 text-xs rounded
                                    @if($order->status == 'pending') bg-yellow-100 text-yellow-800
                                    @elseif($order->status == 'delivered') bg-green-100 text-green-800
                                    @elseif($order->status == 'cancelled') bg-red-100 text-red-800
                                    @else bg-blue-100 text-blue-800
                                    @endif">
                                    {{ ucfirst($order->status) }}
                                </span>
                            </td>
                            <td class="py-2">{{ $order->created_at ? $order->created_at->format('d M Y') : '-' }}</td>
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
