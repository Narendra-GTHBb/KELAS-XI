<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Order;
use App\Models\OrderItem;
use App\Models\CartItem;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class OrderController extends Controller
{
    private function normalizePaymentMethod(string $method): string
    {
        $map = [
            'cash on delivery' => 'cash', 'cash_on_delivery' => 'cash', 'cod' => 'cash', 'cash' => 'cash',
            'credit card' => 'credit_card', 'credit_card' => 'credit_card',
            'bank transfer' => 'transfer', 'bank_transfer' => 'transfer', 'transfer' => 'transfer',
            'e_wallet' => 'e_wallet', 'e-wallet' => 'e_wallet', 'ewallet' => 'e_wallet',
        ];
        return $map[strtolower(trim($method))] ?? 'cash';
    }

    private function formatOrder(Order $order): array
    {
        $addr = $order->shipping_address;
        $addressStr = is_array($addr)
            ? implode(', ', array_filter([$addr['address'] ?? '', $addr['city'] ?? '', $addr['postal_code'] ?? '']))
            : (string) $addr;

        return [
            'id'              => $order->id,
            'order_number'    => $order->order_number,
            'status'          => $order->status,
            'payment_status'  => $order->payment_status,
            'payment_method'  => $order->payment_method,
            'total_price'     => (float) $order->total_amount,
            'tax_amount'      => (float) $order->tax_amount,
            'shipping_amount' => (float) $order->shipping_amount,
            'shipping_address'=> $addressStr,
            'tracking_number' => $order->tracking_number,
            'courier'         => $order->courier,
            'notes'           => $order->notes,
            'created_at'      => $order->created_at?->timestamp * 1000,
            'updated_at'      => $order->updated_at?->timestamp * 1000,
            'paid_at'         => $order->paid_at?->timestamp * 1000,
            'shipped_at'      => $order->shipped_at?->timestamp * 1000,
            'delivered_at'    => $order->delivered_at?->timestamp * 1000,
            'completed_at'    => $order->completed_at?->timestamp * 1000,
            'items'           => $order->orderItems->map(fn($item) => [
                'id'         => $item->id,
                'order_id'   => $item->order_id,
                'product_id' => $item->product_id,
                'quantity'   => $item->quantity,
                'price'      => (float) $item->price,
                'created_at' => $order->created_at?->timestamp * 1000,
                'updated_at' => $order->updated_at?->timestamp * 1000,
                'product'    => $item->product ? [
                    'id'        => $item->product->id,
                    'name'      => $item->product->name,
                    'image_url' => $item->product->image_url,
                    'price'     => (float) $item->product->price,
                ] : null,
            ])->values()->toArray(),
            'status_history' => $order->statusHistories->map(fn($h) => [
                'status'          => $h->status,
                'previous_status' => $h->previous_status,
                'note'            => $h->note,
                'created_at'      => $h->created_at?->timestamp * 1000,
            ])->values()->toArray(),
        ];
    }

    /** GET /orders â€” list all orders (paginated) */
    public function index(Request $request)
    {
        $orders = Order::with(['orderItems.product', 'statusHistories'])
            ->where('user_id', $request->user()->id)
            ->orderBy('created_at', 'desc')
            ->get();

        return response()->json([
            'status' => 'success',
            'data'   => $orders->map(fn($o) => $this->formatOrder($o)),
        ]);
    }

    /** POST /orders â€” create order from cart */
    public function store(Request $request)
    {
        $request->validate([
            'payment_method'   => 'required|string',
            'shipping_address' => 'required',
            'shipping_amount'  => 'nullable|numeric|min:0',
            'notes'            => 'nullable|string',
        ]);

        $paymentMethod  = $this->normalizePaymentMethod($request->input('payment_method'));
        $rawAddress     = $request->input('shipping_address');
        $shippingAddress = is_string($rawAddress)
            ? ['address' => $rawAddress, 'city' => '', 'postal_code' => '']
            : (is_array($rawAddress) ? $rawAddress : null);

        if (!$shippingAddress) {
            return response()->json(['status' => 'error', 'message' => 'Invalid shipping address'], 422);
        }

        $cartItems = CartItem::with('product')
            ->where('user_id', $request->user()->id)
            ->get();

        if ($cartItems->isEmpty()) {
            return response()->json(['status' => 'error', 'message' => 'Cart is empty'], 400);
        }

        foreach ($cartItems as $item) {
            if ($item->product->stock_quantity < $item->quantity) {
                return response()->json([
                    'status'  => 'error',
                    'message' => "Insufficient stock for: {$item->product->name}",
                ], 400);
            }
        }

        DB::beginTransaction();
        try {
            $subtotal      = $cartItems->sum(fn($i) => $i->price * $i->quantity);
            $taxAmount     = $subtotal * 0.1;
            $shippingAmt   = (float) $request->input('shipping_amount', 0);
            $totalAmount   = $subtotal + $taxAmount + $shippingAmt;

            $order = Order::create([
                'order_number'    => 'ORD-' . strtoupper(uniqid()),
                'user_id'         => $request->user()->id,
                'total_amount'    => $totalAmount,
                'tax_amount'      => $taxAmount,
                'shipping_amount' => $shippingAmt,
                'status'          => 'pending',
                'payment_status'  => 'pending',
                'payment_method'  => $paymentMethod,
                'shipping_address'=> $shippingAddress,
                'billing_address' => $shippingAddress,
                'notes'           => $request->input('notes'),
            ]);

            foreach ($cartItems as $cartItem) {
                OrderItem::create([
                    'order_id'     => $order->id,
                    'product_id'   => $cartItem->product_id,
                    'product_name' => $cartItem->product->name,
                    'quantity'     => $cartItem->quantity,
                    'price'        => $cartItem->price,
                    'total'        => $cartItem->price * $cartItem->quantity,
                ]);
                $cartItem->product->decrement('stock_quantity', $cartItem->quantity);
            }

            CartItem::where('user_id', $request->user()->id)->delete();
            DB::commit();

            $order->load(['orderItems.product', 'statusHistories']);
            return response()->json([
                'status'  => 'success',
                'message' => 'Order created successfully',
                'data'    => $this->formatOrder($order),
            ], 201);

        } catch (\Exception $e) {
            DB::rollBack();
            return response()->json(['status' => 'error', 'message' => 'Failed: ' . $e->getMessage()], 500);
        }
    }

    /** GET /orders/{id} */
    public function show(Request $request, $id)
    {
        $order = Order::with(['orderItems.product', 'statusHistories'])
            ->where('id', $id)
            ->where('user_id', $request->user()->id)
            ->first();

        if (!$order) {
            return response()->json(['status' => 'error', 'message' => 'Order not found'], 404);
        }

        return response()->json(['status' => 'success', 'data' => $this->formatOrder($order)]);
    }

    /** PUT /orders/{id}/confirm-received â€” user konfirmasi barang diterima */
    public function confirmReceived(Request $request, $id)
    {
        $order = Order::where('id', $id)->where('user_id', $request->user()->id)->first();

        if (!$order) {
            return response()->json(['status' => 'error', 'message' => 'Order not found'], 404);
        }
        if ($order->status !== 'delivered') {
            return response()->json(['status' => 'error', 'message' => 'Order has not been delivered yet'], 400);
        }

        $order->transitionTo('completed', 'Confirmed received by customer', $request->user()->id, 'user');
        $order->load(['orderItems.product', 'statusHistories']);

        return response()->json([
            'status'  => 'success',
            'message' => 'Thank you! Order marked as completed.',
            'data'    => $this->formatOrder($order),
        ]);
    }

    /** PUT /orders/{id}/cancel */
    public function cancel(Request $request, $id)
    {
        $order = Order::with('orderItems.product')
            ->where('id', $id)
            ->where('user_id', $request->user()->id)
            ->first();

        if (!$order) {
            return response()->json(['status' => 'error', 'message' => 'Order not found'], 404);
        }
        if (!$order->canTransitionTo('cancelled')) {
            return response()->json(['status' => 'error', 'message' => 'Order cannot be cancelled at this stage'], 400);
        }

        DB::beginTransaction();
        try {
            foreach ($order->orderItems as $item) {
                if ($item->product) {
                    $item->product->increment('stock_quantity', $item->quantity);
                }
            }
            $order->transitionTo('cancelled', 'Cancelled by customer', $request->user()->id, 'user');
            DB::commit();

            $order->load(['orderItems.product', 'statusHistories']);
            return response()->json([
                'status'  => 'success',
                'message' => 'Order cancelled',
                'data'    => $this->formatOrder($order),
            ]);
        } catch (\Exception $e) {
            DB::rollBack();
            return response()->json(['status' => 'error', 'message' => 'Failed: ' . $e->getMessage()], 500);
        }
    }
}
