<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Category;
use App\Models\Product;
use Illuminate\Http\Request;

class CategoryController extends Controller
{
    /**
     * Display a listing of active categories
     */
    public function index(Request $request)
    {
        $query = Category::where('is_active', true);

        // Search by name
        if ($request->has('search')) {
            $search = $request->get('search');
            $query->where('name', 'like', "%{$search}%");
        }

        // Include product count
        $categories = $query->withCount(['products' => function($q) {
            $q->where('is_active', true);
        }])->orderBy('name')->get();

        return response()->json([
            'status' => 'success',
            'data' => $categories->map(function($category) {
                return [
                    'id' => $category->id,
                    'name' => $category->name,
                    'description' => $category->description,
                    'image' => $category->image,
                    'is_active' => $category->is_active,
                    'products_count' => $category->products_count,
                ];
            })
        ]);
    }

    /**
     * Display products in a specific category
     */
    public function products($id, Request $request)
    {
        $category = Category::where('is_active', true)->find($id);

        if (!$category) {
            return response()->json([
                'status' => 'error',
                'message' => 'Category not found'
            ], 404);
        }

        $query = Product::with('category')
            ->where('category_id', $category->id)
            ->where('is_active', true);

        // Search by name or description
        if ($request->has('search')) {
            $search = $request->get('search');
            $query->where(function($q) use ($search) {
                $q->where('name', 'like', "%{$search}%")
                  ->orWhere('description', 'like', "%{$search}%");
            });
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
            'data' => [
                'category' => [
                    'id' => $category->id,
                    'name' => $category->name,
                    'description' => $category->description,
                    'image' => $category->image,
                ],
                'products' => $products->items(),
                'pagination' => [
                    'current_page' => $products->currentPage(),
                    'last_page' => $products->lastPage(),
                    'per_page' => $products->perPage(),
                    'total' => $products->total(),
                ]
            ]
        ]);
    }
}
