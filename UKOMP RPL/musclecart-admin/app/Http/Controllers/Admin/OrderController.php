<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Order;
use Illuminate\Http\Request;

class OrderController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        $query = Order::with('user');

        if ($request->has('status') && $request->status) {
            $query->where('status', $request->status);
        }

        if ($request->has('search') && $request->search) {
            $search = $request->search;
            $query->where(function ($q) use ($search) {
                $q->where('order_number', 'like', "%{$search}%")
                  ->orWhereHas('user', function ($q2) use ($search) {
                      $q2->where('name', 'like', "%{$search}%");
                  });
            });
        }

        $orders = $query->orderBy('created_at', 'desc')->paginate(15);

        $stats = [
            'total_orders'      => Order::count(),
            'pending_orders'    => Order::where('status', 'pending')->count(),
            'processing_orders' => Order::where('status', 'processing')->count(),
            'shipped_orders'    => Order::where('status', 'shipped')->count(),
            'delivered_orders'  => Order::where('status', 'delivered')->count(),
            'cancelled_orders'  => Order::where('status', 'cancelled')->count(),
            'total_revenue'     => Order::whereIn('status', ['processing', 'shipped', 'delivered', 'completed'])
                ->sum('total_amount'),
        ];

        return view('admin.orders.index', compact('orders', 'stats'));
    }

    /**
     * Orders are created by customers, not from admin panel.
     */
    public function create()
    {
        return redirect()->route('admin.orders.index')
            ->with('info', 'Orders are created by customers through the app.');
    }

    public function store(Request $request)
    {
        return redirect()->route('admin.orders.index')
            ->with('info', 'Orders are created by customers through the app.');
    }

    /**
     * Display the specified resource.
     */
    public function show(Order $order)
    {
        $order->load(['user', 'orderItems.product', 'statusHistories']);
        $allowedTransitions = Order::ALLOWED_TRANSITIONS[$order->status] ?? [];
        return view('admin.orders.show', compact('order', 'allowedTransitions'));
    }

    /**
     * Redirect edit to show — all order management is on the show page.
     */
    public function edit(Order $order)
    {
        return redirect()->route('admin.orders.show', $order);
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Order $order)
    {
        $validated = $request->validate([
            'status'          => 'required|in:pending,paid,processing,shipped,delivered,completed,cancelled',
            'tracking_number' => 'nullable|string|max:100',
            'courier'         => 'nullable|string|max:100',
            'notes'           => 'nullable|string',
        ]);

        // Update tracking/courier fields regardless of status change
        $trackingUpdates = array_filter([
            'tracking_number' => $validated['tracking_number'] ?? null,
            'courier'         => $validated['courier'] ?? null,
            'notes'           => $validated['notes'] ?? null,
        ], fn($v) => $v !== null);

        if (!empty($trackingUpdates)) {
            $order->update($trackingUpdates);
        }

        if ($validated['status'] !== $order->status) {
            $note = $validated['notes'] ?? null;
            if (!$order->transitionTo($validated['status'], $note, auth()->id(), 'admin')) {
                return redirect()->back()
                    ->with('error', "Cannot change status from '{$order->status}' to '{$validated['status']}'.");
            }
        }

        return redirect()->route('admin.orders.show', $order)
            ->with('success', 'Order updated successfully.');
    }

    /**
     * Remove the specified resource from storage (soft delete).
     */
    public function destroy(Order $order)
    {
        $order->delete();

        return redirect()->route('admin.orders.index')
            ->with('success', 'Order deleted successfully.');
    }
}
