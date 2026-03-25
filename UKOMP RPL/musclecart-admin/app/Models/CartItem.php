<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class CartItem extends Model
{
    // Disable Laravel's default timestamps since database uses BIGINT
    public $timestamps = false;
    
    protected $fillable = [
        'user_id',
        'product_id',
        'quantity',
        'price',
    ];

    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    public function product(): BelongsTo
    {
        return $this->belongsTo(Product::class);
    }

    // Calculate total using product price
    public function getTotalAttribute(): float
    {
        return $this->quantity * ($this->product ? $this->product->price : 0);
    }
    
    // Convert BigInt timestamps to dates
    public function getAddedAtAttribute($value)
    {
        return $value ? \Carbon\Carbon::createFromTimestampMs($value) : null;
    }
    
    public function getUpdatedAtAttribute($value)
    {
        return $value ? \Carbon\Carbon::createFromTimestampMs($value) : null;
    }
}
