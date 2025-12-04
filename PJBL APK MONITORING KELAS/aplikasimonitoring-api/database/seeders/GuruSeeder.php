<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\Guru;

class GuruSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $gurus = [
            ['guru' => 'Budi Setiawan', 'kode_guru' => 'G001', 'telepon' => '081234567890'],
            ['guru' => 'Joko', 'kode_guru' => 'G002', 'telepon' => '081234567891'],
            ['guru' => 'Agus', 'kode_guru' => 'G003', 'telepon' => '081234567892'],
            ['guru' => 'Siti Rahayu', 'kode_guru' => 'G004', 'telepon' => '081234567893'],
            ['guru' => 'Ahmad Fadli', 'kode_guru' => 'G005', 'telepon' => '081234567894'],
        ];

        foreach ($gurus as $g) {
            Guru::updateOrCreate(
                ['kode_guru' => $g['kode_guru']],
                $g
            );
        }
    }
}
