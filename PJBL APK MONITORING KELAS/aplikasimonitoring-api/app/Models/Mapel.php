<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Mapel extends Model
{
    use HasFactory;

    protected $fillable = [
        'kode_mapel',
        'mapel',
    ];

    /**
     * Boot method untuk auto-generate kode_mapel
     */
    protected static function boot()
    {
        parent::boot();

        static::creating(function ($mapel) {
            if (empty($mapel->kode_mapel)) {
                $mapel->kode_mapel = self::generateKodeMapel();
            }
        });
    }

    /**
     * Generate kode mapel otomatis berdasarkan urutan
     * Format: M001, M002, M003, dst.
     */
    public static function generateKodeMapel(): string
    {
        // Cari kode mapel terakhir dengan format M + angka
        $lastMapel = self::where('kode_mapel', 'LIKE', 'M%')
            ->whereRaw("kode_mapel REGEXP '^M[0-9]+$'")
            ->orderByRaw('CAST(SUBSTRING(kode_mapel, 2) AS UNSIGNED) DESC')
            ->first();
        
        if (!$lastMapel) {
            // Jika belum ada yang format M001, mulai dari jumlah data + 1
            $nextNumber = self::count() + 1;
            return 'M' . str_pad($nextNumber, 3, '0', STR_PAD_LEFT);
        }

        // Extract nomor dari kode terakhir
        if (preg_match('/M(\d+)/', $lastMapel->kode_mapel, $matches)) {
            $nextNumber = intval($matches[1]) + 1;
        } else {
            $nextNumber = self::count() + 1;
        }

        return 'M' . str_pad($nextNumber, 3, '0', STR_PAD_LEFT);
    }
}
