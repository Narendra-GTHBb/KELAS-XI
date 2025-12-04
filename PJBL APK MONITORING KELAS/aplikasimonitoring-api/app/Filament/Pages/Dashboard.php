<?php

namespace App\Filament\Pages;

use App\Models\Guru;
use App\Models\GuruMengajar;
use App\Models\Jadwal;
use App\Models\Kelas;
use App\Models\Mapel;
use Carbon\Carbon;
use Filament\Pages\Page;

class Dashboard extends Page
{
    protected string $view = 'filament.pages.dashboard';
    
    protected static ?int $navigationSort = -2;
    
    public static function getNavigationLabel(): string
    {
        return 'Dashboard';
    }
    
    public function getTitle(): string
    {
        return 'Dashboard Admin Panel';
    }
    
    public static function getNavigationIcon(): ?string
    {
        return 'heroicon-o-home';
    }
    
    public function getViewData(): array
    {
        // Data Master
        $totalGuru = Guru::count();
        $totalMapel = Mapel::count();
        $totalKelas = Kelas::count();
        $totalJadwal = Jadwal::count();
        
        // Statistik Kehadiran (All Time)
        $totalMasuk = GuruMengajar::where('status', 'Masuk')->count();
        $totalTidakMasuk = GuruMengajar::where('status', 'Tidak Masuk')->count();
        $totalKehadiran = $totalMasuk + $totalTidakMasuk;
        
        $persentaseMasuk = $totalKehadiran > 0 
            ? round(($totalMasuk / $totalKehadiran) * 100, 1) 
            : 0;
        
        // Aktivitas Terbaru (5 terakhir)
        $recentActivities = GuruMengajar::with(['jadwal.guru', 'jadwal.mapel', 'jadwal.kelas'])
            ->latest()
            ->take(5)
            ->get();
        
        return [
            'totalGuru' => $totalGuru,
            'totalMapel' => $totalMapel,
            'totalKelas' => $totalKelas,
            'totalJadwal' => $totalJadwal,
            'totalMasuk' => $totalMasuk,
            'totalTidakMasuk' => $totalTidakMasuk,
            'totalKehadiran' => $totalKehadiran,
            'persentaseMasuk' => $persentaseMasuk,
            'recentActivities' => $recentActivities,
            'tanggalHariIni' => Carbon::now()->locale('id')->isoFormat('dddd, D MMMM Y'),
        ];
    }
}
