<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Guru extends Model
{
    use HasFactory;

    protected $fillable = [
        'kode_guru',
        'guru',
        'telepon',
    ];

    /**
     * Boot method untuk auto-generate kode_guru
     */
    protected static function boot()
    {
        parent::boot();

        static::creating(function ($guru) {
            if (empty($guru->kode_guru)) {
                $guru->kode_guru = self::generateKodeGuru();
            }
        });
    }

    /**
     * Generate kode guru otomatis berdasarkan urutan
     * Format: G001, G002, G003, dst.
     */
    public static function generateKodeGuru(): string
    {
        // Cari kode guru terakhir dengan format G + angka
        $lastGuru = self::where('kode_guru', 'LIKE', 'G%')
            ->orderByRaw('CAST(SUBSTRING(kode_guru, 2) AS UNSIGNED) DESC')
            ->first();
        
        if (!$lastGuru) {
            return 'G001';
        }

        // Extract nomor dari kode terakhir
        $lastKode = $lastGuru->kode_guru;
        
        if (preg_match('/G(\d+)/', $lastKode, $matches)) {
            $nextNumber = intval($matches[1]) + 1;
        } else {
            // Fallback: hitung total guru + 1
            $nextNumber = self::count() + 1;
        }

        return 'G' . str_pad($nextNumber, 3, '0', STR_PAD_LEFT);
    }
}
