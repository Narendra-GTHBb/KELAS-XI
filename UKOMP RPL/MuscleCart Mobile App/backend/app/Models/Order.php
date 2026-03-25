<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\SoftDeletes;

class Order extends Model
{
    use SoftDeletes;

    // Status flow: pending → paid → processing → shipped → delivered → completed
    const STATUS_PENDING    = 'pending';
    const STATUS_PAID       = 'paid';
    const STATUS_PROCESSING = 'processing';
    const STATUS_SHIPPED    = 'shipped';
    const STATUS_DELIVERED  = 'delivered';
    const STATUS_COMPLETED  = 'completed';
    const STATUS_CANCELLED  = 'cancelled';

    const ALLOWED_TRANSITIONS = [
        'pending'    => ['processing', 'cancelled'],
        'paid'       => ['processing', 'cancelled'],
        'processing' => ['shipped', 'cancelled'],
        'shipped'    => ['delivered'],
        'delivered'  => ['completed'],
        'completed'  => [],
        'cancelled'  => [],
    ];

    protected $fillable = [
        'order_number',
        'user_id',
        'total_amount',
        'tax_amount',
        'shipping_amount',
        'status',
        'payment_status',
        'payment_method',
        'shipping_address',
        'billing_address',
        'notes',
        'tracking_number',
        'courier',
        'shipped_at',
        'delivered_at',
        'paid_at',
        'completed_at',
    ];

    protected $casts = [
        'total_amount'     => 'decimal:2',
        'tax_amount'       => 'decimal:2',
        'shipping_amount'  => 'decimal:2',
        'shipping_address' => 'array',
        'billing_address'  => 'array',
        'shipped_at'       => 'datetime',
        'delivered_at'     => 'datetime',
        'paid_at'          => 'datetime',
        'completed_at'     => 'datetime',
    ];

    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    public function orderItems(): HasMany
    {
        return $this->hasMany(OrderItem::class);
    }

    public function statusHistories(): HasMany
    {
        return $this->hasMany(OrderStatusHistory::class)->orderBy('created_at', 'asc');
    }

    public function canTransitionTo(string $newStatus): bool
    {
        return in_array($newStatus, self::ALLOWED_TRANSITIONS[$this->status] ?? []);
    }

    public function transitionTo(string $newStatus, ?string $note = null, ?int $changedBy = null, string $role = 'admin'): bool
    {
        if (!$this->canTransitionTo($newStatus)) {
            return false;
        }

        OrderStatusHistory::record($this, $newStatus, $note, $changedBy, $role);

        $timestamps = [];
        if ($newStatus === 'paid')      $timestamps['paid_at']      = now();
        if ($newStatus === 'shipped')   $timestamps['shipped_at']   = now();
        if ($newStatus === 'delivered') $timestamps['delivered_at'] = now();
        if ($newStatus === 'completed') $timestamps['completed_at'] = now();

        $this->update(array_merge(['status' => $newStatus], $timestamps));
        return true;
    }

    protected static function boot()
    {
        parent::boot();

        static::creating(function ($order) {
            if (empty($order->order_number)) {
                $order->order_number = 'ORD-' . strtoupper(uniqid());
            }
        });

        static::created(function ($order) {
            OrderStatusHistory::record($order, 'pending', 'Order placed', $order->user_id, 'user');
        });
    }
}

