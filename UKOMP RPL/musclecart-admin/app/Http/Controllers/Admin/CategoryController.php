<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Category;
use App\Models\OrderItem;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Storage;
use Illuminate\Validation\ValidationException;

class CategoryController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index()
    {
        $categories = Category::withCount('products')
                             ->orderBy('created_at', 'desc')
                             ->paginate(15);
        
        // Calculate statistics for summary cards
        $totalCategories = Category::count();
        $activeCategories = Category::where('is_active', true)->count();
        $maxProductCount = Category::withCount('products')
                                  ->orderBy('products_count', 'desc')
                                  ->first()
                                  ->products_count ?? 0;
        
        return view('admin.categories.index', compact(
            'categories', 
            'totalCategories', 
            'activeCategories', 
            'maxProductCount'
        ));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        return view('admin.categories.create');
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        try {
            $validated = $request->validate([
                'name' => 'required|string|max:255|unique:categories,name',
                'description' => 'nullable|string',
                'is_active' => 'boolean',
            ]);

            $validated['is_active'] = $request->has('is_active') && $request->is_active ? true : false;

            $category = Category::create($validated);

            // Handle AJAX requests
            if ($request->ajax()) {
                return response()->json([
                    'success' => true,
                    'message' => 'Category created successfully.',
                    'category' => $category
                ]);
            }

            return redirect()->route('admin.categories.index')
                ->with('success', 'Category created successfully.');

        } catch (ValidationException $e) {
            if ($request->ajax()) {
                return response()->json([
                    'success' => false,
                    'errors' => $e->errors()
                ], 422);
            }
            throw $e;
        } catch (\Exception $e) {
            if ($request->ajax()) {
                return response()->json([
                    'success' => false,
                    'message' => 'An error occurred while creating the category.'
                ], 500);
            }
            
            return redirect()->back()
                ->withInput()
                ->with('error', 'An error occurred while creating the category.');
        }
    }

    /**
     * Display the specified resource.
     */
    public function show(Category $category)
    {
        $category->load('products');
        return view('admin.categories.show', compact('category'));
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Category $category)
    {
        return view('admin.categories.edit', compact('category'));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Category $category)
    {
        try {
            $validated = $request->validate([
                'name' => 'required|string|max:255|unique:categories,name,' . $category->id,
                'description' => 'nullable|string',
                'image' => 'nullable|image|mimes:jpeg,png,jpg,gif|max:2048',
                'is_active' => 'boolean',
            ]);

            if ($request->hasFile('image')) {
                if ($category->image) {
                    Storage::disk('public')->delete($category->image);
                }
                $validated['image'] = $request->file('image')->store('categories', 'public');
            }

            $validated['is_active'] = $request->has('is_active');

            $category->update($validated);

            // Handle AJAX requests
            if ($request->ajax() || $request->wantsJson()) {
                return response()->json([
                    'success' => true,
                    'message' => 'Category updated successfully.',
                    'category' => $category
                ]);
            }

            return redirect()->route('admin.categories.index')
                ->with('success', 'Category updated successfully.');
        } catch (\Illuminate\Validation\ValidationException $e) {
            if ($request->ajax() || $request->wantsJson()) {
                return response()->json([
                    'success' => false,
                    'errors' => $e->errors()
                ], 422);
            }
            throw $e;
        } catch (\Exception $e) {
            if ($request->ajax() || $request->wantsJson()) {
                return response()->json([
                    'success' => false,
                    'message' => 'An error occurred while updating the category.'
                ], 500);
            }
            
            return redirect()->back()
                ->withInput()
                ->with('error', 'An error occurred while updating the category.');
        }
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Request $request, Category $category)
    {
        DB::transaction(function () use ($category) {
            // Delete order items linked to products in this category
            $productIds = $category->products()->pluck('id');
            OrderItem::whereIn('product_id', $productIds)->delete();

            // Delete product images
            foreach ($category->products as $product) {
                if ($product->image_url) {
                    Storage::disk('public')->delete($product->image_url);
                }
            }

            // Delete products
            $category->products()->delete();

            // Delete category image
            if ($category->image) {
                Storage::disk('public')->delete($category->image);
            }

            $category->delete();
        });

        // Handle AJAX requests
        if ($request->wantsJson() || $request->ajax()) {
            return response()->json([
                'success' => true,
                'message' => 'Category and all its products deleted successfully.'
            ]);
        }

        return redirect()->route('admin.categories.index')
            ->with('success', 'Category and all its products deleted successfully.');
    }
    
    /**
     * API endpoint to get categories for modal
     */
    public function apiIndex()
    {
        $categories = Category::select('id', 'name')
            ->where('is_active', true)
            ->orderBy('name')
            ->get();
            
        return response()->json($categories);
    }
}
