<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Favorite;
use App\Models\Product;
use Illuminate\Http\JsonResponse;
use Illuminate\Http\Request;

class FavoriteController extends Controller
{
    /**
     * Get user's favorite products.
     */
    public function index(Request $request): JsonResponse
    {
        $user = $request->user();
        
        $favorites = Favorite::with('product.category')
            ->where('user_id', $user->id)
            ->orderBy('created_at', 'desc')
            ->get();

        $products = $favorites->map(function ($favorite) {
            return $favorite->product;
        });

        return response()->json([
            'status' => 'success',
            'data' => $products
        ]);
    }

    /**
     * Toggle favorite (add/remove).
     */
    public function toggle(Request $request, $productId): JsonResponse
    {
        $user = $request->user();
        
        if (!$user) {
            \Log::error('Favorite toggle failed: User not authenticated');
            return response()->json([
                'status' => 'error',
                'message' => 'User not authenticated'
            ], 401);
        }
        
        $product = Product::find($productId);

        if (!$product) {
            return response()->json([
                'status' => 'error',
                'message' => 'Product not found'
            ], 404);
        }

        $favorite = Favorite::where('user_id', $user->id)
            ->where('product_id', $productId)
            ->first();

        if ($favorite) {
            // Remove from favorites
            $favorite->delete();

            return response()->json([
                'status' => 'success',
                'message' => 'Removed from favorites',
                'data' => ['is_favorite' => false]
            ]);
        }

        // Add to favorites
        Favorite::create([
            'user_id' => $user->id,
            'product_id' => $productId,
        ]);

        return response()->json([
            'status' => 'success',
            'message' => 'Added to favorites',
            'data' => ['is_favorite' => true]
        ]);
    }

    /**
     * Check if product is favorited.
     */
    public function check(Request $request, $productId): JsonResponse
    {
        $user = $request->user();
        
        $isFavorite = Favorite::where('user_id', $user->id)
            ->where('product_id', $productId)
            ->exists();

        return response()->json([
            'status' => 'success',
            'data' => ['is_favorite' => $isFavorite]
        ]);
    }
}
