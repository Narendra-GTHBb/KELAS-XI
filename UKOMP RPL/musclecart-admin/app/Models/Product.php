<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;

class Product extends Model
{
    protected $fillable = [
        'name',
        'description',
        'price',
        'stock_quantity',
        'low_stock_threshold',
        'image_url',
        'gallery',
        'category_id',
        'brand',
        'weight',
        'specifications',
        'is_featured',
        'is_active',
    ];

    protected $appends = [
        'stock',
        'full_image_url',
        'avg_rating',
        'total_reviews',
    ];

    protected $casts = [
        'price' => 'decimal:2',
        'weight' => 'decimal:3',  // 3 decimal places for better precision (up to 999.999 kg or 0.001 kg = 1 gram)
        'is_active' => 'boolean',
        'is_featured' => 'boolean',
        'gallery' => 'array',
    ];

    // Accessor to get full image URL with APP_URL
    public function getFullImageUrlAttribute()
    {
        if (!$this->image_url) {
            return null;
        }

        // If already full URL, return as-is
        if (str_starts_with($this->image_url, 'http://') || str_starts_with($this->image_url, 'https://')) {
            return $this->image_url;
        }

        // Strip 'products/' prefix if present (fix for old bad data)
        $filename = str_starts_with($this->image_url, 'products/') 
            ? substr($this->image_url, 9) 
            : $this->image_url;

        // Generate full URL: APP_URL/storage/products/filename
        return config('app.url') . '/storage/products/' . $filename;
    }

    // Accessor to map stock_quantity to stock for backward compatibility
    public function getStockAttribute()
    {
        return $this->stock_quantity;
    }

    public function category(): BelongsTo
    {
        return $this->belongsTo(Category::class);
    }

    public function cartItems(): HasMany
    {
        return $this->hasMany(CartItem::class);
    }

    public function orderItems(): HasMany
    {
        return $this->hasMany(OrderItem::class);
    }

    public function reviews(): HasMany
    {
        return $this->hasMany(Review::class);
    }

    public function getAvgRatingAttribute(): float
    {
        return round($this->reviews()->avg('rating') ?? 0, 1);
    }

    public function getTotalReviewsAttribute(): int
    {
        return $this->reviews()->count();
    }
}
