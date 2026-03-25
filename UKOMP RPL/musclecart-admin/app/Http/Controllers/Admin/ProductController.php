<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\Category;
use App\Models\Product;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Storage;
use Intervention\Image\Laravel\Facades\Image;

class ProductController extends Controller
{
    /**
     * Display a listing of the resource.
     */
    public function index(Request $request)
    {
        $query = Product::with('category');

        // Search functionality
        if ($request->has('search')) {
            $search = $request->get('search');
            $query->where('name', 'like', "%{$search}%")
                  ->orWhere('description', 'like', "%{$search}%");
        }

        // Category filter
        if ($request->has('category_id') && $request->category_id) {
            $query->where('category_id', $request->category_id);
        }

        // Status filter
        if ($request->has('status')) {
            $is_active = $request->status === 'active';
            $query->where('is_active', $is_active);
        }

        $products = $query->orderBy('created_at', 'desc')->paginate(15);
        $categories = Category::where('is_active', true)->get();
        
        // Statistics
        $totalProducts = Product::count();
        $activeProducts = Product::where('is_active', true)->count();
        $outOfStock = Product::where('stock_quantity', 0)->count();
        $lowStock = Product::where('stock_quantity', '>', 0)
                           ->whereRaw('stock_quantity <= COALESCE(low_stock_threshold, 10)')
                           ->count();

        return view('admin.products.index', compact('products', 'categories', 'totalProducts', 'activeProducts', 'outOfStock', 'lowStock'));
    }

    /**
     * Show the form for creating a new resource.
     */
    public function create()
    {
        $categories = Category::where('is_active', true)->get();
        return view('admin.products.create', compact('categories'));
    }

    /**
     * Store a newly created resource in storage.
     */
    public function store(Request $request)
    {
        try {
            // Log incoming request for debugging
            \Log::info('Product Store Request', [
                'all_data' => $request->all(),
                'has_file' => $request->hasFile('image'),
            ]);

            $validated = $request->validate([
                'name' => 'required|string|max:255',
                'description' => 'required|string',
                'price' => 'required|numeric|min:0',
                'stock_quantity' => 'required|integer|min:0',
                'category_id' => 'required|exists:categories,id',
                'brand' => 'nullable|string|max:255',
                'weight' => 'nullable|numeric|min:0',
                'weight_unit' => 'nullable|in:gr,kg',
                'low_stock_threshold' => 'nullable|integer|min:0',
                'specifications' => 'nullable|string',
                'image' => 'nullable|image|mimes:jpeg,png,jpg,gif,webp|max:2048',
            ]);

            // Convert weight to kg if unit is gr
            if (isset($validated['weight']) && isset($validated['weight_unit'])) {
                if ($validated['weight_unit'] === 'gr') {
                    $validated['weight'] = $validated['weight'] / 1000;
                }
            }
            unset($validated['weight_unit']);

            // Handle image upload with compression for mobile optimization
            if ($request->hasFile('image')) {
                $file = $request->file('image');
                $filename = time() . '_' . uniqid() . '.jpg';
                $storagePath = storage_path('app/public/products');
                
                // Ensure directory exists
                if (!file_exists($storagePath)) {
                    mkdir($storagePath, 0755, true);
                }
                
                // Compress and resize image for mobile
                $image = imagecreatefromstring(file_get_contents($file->getRealPath()));
                if ($image) {
                    // Get original dimensions
                    $origWidth = imagesx($image);
                    $origHeight = imagesy($image);
                    
                    // Max dimensions for mobile (800x800)
                    $maxWidth = 800;
                    $maxHeight = 800;
                    
                    // Calculate new dimensions maintaining aspect ratio
                    $ratio = min($maxWidth / $origWidth, $maxHeight / $origHeight);
                    if ($ratio < 1) {
                        $newWidth = (int) ($origWidth * $ratio);
                        $newHeight = (int) ($origHeight * $ratio);
                    } else {
                        $newWidth = $origWidth;
                        $newHeight = $origHeight;
                    }
                    
                    // Create resized image
                    $resized = imagecreatetruecolor($newWidth, $newHeight);
                    
                    // Preserve transparency for PNG
                    imagealphablending($resized, false);
                    imagesavealpha($resized, true);
                    
                    imagecopyresampled($resized, $image, 0, 0, 0, 0, $newWidth, $newHeight, $origWidth, $origHeight);
                    
                    // Save as JPEG with 75% quality (good balance of quality vs size)
                    imagejpeg($resized, $storagePath . '/' . $filename, 75);
                    
                    imagedestroy($image);
                    imagedestroy($resized);
                    
                    \Log::info('Image compressed', [
                        'original_size' => $file->getSize(),
                        'original_dimensions' => "{$origWidth}x{$origHeight}",
                        'new_dimensions' => "{$newWidth}x{$newHeight}",
                        'filename' => $filename
                    ]);
                } else {
                    // Fallback: just store as-is if GD fails
                    $file->storeAs('products', $filename, 'public');
                }
                
                $validated['image_url'] = $filename;
            }
            unset($validated['image']);

            // Handle specifications as JSON
            if (isset($validated['specifications'])) {
                $validated['specifications'] = json_decode($validated['specifications'], true) ?? $validated['specifications'];
            }

            // Handle boolean fields
            $validated['is_featured'] = $request->input('is_featured', 0) == 1;
            $validated['is_active'] = $request->input('is_active', 0) == 1;

            \Log::info('Validated data before create', $validated);

            $product = Product::create($validated);

            \Log::info('Product created successfully', ['product_id' => $product->id]);

            // Handle AJAX requests
            if ($request->wantsJson() || $request->ajax()) {
                return response()->json([
                    'success' => true,
                    'message' => 'Product created successfully.',
                    'product' => $product
                ], 201);
            }

            return redirect()->route('admin.products.index')
                ->with('success', 'Product created successfully.');
        } catch (\Illuminate\Validation\ValidationException $e) {
            \Log::error('Validation failed', ['errors' => $e->errors()]);
            if ($request->wantsJson() || $request->ajax()) {
                return response()->json([
                    'success' => false,
                    'message' => 'Validation failed.',
                    'errors' => $e->errors()
                ], 422);
            }
            throw $e;
        } catch (\Exception $e) {
            \Log::error('Error creating product', [
                'message' => $e->getMessage(),
                'trace' => $e->getTraceAsString()
            ]);
            if ($request->wantsJson() || $request->ajax()) {
                return response()->json([
                    'success' => false,
                    'message' => 'An error occurred: ' . $e->getMessage()
                ], 500);
            }
            return redirect()->back()
                ->withInput()
                ->withErrors(['error' => 'An error occurred while creating the product.']);
        }
    }

    /**
     * Display the specified resource.
     */
    public function show(Product $product)
    {
        return view('admin.products.show', compact('product'));
    }

    /**
     * Show the form for editing the specified resource.
     */
    public function edit(Product $product)
    {
        $categories = Category::where('is_active', true)->get();
        return view('admin.products.edit', compact('product', 'categories'));
    }

    /**
     * Update the specified resource in storage.
     */
    public function update(Request $request, Product $product)
    {
        try {
            $validated = $request->validate([
                'name' => 'required|string|max:255',
                'description' => 'required|string',
                'price' => 'required|numeric|min:0',
                'stock_quantity' => 'required|integer|min:0',
                'category_id' => 'required|exists:categories,id',
                'brand' => 'nullable|string|max:255',
                'weight' => 'nullable|numeric|min:0',
                'weight_unit' => 'nullable|in:gr,kg',
                'low_stock_threshold' => 'nullable|integer|min:0',
                'specifications' => 'nullable|string',
                'image' => 'nullable|image|mimes:jpeg,png,jpg,gif,webp|max:2048',
            ]);

            // Convert weight to kg if unit is gr
            if (isset($validated['weight']) && isset($validated['weight_unit'])) {
                if ($validated['weight_unit'] === 'gr') {
                    $validated['weight'] = $validated['weight'] / 1000;
                }
            }
            unset($validated['weight_unit']);

            // Handle image upload with compression for mobile optimization
            if ($request->hasFile('image')) {
                if ($product->image_url) {
                    // Delete old file - handle both with and without products/ prefix
                    $oldFile = str_starts_with($product->image_url, 'products/') 
                        ? $product->image_url 
                        : 'products/' . $product->image_url;
                    Storage::disk('public')->delete($oldFile);
                }
                
                $file = $request->file('image');
                $filename = time() . '_' . uniqid() . '.jpg';
                $storagePath = storage_path('app/public/products');
                
                if (!file_exists($storagePath)) {
                    mkdir($storagePath, 0755, true);
                }
                
                // Compress and resize for mobile
                $image = imagecreatefromstring(file_get_contents($file->getRealPath()));
                if ($image) {
                    $origWidth = imagesx($image);
                    $origHeight = imagesy($image);
                    $maxWidth = 800;
                    $maxHeight = 800;
                    
                    $ratio = min($maxWidth / $origWidth, $maxHeight / $origHeight);
                    if ($ratio < 1) {
                        $newWidth = (int) ($origWidth * $ratio);
                        $newHeight = (int) ($origHeight * $ratio);
                    } else {
                        $newWidth = $origWidth;
                        $newHeight = $origHeight;
                    }
                    
                    $resized = imagecreatetruecolor($newWidth, $newHeight);
                    imagealphablending($resized, false);
                    imagesavealpha($resized, true);
                    imagecopyresampled($resized, $image, 0, 0, 0, 0, $newWidth, $newHeight, $origWidth, $origHeight);
                    imagejpeg($resized, $storagePath . '/' . $filename, 75);
                    imagedestroy($image);
                    imagedestroy($resized);
                } else {
                    $file->storeAs('products', $filename, 'public');
                }
                
                $validated['image_url'] = $filename;
            }
            unset($validated['image']);

            // Handle specifications as JSON
            if (isset($validated['specifications'])) {
                $validated['specifications'] = json_decode($validated['specifications'], true) ?? $validated['specifications'];
            }

            $validated['is_featured'] = $request->boolean('is_featured');
            $validated['is_active'] = $request->boolean('is_active');

            $product->update($validated);

            // Handle AJAX requests
            if ($request->wantsJson() || $request->ajax()) {
                return response()->json([
                    'success' => true,
                    'message' => 'Product updated successfully.',
                    'product' => $product->fresh()
                ]);
            }

            return redirect()->route('admin.products.index')
                ->with('success', 'Product updated successfully.');
        } catch (\Illuminate\Validation\ValidationException $e) {
            if ($request->wantsJson() || $request->ajax()) {
                return response()->json([
                    'success' => false,
                    'errors' => $e->errors()
                ], 422);
            }
            throw $e;
        } catch (\Exception $e) {
            \Log::error('Error updating product', [
                'error' => $e->getMessage(),
                'trace' => $e->getTraceAsString(),
                'product_id' => $product->id
            ]);
            
            if ($request->wantsJson() || $request->ajax()) {
                return response()->json([
                    'success' => false,
                    'message' => 'An error occurred while updating the product: ' . $e->getMessage()
                ], 500);
            }
            
            return redirect()->back()
                ->withInput()
                ->with('error', 'An error occurred while updating the product.');
        }
    }

    /**
     * Remove the specified resource from storage.
     */
    public function destroy(Request $request, Product $product)
    {
        DB::transaction(function () use ($product) {
            // Delete related order items first
            $product->orderItems()->delete();

            // Delete image if exists
            if ($product->image_url) {
                Storage::disk('public')->delete($product->image_url);
            }

            $product->delete();
        });

        // Handle AJAX requests
        if ($request->wantsJson() || $request->ajax()) {
            return response()->json([
                'success' => true,
                'message' => 'Product deleted successfully.'
            ]);
        }

        return redirect()->route('admin.products.index')
            ->with('success', 'Product deleted successfully.');
    }
}
