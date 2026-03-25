<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\CartItem;
use App\Models\Product;
use Illuminate\Http\Request;

class CartController extends Controller
{
    /**
     * Get user's cart items
     */
    public function index(Request $request)
    {
        $cartItems = CartItem::with('product.category')
            ->where('user_id', $request->user()->id)
            ->get();

        $total = $cartItems->sum(function ($item) {
            return $item->price * $item->quantity;
        });

        return response()->json([
            'status' => 'success',
            'data' => [
                'items' => $cartItems,
                'total' => $total,
                'count' => $cartItems->count()
            ]
        ]);
    }

    /**
     * Add item to cart
     */
    public function add(Request $request)
    {
        $validated = $request->validate([
            'product_id' => 'required|exists:products,id',
            'quantity' => 'required|integer|min:1',
        ]);

        $product = Product::find($validated['product_id']);

        if (!$product->is_active) {
            return response()->json([
                'status' => 'error',
                'message' => 'Product is not available'
            ], 400);
        }

        if ($product->stock_quantity < $validated['quantity']) {
            return response()->json([
                'status' => 'error',
                'message' => 'Insufficient stock'
            ], 400);
        }

        // Check if item already in cart
        $existingItem = CartItem::where('user_id', $request->user()->id)
            ->where('product_id', $validated['product_id'])
            ->first();

        if ($existingItem) {
            // Update existing cart item
            $newQuantity = $existingItem->quantity + $validated['quantity'];

            if ($product->stock_quantity < $newQuantity) {
                return response()->json([
                    'status' => 'error',
                    'message' => 'Insufficient stock for requested quantity'
                ], 400);
            }

            $existingItem->quantity = $newQuantity;
            $existingItem->price = $product->price;
            $existingItem->save();
            $cartItem = $existingItem;
        } else {
            // Create new cart item
            $cartItem = CartItem::create([
                'user_id' => $request->user()->id,
                'product_id' => $validated['product_id'],
                'quantity' => $validated['quantity'],
                'price' => $product->price
            ]);
        }

        $cartItem->load('product.category');

        return response()->json([
            'status' => 'success',
            'message' => 'Item added to cart',
            'data' => $cartItem
        ]);
    }

    /**
     * Update cart item quantity
     */
    public function update(Request $request, $id)
    {
        $validated = $request->validate([
            'quantity' => 'required|integer|min:1',
        ]);

        $cartItem = CartItem::where('id', $id)
            ->where('user_id', $request->user()->id)
            ->first();

        if (!$cartItem) {
            return response()->json([
                'status' => 'error',
                'message' => 'Cart item not found'
            ], 404);
        }

        if ($cartItem->product->stock_quantity < $validated['quantity']) {
            return response()->json([
                'status' => 'error',
                'message' => 'Insufficient stock'
            ], 400);
        }

        $cartItem->update([
            'quantity' => $validated['quantity']
        ]);

        $cartItem->load('product.category');

        return response()->json([
            'status' => 'success',
            'message' => 'Cart item updated',
            'data' => $cartItem
        ]);
    }

    /**
     * Remove item from cart
     */
    public function remove(Request $request, $id)
    {
        $cartItem = CartItem::where('id', $id)
            ->where('user_id', $request->user()->id)
            ->first();

        if (!$cartItem) {
            return response()->json([
                'status' => 'error',
                'message' => 'Cart item not found'
            ], 404);
        }

        $cartItem->delete();

        return response()->json([
            'status' => 'success',
            'message' => 'Item removed from cart'
        ]);
    }

    /**
     * Clear all cart items
     */
    public function clear(Request $request)
    {
        CartItem::where('user_id', $request->user()->id)->delete();

        return response()->json([
            'status' => 'success',
            'message' => 'Cart cleared'
        ]);
    }
}
