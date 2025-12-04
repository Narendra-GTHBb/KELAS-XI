<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class GuruMengajar extends Model
{
    use HasFactory;

    protected $fillable = [
        'jadwal_id',
        'keterangan',
        'status',
        'guru_pengganti_id',
        'tanggal_mulai_izin',
        'tanggal_selesai_izin',
    ];
    
    protected $casts = [
        'tanggal_mulai_izin' => 'date',
        'tanggal_selesai_izin' => 'date',
    ];
    
    // Accessor untuk menghitung durasi izin dalam hari
    public function getDurasiIzinAttribute()
    {
        if ($this->tanggal_mulai_izin && $this->tanggal_selesai_izin) {
            return $this->tanggal_mulai_izin->diffInDays($this->tanggal_selesai_izin) + 1;
        }
        return null;
    }

    public function jadwal()
    {
        return $this->belongsTo(Jadwal::class);
    }
    
    // Relasi ke guru pengganti
    public function guruPengganti()
    {
        return $this->belongsTo(Guru::class, 'guru_pengganti_id');
    }
    
    // Accessor untuk mendapatkan guru melalui jadwal
    public function getGuruAttribute()
    {
        return $this->jadwal?->guru;
    }
    
    // Accessor untuk mendapatkan mapel melalui jadwal
    public function getMapelAttribute()
    {
        return $this->jadwal?->mapel;
    }
    
    // Accessor untuk mendapatkan kelas melalui jadwal
    public function getKelasAttribute()
    {
        return $this->jadwal?->kelas;
    }
}
