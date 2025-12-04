<?php

namespace App\Filament\Widgets;

use App\Models\Guru;
use App\Models\GuruMengajar;
use App\Models\Jadwal;
use App\Models\Kelas;
use App\Models\Mapel;
use App\Models\TahunAjaran;
use Filament\Widgets\StatsOverviewWidget as BaseWidget;
use Filament\Widgets\StatsOverviewWidget\Stat;

class StatsOverview extends BaseWidget
{
    protected static ?int $sort = 1;
    
    protected function getStats(): array
    {
        $totalGuru = Guru::count();
        $totalMapel = Mapel::count();
        $totalKelas = Kelas::count();
        $totalJadwal = Jadwal::count();
        $totalTahunAjaran = TahunAjaran::count();
        
        // Statistik kehadiran
        $totalMasuk = GuruMengajar::where('status', 'Masuk')->count();
        $totalTidakMasuk = GuruMengajar::where('status', 'Tidak Masuk')->count();
        $totalKehadiran = $totalMasuk + $totalTidakMasuk;
        
        // Persentase kehadiran
        $persentaseMasuk = $totalKehadiran > 0 
            ? round(($totalMasuk / $totalKehadiran) * 100, 1) 
            : 0;
        
        return [
            Stat::make('Total Guru', $totalGuru)
                ->description('Jumlah guru terdaftar')
                ->descriptionIcon('heroicon-m-user-group')
                ->color('primary')
                ->chart([7, 3, 4, 5, 6, 3, 5]),
                
            Stat::make('Total Mapel', $totalMapel)
                ->description('Mata pelajaran aktif')
                ->descriptionIcon('heroicon-m-book-open')
                ->color('success')
                ->chart([3, 5, 4, 6, 7, 4, 6]),
                
            Stat::make('Total Kelas', $totalKelas)
                ->description('Kelas yang terdaftar')
                ->descriptionIcon('heroicon-m-academic-cap')
                ->color('warning')
                ->chart([4, 6, 5, 7, 3, 4, 5]),
                
            Stat::make('Total Jadwal', $totalJadwal)
                ->description('Jadwal pelajaran')
                ->descriptionIcon('heroicon-m-calendar-days')
                ->color('info')
                ->chart([5, 4, 6, 3, 7, 5, 4]),
        ];
    }
}
