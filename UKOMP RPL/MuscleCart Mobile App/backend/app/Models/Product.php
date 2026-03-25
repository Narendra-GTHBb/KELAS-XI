<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;

class Product extends Model
{
    // Disable Laravel's default timestamps since database uses BIGINT
    public $timestamps = false;
    
    protected $fillable = [
        'name',
        'description',
        'price',
        'stock_quantity',
        'image_url',
        'gallery',
        'category_id',
        'brand',
        'weight',
        'specifications',
        'is_featured',
        'is_active'
    ];

    protected $appends = ['full_image_url'];

    // Accessor to map stock_quantity to stock for backward compatibility
    public function getStockAttribute()
    {
        return $this->stock_quantity;
    }

    // Accessor to map image_url to image for backward compatibility
    public function getImageAttribute()
    {
        return $this->image_url;
    }
    
    // Return full URL for image
    public function getFullImageUrlAttribute()
    {
        if (!$this->image_url) {
            return null;
        }
        
        // If it's already a full URL, return as is
        if (str_starts_with($this->image_url, 'http://') || str_starts_with($this->image_url, 'https://')) {
            return $this->image_url;
        }
        
        // Strip 'products/' prefix if present (fix for old bad data)
        $filename = str_starts_with($this->image_url, 'products/') 
            ? substr($this->image_url, 9) 
            : $this->image_url;
        
        // Otherwise, prepend the storage URL with products folder
        // Use config('app.url') explicitly so it always returns the correct APP_URL
        // instead of the request host (which may differ when behind Apache proxy)
        $appUrl = rtrim(config('app.url'), '/');
        return $appUrl . '/storage/products/' . $filename;
    }
    
    // Convert BigInt timestamps to dates
    public function getCreatedAtAttribute($value)
    {
        return $value ? \Carbon\Carbon::createFromTimestampMs($value) : null;
    }
    
    public function getUpdatedAtAttribute($value)
    {
        return $value ? \Carbon\Carbon::createFromTimestampMs($value) : null;
    }

    protected $casts = [
        'price' => 'decimal:2',
        'weight' => 'decimal:2',
        'gallery' => 'array',
        'specifications' => 'array',
        'is_featured' => 'boolean',
        'is_active' => 'boolean',
    ];

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
}
