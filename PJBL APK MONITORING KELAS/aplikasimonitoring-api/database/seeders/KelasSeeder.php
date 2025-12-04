<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\Kelas;

class KelasSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $kelass = [
            ['kelas' => 'X RPL'],
            ['kelas' => 'XI RPL'],
            ['kelas' => 'XII RPL'],
            ['kelas' => 'X TKJ'],
            ['kelas' => 'XI TKJ'],
        ];

        foreach ($kelass as $k) {
            Kelas::updateOrCreate(
                ['kelas' => $k['kelas']],
                $k
            );
        }
    }
}
