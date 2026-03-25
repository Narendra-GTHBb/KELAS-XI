<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Product;
use Illuminate\Http\Request;

class ProductController extends Controller
{
    /**
     * Display a listing of products
     */
    public function index(Request $request)
    {
        $query = Product::with('category')
            ->where('is_active', true);

        // Search by name or description
        if ($request->has('search')) {
            $search = $request->get('search');
            $query->where(function($q) use ($search) {
                $q->where('name', 'like', "%{$search}%")
                  ->orWhere('description', 'like', "%{$search}%");
            });
        }

        // Filter by category
        if ($request->has('category_id')) {
            $query->where('category_id', $request->category_id);
        }

        // Filter featured products
        if ($request->has('featured')) {
            $query->where('is_featured', true);
        }

        // Sort options
        $sortBy = $request->get('sort_by', 'created_at');
        $sortOrder = $request->get('sort_order', 'desc');
        
        switch ($sortBy) {
            case 'price':
                $query->orderBy('price', $sortOrder);
                break;
            case 'name':
                $query->orderBy('name', $sortOrder);
                break;
            default:
                $query->orderBy('created_at', $sortOrder);
        }

        $products = $query->paginate(20);

        return response()->json([
            'status' => 'success',
            'data' => $products->items(),
            'pagination' => [
                'current_page' => $products->currentPage(),
                'last_page' => $products->lastPage(),
                'per_page' => $products->perPage(),
                'total' => $products->total(),
            ]
        ]);
    }

    /**
     * Display the specified product
     */
    public function show($id)
    {
        $product = Product::with('category')
            ->where('is_active', true)
            ->find($id);

        if (!$product) {
            return response()->json([
                'status' => 'error',
                'message' => 'Product not found'
            ], 404);
        }

        // Get related products from same category
        $relatedProducts = Product::with('category')
            ->where('category_id', $product->category_id)
            ->where('id', '!=', $product->id)
            ->where('is_active', true)
            ->limit(4)
            ->get();

        // Get latest 10 reviews
        $reviews = $product->reviews()
            ->with('user:id,name')
            ->orderByDesc('created_at')
            ->limit(10)
            ->get()
            ->map(fn($r) => [
                'id'        => $r->id,
                'rating'    => $r->rating,
                'comment'   => $r->comment,
                'user_name' => $r->user->name ?? 'Pengguna',
                'created_at' => $r->created_at->toIso8601String(),
            ]);

        return response()->json([
            'status' => 'success',
            'data' => [
                'product'          => $product,
                'related_products' => $relatedProducts,
                'reviews'          => $reviews,
            ]
        ]);
    }
}
