<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class OrderStatusHistory extends Model
{
    protected $fillable = [
        'order_id',
        'status',
        'previous_status',
        'note',
        'changed_by',
        'changed_by_role',
    ];

    public function order(): BelongsTo
    {
        return $this->belongsTo(Order::class);
    }

    public static function record(Order $order, string $newStatus, ?string $note = null, ?int $changedBy = null, string $role = 'admin'): void
    {
        self::create([
            'order_id'        => $order->id,
            'status'          => $newStatus,
            'previous_status' => $order->status,
            'note'            => $note,
            'changed_by'      => $changedBy,
            'changed_by_role' => $role,
        ]);
    }
}
