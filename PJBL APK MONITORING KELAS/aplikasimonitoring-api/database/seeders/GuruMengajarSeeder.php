<?php

namespace Database\Seeders;

use App\Models\GuruMengajar;
use App\Models\Jadwal;
use Illuminate\Database\Seeder;

class GuruMengajarSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $jadwals = Jadwal::all();
        $statuses = ['Masuk', 'Tidak Masuk'];
        $keterangans = [
            'Masuk' => ['Mengajar dengan baik', 'Materi tersampaikan', 'Siswa aktif', null],
            'Tidak Masuk' => ['Tidak ada keterangan', 'Izin keperluan keluarga', 'Sakit', 'Rapat dinas'],
        ];

        // Buat data untuk semua jadwal (bukan hanya 10)
        foreach ($jadwals as $jadwal) {
            // Tentukan status - 80% masuk, 20% tidak masuk untuk realistis
            $status = rand(1, 100) <= 80 ? 'Masuk' : 'Tidak Masuk';
            
            GuruMengajar::create([
                'jadwal_id' => $jadwal->id,
                'status' => $status,
                'keterangan' => $keterangans[$status][array_rand($keterangans[$status])],
            ]);
        }
    }
}
