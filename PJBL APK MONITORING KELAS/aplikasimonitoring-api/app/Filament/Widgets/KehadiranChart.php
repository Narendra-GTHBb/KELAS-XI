<?php

namespace App\Filament\Widgets;

use App\Models\GuruMengajar;
use Filament\Widgets\ChartWidget;
use Illuminate\Support\Facades\DB;

class KehadiranChart extends ChartWidget
{
    protected static ?int $sort = 4;
    
    protected int | string | array $columnSpan = 'full';

    public function getHeading(): string
    {
        return 'Statistik Kehadiran Per Hari';
    }

    protected function getData(): array
    {
        $days = ['Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat'];
        
        $masukPerHari = [];
        $tidakMasukPerHari = [];
        
        foreach ($days as $day) {
            $masukPerHari[] = GuruMengajar::where('status', 'Masuk')
                ->whereHas('jadwal', function ($query) use ($day) {
                    $query->where('hari', $day);
                })
                ->count();
                
            $tidakMasukPerHari[] = GuruMengajar::where('status', 'Tidak Masuk')
                ->whereHas('jadwal', function ($query) use ($day) {
                    $query->where('hari', $day);
                })
                ->count();
        }
        
        return [
            'datasets' => [
                [
                    'label' => 'Masuk',
                    'data' => $masukPerHari,
                    'backgroundColor' => 'rgba(34, 197, 94, 0.7)',
                    'borderColor' => 'rgb(34, 197, 94)',
                    'borderWidth' => 1,
                ],
                [
                    'label' => 'Tidak Masuk',
                    'data' => $tidakMasukPerHari,
                    'backgroundColor' => 'rgba(239, 68, 68, 0.7)',
                    'borderColor' => 'rgb(239, 68, 68)',
                    'borderWidth' => 1,
                ],
            ],
            'labels' => $days,
        ];
    }

    protected function getType(): string
    {
        return 'bar';
    }
}
