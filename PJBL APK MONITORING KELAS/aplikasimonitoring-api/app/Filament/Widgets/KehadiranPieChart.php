<?php

namespace App\Filament\Widgets;

use App\Models\GuruMengajar;
use Filament\Widgets\ChartWidget;

class KehadiranPieChart extends ChartWidget
{
    protected static ?int $sort = 5;

    public function getHeading(): string
    {
        return 'Perbandingan Status Kehadiran';
    }

    protected function getData(): array
    {
        $masuk = GuruMengajar::where('status', 'Masuk')->count();
        $tidakMasuk = GuruMengajar::where('status', 'Tidak Masuk')->count();
        
        return [
            'datasets' => [
                [
                    'data' => [$masuk, $tidakMasuk],
                    'backgroundColor' => [
                        'rgba(34, 197, 94, 0.8)',  // Hijau untuk Masuk
                        'rgba(239, 68, 68, 0.8)',  // Merah untuk Tidak Masuk
                    ],
                    'borderColor' => [
                        'rgb(34, 197, 94)',
                        'rgb(239, 68, 68)',
                    ],
                    'borderWidth' => 1,
                ],
            ],
            'labels' => ['Masuk', 'Tidak Masuk'],
        ];
    }

    protected function getType(): string
    {
        return 'doughnut';
    }
    
    protected function getOptions(): array
    {
        return [
            'plugins' => [
                'legend' => [
                    'position' => 'bottom',
                ],
            ],
        ];
    }
}
