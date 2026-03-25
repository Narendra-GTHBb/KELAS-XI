<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\CartItem;
use App\Models\Product;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;

class CartController extends Controller
{
    /**
     * Display user's cart items
     */
    public function index(Request $request)
    {
        $user = $request->user();

        $cartItems = CartItem::with(['product', 'product.category'])
            ->where('user_id', $user->id)
            ->get();

        // Calculate totals
        $subtotal = $cartItems->sum(function($item) {
            return $item->quantity * ($item->product ? $item->product->price : 0);
        });

        $totalItems = $cartItems->sum('quantity');

        return response()->json([
            'status' => 'success',
            'data' => [
                'items' => $cartItems->map(function($item) {
                    return [
                        'id' => $item->id,
                        'product' => $item->product ? [
                            'id' => $item->product->id,
                            'name' => $item->product->name,
                            'price' => $item->product->price,
                            'image' => $item->product->image_url,
                            'image_url' => $item->product->image_url,
                            'full_image_url' => $item->product->full_image_url,
                            'stock_quantity' => $item->product->stock_quantity,
                            'stock' => $item->product->stock_quantity,
                            'is_active' => $item->product->is_active,
                            'category_id' => $item->product->category_id,
                            'category' => $item->product->category ? [
                                'id' => $item->product->category->id,
                                'name' => $item->product->category->name,
                            ] : null,
                        ] : null,
                        'product_id' => $item->product_id,
                        'quantity' => $item->quantity,
                        'subtotal' => $item->quantity * ($item->product ? $item->product->price : 0),
                        'added_at' => $item->created_at,
                    ];
                }),
                'summary' => [
                    'total_items' => $totalItems,
                    'subtotal' => $subtotal,
                    'tax' => 0, // Add tax calculation if needed
                    'shipping' => 0, // Add shipping calculation if needed
                    'total' => $subtotal,
                ]
            ]
        ]);
    }

    /**
     * Add product to cart
     */
    public function add(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'product_id' => 'required|integer|exists:products,id',
            'quantity' => 'required|integer|min:1',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'error',
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $user = $request->user();
        $product = Product::where('is_active', true)->find($request->product_id);

        if (!$product) {
            return response()->json([
                'status' => 'error',
                'message' => 'Product not found or inactive'
            ], 404);
        }

        // Check stock availability
        if ($product->stock_quantity < $request->quantity) {
            return response()->json([
                'status' => 'error',
                'message' => 'Insufficient stock. Available: ' . $product->stock_quantity
            ], 400);
        }

        try {
            // Check if item already exists in cart
            $existingCartItem = CartItem::where('user_id', $user->id)
                ->where('product_id', $request->product_id)
                ->first();

            if ($existingCartItem) {
                // Update quantity
                $newQuantity = $existingCartItem->quantity + $request->quantity;
                
                // Check stock for new quantity
                if ($product->stock_quantity < $newQuantity) {
                    return response()->json([
                        'status' => 'error',
                        'message' => 'Insufficient stock. Available: ' . $product->stock_quantity . ', Current in cart: ' . $existingCartItem->quantity
                    ], 400);
                }

                $existingCartItem->update([
                    'quantity' => $newQuantity,
                    'price' => $product->price,
                ]);
                $cartItem = $existingCartItem;
            } else {
                // Create new cart item
                $cartItem = CartItem::create([
                    'user_id' => $user->id,
                    'product_id' => $request->product_id,
                    'quantity' => $request->quantity,
                    'price' => $product->price,
                ]);
            }

            // Load relationships
            $cartItem->load(['product', 'product.category']);

            return response()->json([
                'status' => 'success',
                'message' => 'Product added to cart successfully',
                'data' => [
                    'cart_item' => [
                        'id' => $cartItem->id,
                        'product' => [
                            'id' => $cartItem->product->id,
                            'name' => $cartItem->product->name,
                            'price' => $cartItem->product->price,
                            'image' => $cartItem->product->image,
                        ],
                        'quantity' => $cartItem->quantity,
                        'subtotal' => $cartItem->quantity * $cartItem->product->price,
                    ]
                ]
            ]);

        } catch (\Exception $e) {
            return response()->json([
                'status' => 'error',
                'message' => 'Failed to add product to cart',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Update cart item quantity
     */
    public function update($id, Request $request)
    {
        $validator = Validator::make($request->all(), [
            'quantity' => 'required|integer|min:1',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'status' => 'error',
                'message' => 'Validation failed',
                'errors' => $validator->errors()
            ], 422);
        }

        $user = $request->user();
        $cartItem = CartItem::with('product')
            ->where('id', $id)
            ->where('user_id', $user->id)
            ->first();

        if (!$cartItem) {
            return response()->json([
                'status' => 'error',
                'message' => 'Cart item not found'
            ], 404);
        }

        // Check stock availability
        if ($cartItem->product->stock_quantity < $request->quantity) {
            return response()->json([
                'status' => 'error',
                'message' => 'Insufficient stock. Available: ' . $cartItem->product->stock_quantity
            ], 400);
        }

        try {
            $cartItem->update(['quantity' => $request->quantity]);

            return response()->json([
                'status' => 'success',
                'message' => 'Cart item updated successfully',
                'data' => [
                    'cart_item' => [
                        'id' => $cartItem->id,
                        'quantity' => $cartItem->quantity,
                        'subtotal' => $cartItem->quantity * $cartItem->product->price,
                    ]
                ]
            ]);

        } catch (\Exception $e) {
            return response()->json([
                'status' => 'error',
                'message' => 'Failed to update cart item',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Remove item from cart
     */
    public function remove($id, Request $request)
    {
        $user = $request->user();
        $cartItem = CartItem::where('id', $id)
            ->where('user_id', $user->id)
            ->first();

        if (!$cartItem) {
            return response()->json([
                'status' => 'error',
                'message' => 'Cart item not found'
            ], 404);
        }

        try {
            $cartItem->delete();

            return response()->json([
                'status' => 'success',
                'message' => 'Item removed from cart successfully'
            ]);

        } catch (\Exception $e) {
            return response()->json([
                'status' => 'error',
                'message' => 'Failed to remove item from cart',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * Clear all items from user's cart
     */
    public function clear(Request $request)
    {
        $user = $request->user();

        try {
            CartItem::where('user_id', $user->id)->delete();

            return response()->json([
                'status' => 'success',
                'message' => 'Cart cleared successfully'
            ]);

        } catch (\Exception $e) {
            return response()->json([
                'status' => 'error',
                'message' => 'Failed to clear cart',
                'error' => $e->getMessage()
            ], 500);
        }
    }
}
