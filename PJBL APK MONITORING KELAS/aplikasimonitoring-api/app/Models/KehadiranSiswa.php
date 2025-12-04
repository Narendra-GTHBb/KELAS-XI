<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class KehadiranSiswa extends Model
{
    use HasFactory;

    protected $fillable = [
        'jadwal_id',
        'kelas_id',
        'tanggal',
        'jumlah_hadir',
        'jumlah_sakit',
        'jumlah_izin',
        'jumlah_alpha',
        'keterangan',
        'reported_by',
    ];

    protected $casts = [
        'tanggal' => 'date',
    ];

    public function jadwal(): BelongsTo
    {
        return $this->belongsTo(Jadwal::class);
    }

    public function kelas(): BelongsTo
    {
        return $this->belongsTo(Kelas::class);
    }

    public function reportedBy(): BelongsTo
    {
        return $this->belongsTo(User::class, 'reported_by');
    }

    // Accessor untuk total siswa
    public function getTotalSiswaAttribute(): int
    {
        return $this->jumlah_hadir + $this->jumlah_sakit + $this->jumlah_izin + $this->jumlah_alpha;
    }

    // Accessor untuk persentase kehadiran
    public function getPersentaseHadirAttribute(): float
    {
        $total = $this->total_siswa;
        return $total > 0 ? round(($this->jumlah_hadir / $total) * 100, 2) : 0;
    }

    // Scope untuk filter berdasarkan tanggal hari ini
    public function scopeHariIni($query)
    {
        return $query->whereDate('tanggal', now()->toDateString());
    }
}
