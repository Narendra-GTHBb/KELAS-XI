<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\CartItem;
use App\Models\Notification;
use App\Models\Order;
use App\Models\OrderItem;
use App\Models\PointsHistory;
use App\Models\Product;
use App\Models\User;
use App\Models\Voucher;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Str;

class OrderController extends Controller
{
    /**
     * Display user's orders
     */
    public function index(Request $request)
    {
        $user = $request->user();

        $query = Order::with([
                'orderItems' => function ($q) {
                    $q->select('id', 'order_id', 'product_name', 'quantity', 'price', 'total');
                }
            ])
            ->select('id', 'user_id', 'order_number', 'total_amount', 'tax_amount', 'shipping_amount',
                     'discount_amount', 'voucher_code', 'points_earned', 'points_used', 'final_price',
                     'status', 'payment_status', 'payment_method', 'shipping_address', 'notes',
                     'tracking_number', 'courier', 'created_at', 'updated_at')
            ->where('user_id', $user->id);

        // Filter by status
        if ($request->has('status')) {
            $query->where('status', $request->status);
        }

        // Sort by created_at desc by default
        $orders = $query->orderBy('created_at', 'desc')->paginate(20);

        return response()->json([
            'status' => 'success',
            'data' => $orders->items(),
            'pagination' => [
                'current_page' => $orders->currentPage(),
                'last_page' => $orders->lastPage(),
                'per_page' => $orders->perPage(),
                'total' => $orders->total(),
            ]
        ]);
    }

    /**
     * Create new order from cart
     */
    public function store(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'payment_method'      => 'required|string',
            'shipping_address'    => 'required',
            'notes'               => 'nullable|string|max:500',
            'shipping_cost'       => 'nullable|integer|min:0',
            'courier'             => 'nullable|string|max:50',
            'courier_service'     => 'nullable|string|max:50',
            'destination_city_id' => 'nullable|string',
            'voucher_code'        => 'nullable|string|max:50',
            'points_used'         => 'nullable|integer|min:0',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'error',
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        // Normalize payment_method to match DB ENUM: cash, transfer, credit_card, e_wallet
        $rawPayment = strtolower(trim($request->input('payment_method')));
        $paymentMethodMap = [
            'cash on delivery' => 'cash',
            'cod'              => 'cash',
            'cash'             => 'cash',
            'bank transfer'    => 'transfer',
            'bank_transfer'    => 'transfer',
            'transfer'         => 'transfer',
            'credit card'      => 'credit_card',
            'credit_card'      => 'credit_card',
            'e-wallet'         => 'e_wallet',
            'e_wallet'         => 'e_wallet',
            'ewallet'          => 'e_wallet',
        ];
        $paymentMethod = $paymentMethodMap[$rawPayment] ?? 'cash';

        // Normalize shipping_address — accept string or object
        $rawAddress = $request->input('shipping_address');
        if (is_string($rawAddress)) {
            $shippingAddress = [
                'address'     => $rawAddress,
                'name'        => '',
                'phone'       => '',
                'city'        => '',
                'postal_code' => '',
                'province'    => '',
            ];
        } elseif (is_array($rawAddress)) {
            $shippingAddress = $rawAddress;
        } else {
            return response()->json([
                'status'  => 'error',
                'message' => 'Invalid shipping address format',
            ], 422);
        }

        $user = $request->user();

        // Get cart items
        $cartItems = CartItem::with('product')
            ->where('user_id', $user->id)
            ->get();

        if ($cartItems->isEmpty()) {
            return response()->json([
                'status' => 'error',
                'message' => 'Cart is empty'
            ], 400);
        }

        // Check stock availability for all items
        foreach ($cartItems as $cartItem) {
            if (!$cartItem->product || !$cartItem->product->is_active) {
                return response()->json([
                    'status' => 'error',
                    'message' => "Product '{$cartItem->product->name}' is not available"
                ], 400);
            }

            if ($cartItem->product->stock_quantity < $cartItem->quantity) {
                return response()->json([
                    'status' => 'error',
                    'message' => "Insufficient stock for '{$cartItem->product->name}'. Available: {$cartItem->product->stock_quantity}"
                ], 400);
            }
        }

        DB::beginTransaction();

        try {
            // Calculate totals
            $subtotal = $cartItems->sum(function($item) {
                return $item->quantity * $item->product->price;
            });

            $taxAmount = $subtotal * 0.10;
            $shippingAmount = $request->input('shipping_cost', 15000);

            // Apply voucher if provided
            $discountAmount = 0;
            $voucherCode    = null;
            $voucherInput   = $request->input('voucher_code');
            if ($voucherInput) {
                $voucher = Voucher::where('code', strtoupper(trim($voucherInput)))->first();
                if ($voucher && $voucher->isValid() && $subtotal >= (float) $voucher->min_purchase) {
                    $discountAmount = $voucher->calculateDiscount($subtotal);
                    $voucherCode    = $voucher->code;
                    $voucher->increment('used_count');
                }
            }

            $totalAmount = $subtotal + $taxAmount + $shippingAmount - $discountAmount;

            // Apply reward points if provided
            $pointsUsed    = 0;
            $pointsDiscount = 0;
            $requestedPoints = (int) $request->input('points_used', 0);
            if ($requestedPoints > 0) {
                $userPoints = (int) $user->points;
                // Validate: cannot use more than owned
                $maxPointsByBalance = $userPoints;
                // Validate: max discount = 20% of total (before points)
                // Rate: 10 pts = Rp 1 discount
                $maxDiscountByRule = $totalAmount * 0.20;
                $maxPointsByRule   = (int) floor($maxDiscountByRule * 10);
                $pointsUsed = min($requestedPoints, $maxPointsByBalance, $maxPointsByRule);
                $pointsDiscount = (int) floor($pointsUsed / 10);
            }

            $finalPrice = $totalAmount - $pointsDiscount;

            // Calculate points to be earned (awarded when order completes)
            $earnedPoints = User::calculateEarnedPoints($totalAmount);

            // Deduct used points from user balance immediately
            if ($pointsUsed > 0) {
                $user->decrement('points', $pointsUsed);
            }

            // Generate order number
            $orderNumber = 'ORD-' . date('Ymd') . '-' . strtoupper(Str::random(6));

            // Create order
            $order = Order::create([
                'order_number'    => $orderNumber,
                'user_id'         => $user->id,
                'total_amount'    => $totalAmount,
                'tax_amount'      => $taxAmount,
                'shipping_amount' => $shippingAmount,
                'discount_amount' => $discountAmount,
                'voucher_code'    => $voucherCode,
                'points_used'     => $pointsUsed,
                'points_earned'   => $earnedPoints,
                'final_price'     => $finalPrice,
                'status'          => 'pending',
                'payment_status'  => 'pending',
                'payment_method'  => $paymentMethod,
                'shipping_address' => $shippingAddress,
                'billing_address' => $request->billing_address ?? $shippingAddress,
                'notes'           => $request->notes,
                'courier'         => $request->input('courier'),
            ]);

            // Record points redemption history
            if ($pointsUsed > 0) {
                PointsHistory::create([
                    'user_id'     => $user->id,
                    'order_id'    => $order->id,
                    'points'      => $pointsUsed,
                    'type'        => 'redeem',
                    'description' => "Redeem poin untuk pesanan #{$order->order_number}",
                ]);
            }

            // Create order items and update stock
            foreach ($cartItems as $cartItem) {
                // Create order item
                OrderItem::create([
                    'order_id' => $order->id,
                    'product_id' => $cartItem->product_id,
                    'product_name' => $cartItem->product->name,
                    'quantity' => $cartItem->quantity,
                    'price' => $cartItem->product->price,
                    'total' => $cartItem->quantity * $cartItem->product->price,
                ]);

                // Update product stock
                $cartItem->product->decrement('stock_quantity', $cartItem->quantity);
            }

            // Clear cart
            CartItem::where('user_id', $user->id)->delete();

            DB::commit();

            // Notify user order placed
            Notification::send(
                $user->id,
                'Pesanan Berhasil Dibuat',
                "Pesanan #{$order->order_number} berhasil dibuat.",
                'order_update',
                $order->id,
                'order'
            );

            // Add points & points_used to response
            $order->load(['orderItems', 'orderItems.product']);

            return response()->json([
                'status' => 'success',
                'message' => 'Order created successfully',
                'data' => [
                    'order'           => $order,
                    'points_used'     => $pointsUsed,
                    'points_discount' => $pointsDiscount,
                    'points_earned_pending' => $earnedPoints,
                    'user_points_balance'   => (int) $user->fresh()->points,
                ]
            ], 201);

        } catch (\Exception $e) {
            DB::rollBack();

            return response()->json([
                'status' => 'error',
                'message' => 'Failed to create order',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Display specific order details
     */
    public function show($id, Request $request)
    {
        $user = $request->user();

        $order = Order::with([
                'orderItems' => function ($q) {
                    $q->select('id', 'order_id', 'product_id', 'product_name', 'quantity', 'price', 'total');
                },
                'orderItems.product' => function ($q) {
                    $q->select('id', 'name', 'price', 'image_url');
                },
                'statusHistories' => function ($q) {
                    $q->orderBy('created_at', 'asc');
                }
            ])
            ->select('id', 'user_id', 'order_number', 'total_amount', 'tax_amount', 'shipping_amount',
                     'discount_amount', 'voucher_code', 'points_earned', 'points_used', 'final_price',
                     'status', 'payment_status', 'payment_method', 'shipping_address', 'notes',
                     'tracking_number', 'courier', 'paid_at', 'completed_at',
                     'created_at', 'updated_at')
            ->where('id', $id)
            ->where('user_id', $user->id)
            ->first();

        if (!$order) {
            return response()->json([
                'status' => 'error',
                'message' => 'Order not found'
            ], 404);
        }

        return response()->json([
            'status' => 'success',
            'data' => $order
        ]);
    }

    /**
     * Cancel an order
     */
    public function cancel($id, Request $request)
    {
        $user = $request->user();

        $order = Order::with('orderItems.product')
            ->where('id', $id)
            ->where('user_id', $user->id)
            ->first();

        if (!$order) {
            return response()->json([
                'status' => 'error',
                'message' => 'Order not found'
            ], 404);
        }

        // Check if order can be cancelled
        if ($order->status !== 'pending') {
            return response()->json([
                'status' => 'error',
                'message' => 'Order cannot be cancelled. Current status: ' . $order->status
            ], 400);
        }

        DB::beginTransaction();

        try {
            // Restore product stock
            foreach ($order->orderItems as $orderItem) {
                if ($orderItem->product) {
                    $orderItem->product->increment('stock_quantity', $orderItem->quantity);
                }
            }

            // Update order status
            $order->update([
                'status' => 'cancelled',
                'payment_status' => 'cancelled'
            ]);

            DB::commit();

            return response()->json([
                'status' => 'success',
                'message' => 'Order cancelled successfully',
                'data' => [
                    'order' => $order
                ]
            ]);

        } catch (\Exception $e) {
            DB::rollBack();

            return response()->json([
                'status' => 'error',
                'message' => 'Failed to cancel order',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Confirm order received by customer (sets status to completed)
     */
    public function confirmReceived($id, Request $request)
    {
        $user = $request->user();

        $order = Order::with(['orderItems.product', 'statusHistories'])
            ->where('id', $id)
            ->where('user_id', $user->id)
            ->first();

        if (!$order) {
            return response()->json([
                'status' => 'error',
                'message' => 'Order not found'
            ], 404);
        }

        if (!$order->canTransitionTo('completed')) {
            return response()->json([
                'status' => 'error',
                'message' => 'Status tidak bisa diubah ke selesai. Status saat ini: ' . $order->status
            ], 400);
        }

        $order->transitionTo('completed', 'Dikonfirmasi diterima oleh pelanggan', $user->id, 'customer');

        return response()->json([
            'status' => 'success',
            'message' => 'Pesanan berhasil dikonfirmasi diterima',
            'data' => [
                'order'                => $order->fresh(['orderItems.product', 'statusHistories']),
                'points_earned'        => (int) $order->points_earned,
                'user_points_balance'  => (int) $user->fresh()->points,
            ]
        ]);
    }
}
