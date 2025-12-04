<?php

namespace App\Filament\Widgets;

use App\Models\GuruMengajar;
use Filament\Widgets\StatsOverviewWidget as BaseWidget;
use Filament\Widgets\StatsOverviewWidget\Stat;

class KehadiranStats extends BaseWidget
{
    protected static ?int $sort = 2;
    
    protected function getStats(): array
    {
        // Statistik kehadiran
        $totalMasuk = GuruMengajar::where('status', 'Masuk')->count();
        $totalTidakMasuk = GuruMengajar::where('status', 'Tidak Masuk')->count();
        $totalKehadiran = $totalMasuk + $totalTidakMasuk;
        
        // Persentase kehadiran
        $persentaseMasuk = $totalKehadiran > 0 
            ? round(($totalMasuk / $totalKehadiran) * 100, 1) 
            : 0;
            
        $persentaseTidakMasuk = $totalKehadiran > 0 
            ? round(($totalTidakMasuk / $totalKehadiran) * 100, 1) 
            : 0;
        
        // Data hari ini
        $today = now()->format('Y-m-d');
        $masukHariIni = GuruMengajar::where('status', 'Masuk')
            ->whereDate('created_at', $today)
            ->count();
        $tidakMasukHariIni = GuruMengajar::where('status', 'Tidak Masuk')
            ->whereDate('created_at', $today)
            ->count();
        
        return [
            Stat::make('Guru Masuk', $totalMasuk)
                ->description($persentaseMasuk . '% dari total kehadiran')
                ->descriptionIcon('heroicon-m-check-circle')
                ->color('success')
                ->chart([4, 5, 6, 7, 5, 8, 6]),
                
            Stat::make('Guru Tidak Masuk', $totalTidakMasuk)
                ->description($persentaseTidakMasuk . '% dari total kehadiran')
                ->descriptionIcon('heroicon-m-x-circle')
                ->color('danger')
                ->chart([2, 3, 1, 2, 4, 1, 3]),
                
            Stat::make('Masuk Hari Ini', $masukHariIni)
                ->description('Guru masuk tanggal ' . now()->format('d/m/Y'))
                ->descriptionIcon('heroicon-m-arrow-trending-up')
                ->color('primary'),
                
            Stat::make('Tidak Masuk Hari Ini', $tidakMasukHariIni)
                ->description('Guru tidak masuk tanggal ' . now()->format('d/m/Y'))
                ->descriptionIcon('heroicon-m-arrow-trending-down')
                ->color('warning'),
        ];
    }
}
