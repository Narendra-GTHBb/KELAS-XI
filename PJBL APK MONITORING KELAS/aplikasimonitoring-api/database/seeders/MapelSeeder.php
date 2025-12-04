<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\Mapel;

class MapelSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $mapels = [
            ['mapel' => 'Bahasa Indonesia', 'kode_mapel' => 'BIN'],
            ['mapel' => 'Matematika', 'kode_mapel' => 'MTK'],
            ['mapel' => 'IPA', 'kode_mapel' => 'IPA'],
            ['mapel' => 'IPS', 'kode_mapel' => 'IPS'],
            ['mapel' => 'Bahasa Inggris', 'kode_mapel' => 'BING'],
        ];

        foreach ($mapels as $m) {
            Mapel::updateOrCreate(
                ['kode_mapel' => $m['kode_mapel']],
                $m
            );
        }
    }
}
