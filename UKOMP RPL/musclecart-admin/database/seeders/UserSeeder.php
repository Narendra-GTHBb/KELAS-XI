<?php

namespace Database\Seeders;

use App\Models\User;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;

class UserSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Admin user - using existing database structure
        \DB::table('users')->insert([
            'name' => 'Admin MuscleCart',
            'email' => 'admin@musclecart.com',
            'password_hash' => Hash::make('admin123'),
            'phone' => '+62812345678',
            'address' => 'Jl. Gym Center No. 1, Jakarta',
            'is_admin' => 1,
            'created_at' => round(microtime(true) * 1000),
            'updated_at' => round(microtime(true) * 1000),
        ]);

        // Sample customers
        $customers = [
            [
                'name' => 'John Doe',
                'email' => 'john@example.com',
                'password_hash' => Hash::make('password123'),
                'phone' => '+62821234567',
                'address' => 'Jl. Fitness Street No. 10, Bandung',
                'is_admin' => 0,
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
            [
                'name' => 'Jane Smith',
                'email' => 'jane@example.com',
                'password_hash' => Hash::make('password123'),
                'phone' => '+62831234567',
                'address' => 'Jl. Health Avenue No. 25, Surabaya',
                'is_admin' => 0,
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
            [
                'name' => 'Mike Wilson',
                'email' => 'mike@example.com',
                'password_hash' => Hash::make('password123'),
                'phone' => '+62841234567',
                'address' => 'Jl. Workout Lane No. 5, Medan',
                'is_admin' => 0,
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
        ];

        foreach ($customers as $customer) {
            \DB::table('users')->insert($customer);
        }
    }
}
