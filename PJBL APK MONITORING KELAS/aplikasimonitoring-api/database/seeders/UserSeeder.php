<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;
use App\Models\User;

class UserSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Create user Siswa
        User::updateOrCreate(
            ['username' => 'siswa'],
            [
                'nama' => 'Siswa Test',
                'email' => 'siswa@test.com',
                'password' => Hash::make('123'),
                'role' => 'Siswa',
                'kelas_id' => 1, // X RPL
            ]
        );

        // Note: Role 'Guru' belum ada di database enum
        // Untuk testing Guru, gunakan akun melalui panel admin

        // Create user Kurikulum
        User::updateOrCreate(
            ['username' => 'kurikulum'],
            [
                'nama' => 'Kurikulum Test',
                'email' => 'kurikulum@test.com',
                'password' => Hash::make('123'),
                'role' => 'Kurikulum',
            ]
        );

        // Create user Kepala Sekolah
        User::updateOrCreate(
            ['username' => 'kepsek'],
            [
                'nama' => 'Kepala Sekolah Test',
                'email' => 'kepsek@test.com',
                'password' => Hash::make('123'),
                'role' => 'Kepala Sekolah',
            ]
        );

        // Create user Admin 1
        User::updateOrCreate(
            ['email' => 'admin1@sekolah.com'],
            [
                'nama' => 'Admin 1',
                'username' => 'admin1',
                'password' => Hash::make('password123'),
                'role' => 'Admin',
            ]
        );

        // Create user Admin 2
        User::updateOrCreate(
            ['email' => 'admin2@sekolah.com'],
            [
                'nama' => 'Admin 2',
                'username' => 'admin2',
                'password' => Hash::make('password123'),
                'role' => 'Admin',
            ]
        );
    }
}
