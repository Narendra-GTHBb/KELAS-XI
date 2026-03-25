@extends('admin.layouts.app')

@section('title', 'Users - MuscleCart Admin')
@section('header', 'Users')

@section('content')
<div class="bg-white rounded-lg shadow">
    <div class="p-6 border-b flex justify-between items-center">
        <h3 class="text-lg font-semibold text-gray-800">All Users</h3>
        <a href="{{ route('admin.users.create') }}" class="bg-orange-500 hover:bg-orange-600 text-white px-4 py-2 rounded">
            <i class="fas fa-plus mr-2"></i>Add User
        </a>
    </div>
    <div class="p-6">
        <table class="w-full">
            <thead>
                <tr class="text-left text-gray-600 border-b">
                    <th class="pb-3">ID</th>
                    <th class="pb-3">Name</th>
                    <th class="pb-3">Email</th>
                    <th class="pb-3">Phone</th>
                    <th class="pb-3">Role</th>
                    <th class="pb-3">Orders</th>
                    <th class="pb-3">Actions</th>
                </tr>
            </thead>
            <tbody>
                @forelse($users as $user)
                <tr class="border-b hover:bg-gray-50">
                    <td class="py-3">{{ $user->id }}</td>
                    <td class="py-3">{{ $user->name }}</td>
                    <td class="py-3">{{ $user->email }}</td>
                    <td class="py-3">{{ $user->phone ?? '-' }}</td>
                    <td class="py-3">
                        <span class="inline-block px-2 py-1 text-xs rounded {{ $user->role == 'admin' ? 'bg-red-100 text-red-800' : 'bg-blue-100 text-blue-800' }}">
                            {{ ucfirst($user->role) }}
                        </span>
                    </td>
                    <td class="py-3">{{ $user->orders_count ?? $user->orders->count() }}</td>
                    <td class="py-3">
                        <a href="{{ route('admin.users.edit', $user) }}" class="text-blue-600 hover:text-blue-800 mr-3">
                            <i class="fas fa-edit"></i>
                        </a>
                        <form action="{{ route('admin.users.destroy', $user) }}" method="POST" class="inline" onsubmit="return confirm('Are you sure?')">
                            @csrf
                            @method('DELETE')
                            <button type="submit" class="text-red-600 hover:text-red-800">
                                <i class="fas fa-trash"></i>
                            </button>
                        </form>
                    </td>
                </tr>
                @empty
                <tr>
                    <td colspan="7" class="py-6 text-center text-gray-500">No users found</td>
                </tr>
                @endforelse
            </tbody>
        </table>
    </div>
</div>
@endsection
