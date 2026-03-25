<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Order;
use App\Models\Review;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class ReviewController extends Controller
{
    /**
     * Ambil semua review untuk sebuah produk
     * GET /api/v1/products/{productId}/reviews
     */
    public function index($productId)
    {
        $reviews = Review::with('user:id,name')
            ->where('product_id', $productId)
            ->orderByDesc('created_at')
            ->get()
            ->map(fn($r) => [
                'id'         => $r->id,
                'rating'     => $r->rating,
                'comment'    => $r->comment,
                'user_name'  => $r->user->name ?? 'Pengguna',
                'created_at' => $r->created_at->toIso8601String(),
            ]);

        $avgRating = $reviews->avg('rating') ?? 0;
        $totalReviews = $reviews->count();

        return response()->json([
            'status'       => 'success',
            'avg_rating'   => round($avgRating, 1),
            'total_reviews' => $totalReviews,
            'data'         => $reviews,
        ]);
    }

    /**
     * Submit review baru
     * POST /api/v1/reviews
     */
    public function store(Request $request)
    {
        $request->validate([
            'product_id' => 'required|integer|exists:products,id',
            'order_id'   => 'required|integer|exists:orders,id',
            'rating'     => 'required|integer|min:1|max:5',
            'comment'    => 'nullable|string|max:1000',
        ]);

        $user = $request->user();

        // Pastikan order ini milik user dan statusnya completed/delivered
        $order = Order::where('id', $request->order_id)
            ->where('user_id', $user->id)
            ->whereIn('status', ['delivered', 'completed'])
            ->first();

        if (!$order) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Order tidak ditemukan atau belum selesai',
            ], 403);
        }

        // Pastikan produk ada di dalam order
        $isInOrder = $order->orderItems()
            ->where('product_id', $request->product_id)
            ->exists();

        if (!$isInOrder) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Produk tidak ada dalam pesanan ini',
            ], 403);
        }

        // Cek apakah sudah pernah review
        $exists = Review::where('user_id', $user->id)
            ->where('product_id', $request->product_id)
            ->where('order_id', $request->order_id)
            ->exists();

        if ($exists) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Kamu sudah memberikan ulasan untuk produk ini',
            ], 409);
        }

        $review = Review::create([
            'user_id'    => $user->id,
            'product_id' => $request->product_id,
            'order_id'   => $request->order_id,
            'rating'     => $request->rating,
            'comment'    => $request->comment,
        ]);

        return response()->json([
            'status'  => 'success',
            'message' => 'Ulasan berhasil dikirim',
            'data'    => [
                'id'         => $review->id,
                'rating'     => $review->rating,
                'comment'    => $review->comment,
                'created_at' => $review->created_at->toIso8601String(),
            ],
        ], 201);
    }

    /**
     * Cek apakah user sudah review produk ini dari order tertentu
     * GET /api/v1/reviews/check?product_id=X&order_id=Y
     */
    public function check(Request $request)
    {
        $user = $request->user();

        $exists = Review::where('user_id', $user->id)
            ->where('product_id', $request->product_id)
            ->where('order_id', $request->order_id)
            ->exists();

        return response()->json([
            'status'       => 'success',
            'has_reviewed' => $exists,
        ]);
    }
}
