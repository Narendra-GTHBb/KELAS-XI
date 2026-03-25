<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Notification extends Model
{
    protected $fillable = [
        'user_id', 'title', 'body', 'type',
        'reference_id', 'reference_type', 'read_at',
    ];

    protected $casts = [
        'read_at' => 'datetime',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public static function send(int $userId, string $title, string $body, string $type = 'info', int $refId = null, string $refType = null): static
    {
        return static::create([
            'user_id'        => $userId,
            'title'          => $title,
            'body'           => $body,
            'type'           => $type,
            'reference_id'   => $refId,
            'reference_type' => $refType,
        ]);
    }
}
