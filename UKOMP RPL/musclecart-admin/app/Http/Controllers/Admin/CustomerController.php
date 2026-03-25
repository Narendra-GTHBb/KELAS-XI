<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\User;
use App\Models\Order;
use Illuminate\Http\Request;
use Carbon\Carbon;

class CustomerController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        // Base query for customers (users with role 'customer' or not 'admin')
        $query = User::where('role', '!=', 'admin')
            ->withCount('orders')
            ->withSum('orders as total_spent', 'total_amount');

        // Search functionality
        if ($request->has('search') && $request->search) {
            $search = $request->search;
            $query->where(function ($q) use ($search) {
                $q->where('name', 'like', "%{$search}%")
                  ->orWhere('email', 'like', "%{$search}%");
            });
        }

        // Status filter
        if ($request->has('status') && $request->status !== 'all') {
            if ($request->status === 'active') {
                $query->where('is_active', true);
            } elseif ($request->status === 'blocked') {
                $query->where('is_active', false);
            }
        }

        $customers = $query->orderBy('created_at', 'desc')->paginate(15);

        // Calculate statistics
        $stats = [
            'total_customers' => User::where('role', '!=', 'admin')->count(),
            'active_customers' => User::where('role', '!=', 'admin')
                ->where('is_active', true)
                ->count(),
            'new_this_month' => User::where('role', '!=', 'admin')
                ->whereYear('created_at', Carbon::now()->year)
                ->whereMonth('created_at', Carbon::now()->month)
                ->count(),
            'blocked_customers' => User::where('role', '!=', 'admin')
                ->where('is_active', false)
                ->count(),
        ];

        return view('admin.customers.index', compact('customers', 'stats'));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        return view('admin.customers.create');
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|email|unique:users,email',
            'password' => 'required|string|min:8',
        ]);

        $validated['password'] = bcrypt($validated['password']);
        
        User::create($validated);

        return redirect()->route('admin.customers.index')
            ->with('success', 'Customer created successfully.');
    }

    /**
     * Display the specified resource.
     */
    public function show(string $id)
    {
        $customer = User::findOrFail($id);
        return view('admin.customers.show', compact('customer'));
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(string $id)
    {
        $customer = User::findOrFail($id);
        return view('admin.customers.edit', compact('customer'));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, string $id)
    {
        $customer = User::findOrFail($id);
        
        $validated = $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|email|unique:users,email,' . $id,
        ]);

        $customer->update($validated);

        return redirect()->route('admin.customers.index')
            ->with('success', 'Customer updated successfully.');
    }

    /**
     * Ban or unban the customer (toggle is_active status).
     */
    public function destroy(Request $request, string $id)
    {
        $customer = User::findOrFail($id);
        
        // Toggle the is_active status
        $newStatus = !$customer->is_active;
        $customer->is_active = $newStatus;
        $customer->save();
        
        $message = $newStatus ? 'Customer unbanned successfully.' : 'Customer banned successfully.';

        // Handle AJAX requests
        if ($request->wantsJson() || $request->ajax()) {
            return response()->json([
                'success' => true,
                'message' => $message
            ]);
        }

        return redirect()->route('admin.customers.index')
            ->with('success', $message);
    }
}
