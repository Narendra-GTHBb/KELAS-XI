<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\Jadwal;
use App\Models\Guru;
use App\Models\Mapel;
use App\Models\TahunAjaran;
use App\Models\Kelas;

class JadwalSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $gurus = Guru::all();
        $mapels = Mapel::all();
        $tahunAjaran = TahunAjaran::first();
        $kelasList = Kelas::all();
        
        if ($gurus->isEmpty() || $mapels->isEmpty() || !$tahunAjaran || $kelasList->isEmpty()) {
            $this->command->warn('Data Guru, Mapel, Tahun Ajaran, atau Kelas belum ada. Seed data tersebut terlebih dahulu.');
            return;
        }

        $hariList = ['Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat'];
        $jamKeList = ['Jam Ke 1-2', 'Jam Ke 3-4', 'Jam Ke 5-6', 'Jam Ke 7-8'];

        // Buat jadwal untuk setiap kelas dan setiap hari dengan berbagai jam
        foreach ($kelasList as $kelas) {
            foreach ($hariList as $hari) {
                // Setiap hari ada 2-4 jadwal per kelas
                $jumlahJadwal = rand(2, 4);
                $usedJamKe = [];
                
                for ($i = 0; $i < $jumlahJadwal; $i++) {
                    // Pilih jam yang belum digunakan
                    $availableJamKe = array_diff($jamKeList, $usedJamKe);
                    if (empty($availableJamKe)) break;
                    
                    $jamKe = $availableJamKe[array_rand($availableJamKe)];
                    $usedJamKe[] = $jamKe;
                    
                    Jadwal::create([
                        'guru_id' => $gurus->random()->id,
                        'mapel_id' => $mapels->random()->id,
                        'tahun_ajaran_id' => $tahunAjaran->id,
                        'kelas_id' => $kelas->id,
                        'jam_ke' => $jamKe,
                        'hari' => $hari,
                    ]);
                }
            }
        }
        
        $this->command->info('Jadwal seeder berhasil. Total jadwal: ' . Jadwal::count());
    }
}

