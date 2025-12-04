<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\TahunAjaran;

class TahunAjaranSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $tahuns = [
            ['tahun' => '2023/2024'],
            ['tahun' => '2024/2025'],
            ['tahun' => '2025/2026'],
        ];

        foreach ($tahuns as $tahun) {
            TahunAjaran::updateOrCreate(
                ['tahun' => $tahun['tahun']],
                $tahun
            );
        }
    }
}
