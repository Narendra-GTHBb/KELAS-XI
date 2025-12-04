<?php

namespace Database\Seeders;

use App\Models\User;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    use WithoutModelEvents;

    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        // User::factory(10)->create();

        User::factory()->create([
            'nama' => 'Admin User',
            'email' => 'admin@sekolah.com',
            'role' => 'Admin',
        ]);

        $this->call([
            GuruSeeder::class,
            MapelSeeder::class,
            TahunAjaranSeeder::class,
            KelasSeeder::class,
            JadwalSeeder::class,
            GuruMengajarSeeder::class,
            UserSeeder::class, // Pindah ke akhir agar kelas_id dan guru_id sudah ada
        ]);
    }
}
