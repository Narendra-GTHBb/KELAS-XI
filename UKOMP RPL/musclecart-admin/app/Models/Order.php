<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\SoftDeletes;

// Models used in transitionTo
// (autoloaded via same namespace)

class Order extends Model
{
    use SoftDeletes;

    const STATUS_PENDING    = 'pending';
    const STATUS_PAID       = 'paid';
    const STATUS_PROCESSING = 'processing';
    const STATUS_SHIPPED    = 'shipped';
    const STATUS_DELIVERED  = 'delivered';
    const STATUS_COMPLETED  = 'completed';
    const STATUS_CANCELLED  = 'cancelled';

    const ALLOWED_TRANSITIONS = [
        self::STATUS_PENDING    => [self::STATUS_PROCESSING, self::STATUS_CANCELLED],
        self::STATUS_PAID       => [self::STATUS_PROCESSING, self::STATUS_CANCELLED],
        self::STATUS_PROCESSING => [self::STATUS_SHIPPED, self::STATUS_CANCELLED],
        self::STATUS_SHIPPED    => [self::STATUS_DELIVERED],
        self::STATUS_DELIVERED  => [self::STATUS_COMPLETED],
        self::STATUS_COMPLETED  => [],
        self::STATUS_CANCELLED  => [],
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
        'voucher_code',
        'discount_amount',
        'points_earned',
        'points_used',
        'final_price',
        'tracking_number',
        'courier',
        'shipped_at',
        'delivered_at',
        'paid_at',
        'completed_at',
    ];

    protected $casts = [
        'total_amount' => 'decimal:2',
        'tax_amount' => 'decimal:2',
        'shipping_amount' => 'decimal:2',
        'discount_amount' => 'decimal:2',
        'final_price' => 'decimal:2',
        'points_earned' => 'integer',
        'points_used' => 'integer',
        'shipping_address' => 'array',
        'billing_address' => 'array',
        'shipped_at' => 'datetime',
        'delivered_at' => 'datetime',
        'paid_at' => 'datetime',
        'completed_at' => 'datetime',
    ];

    public function canTransitionTo(string $newStatus): bool
    {
        return in_array($newStatus, self::ALLOWED_TRANSITIONS[$this->status] ?? []);
    }

    public function transitionTo(string $newStatus, ?string $note = null, $changedBy = null, string $role = 'admin'): bool
    {
        if (!$this->canTransitionTo($newStatus)) {
            return false;
        }

        $previousStatus = $this->status;
        $updates = ['status' => $newStatus];

        if ($newStatus === self::STATUS_PAID && !$this->paid_at) {
            $updates['paid_at'] = now();
            $updates['payment_status'] = 'paid';
        }
        if ($newStatus === self::STATUS_SHIPPED && !$this->shipped_at) {
            $updates['shipped_at'] = now();
        }
        if ($newStatus === self::STATUS_DELIVERED && !$this->delivered_at) {
            $updates['delivered_at'] = now();
        }
        if ($newStatus === self::STATUS_COMPLETED && !$this->completed_at) {
            $updates['completed_at'] = now();
        }

        $this->update($updates);

        $this->statusHistories()->create([
            'status'           => $newStatus,
            'previous_status'  => $previousStatus,
            'note'             => $note,
            'changed_by'       => $changedBy,
            'changed_by_role'  => $role,
        ]);

        // Award reward points when order is completed (guard against double-awarding)
        if ($newStatus === self::STATUS_COMPLETED) {
            $earnedPoints = (int) $this->points_earned;
            if ($earnedPoints > 0) {
                $alreadyAwarded = PointsHistory::where('order_id', $this->id)
                    ->where('type', 'earn')
                    ->exists();
                if (!$alreadyAwarded) {
                    $this->user()->increment('points', $earnedPoints);
                    PointsHistory::create([
                        'user_id'     => $this->user_id,
                        'order_id'    => $this->id,
                        'points'      => $earnedPoints,
                        'type'        => 'earn',
                        'description' => "Poin reward dari pesanan #{$this->order_number}",
                    ]);
                    Notification::send(
                        $this->user_id,
                        'Poin Reward Diterima',
                        "Selamat! Kamu mendapat {$earnedPoints} poin dari pesanan #{$this->order_number}.",
                        'points',
                        $this->id,
                        'order'
                    );
                }
            }
        }

        // Send in-app notification
        $messages = [
            self::STATUS_PROCESSING => ['Pesanan Diproses', "Pesanan #{$this->order_number} sedang diproses oleh penjual."],
            self::STATUS_SHIPPED    => ['Pesanan Dikirim', "Pesanan #{$this->order_number} sudah dikirim. Silakan lacak paketmu."],
            self::STATUS_DELIVERED  => ['Pesanan Tiba', "Pesanan #{$this->order_number} telah tiba. Terima kasih telah berbelanja!"],
            self::STATUS_COMPLETED  => ['Pesanan Selesai', "Pesanan #{$this->order_number} selesai. Jangan lupa beri ulasan produkmu!"],
            self::STATUS_CANCELLED  => ['Pesanan Dibatalkan', "Pesanan #{$this->order_number} telah dibatalkan."],
        ];
        if (isset($messages[$newStatus])) {
            [$title, $body] = $messages[$newStatus];
            Notification::send($this->user_id, $title, $body, 'order_update', $this->id, 'order');
        }

        return true;
    }

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
        return $this->hasMany(OrderStatusHistory::class)->orderBy('created_at', 'desc');
    }
}
