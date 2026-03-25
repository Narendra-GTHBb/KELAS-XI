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
}
