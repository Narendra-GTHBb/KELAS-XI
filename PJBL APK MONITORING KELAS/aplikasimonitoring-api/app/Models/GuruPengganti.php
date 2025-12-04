<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class GuruPengganti extends Model
{
    use HasFactory;

    protected $fillable = [
        'jadwal_id',
        'guru_izin_id',
        'guru_asli_id',
        'guru_pengganti_id',
        'tanggal',
        'keterangan',
        'created_by',
    ];

    protected $casts = [
        'tanggal' => 'date',
    ];

    public function jadwal(): BelongsTo
    {
        return $this->belongsTo(Jadwal::class);
    }

    public function guruIzin(): BelongsTo
    {
        return $this->belongsTo(GuruIzin::class);
    }

    public function guruAsli(): BelongsTo
    {
        return $this->belongsTo(Guru::class, 'guru_asli_id');
    }

    public function guruPengganti(): BelongsTo
    {
        return $this->belongsTo(Guru::class, 'guru_pengganti_id');
    }

    public function createdBy(): BelongsTo
    {
        return $this->belongsTo(User::class, 'created_by');
    }

    // Scope untuk filter berdasarkan tanggal hari ini
    public function scopeHariIni($query)
    {
        return $query->whereDate('tanggal', now()->toDateString());
    }
}
