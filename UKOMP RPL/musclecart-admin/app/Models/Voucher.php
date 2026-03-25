<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Voucher extends Model
{
    protected $fillable = [
        'code', 'description', 'type', 'value',
        'min_purchase', 'max_discount', 'max_uses',
        'used_count', 'starts_at', 'expires_at', 'is_active',
    ];

    protected $casts = [
        'value'         => 'decimal:2',
        'min_purchase'  => 'decimal:2',
        'max_discount'  => 'decimal:2',
        'is_active'     => 'boolean',
        'starts_at'     => 'datetime',
        'expires_at'    => 'datetime',
    ];

    /**
     * Calculate discount amount for a given subtotal.
     */
    public function calculateDiscount(float $subtotal): float
    {
        if ($this->type === 'percentage') {
            $discount = $subtotal * ($this->value / 100);
            if ($this->max_discount !== null) {
                $discount = min($discount, (float) $this->max_discount);
            }
            return round($discount, 2);
        }
        // fixed
        return min((float) $this->value, $subtotal);
    }

    /**
     * Is the voucher currently usable?
     */
    public function isValid(): bool
    {
        if (!$this->is_active) return false;
        if ($this->max_uses !== null && $this->used_count >= $this->max_uses) return false;
        $now = now();
        if ($this->starts_at && $now->lt($this->starts_at)) return false;
        if ($this->expires_at && $now->gt($this->expires_at)) return false;
        return true;
    }
}
