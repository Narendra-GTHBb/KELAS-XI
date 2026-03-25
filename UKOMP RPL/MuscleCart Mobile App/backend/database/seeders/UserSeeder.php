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
        // Admin user
        User::create([
            'name' => 'Admin MuscleCart',
            'email' => 'admin@musclecart.com',
            'password' => Hash::make('admin123'),
            'phone' => '+62812345678',
            'address' => 'Jl. Gym Center No. 1, Jakarta',
            'role' => 'admin',
            'is_active' => true,
        ]);

        // Test user
        User::create([
            'name' => 'Test User',
            'email' => 'test@test.com',
            'password' => Hash::make('password123'),
            'role' => 'customer',
            'is_active' => true,
        ]);

        // Sample customers
        $customers = [
            [
                'name' => 'John Doe',
                'email' => 'john@example.com',
                'password' => Hash::make('password123'),
                'phone' => '+62821234567',
                'address' => 'Jl. Fitness Street No. 10, Bandung',
                'role' => 'customer',
                'is_active' => true,
            ],
            [
                'name' => 'Jane Smith',
                'email' => 'jane@example.com',
                'password' => Hash::make('password123'),
                'phone' => '+62831234567',
                'address' => 'Jl. Health Avenue No. 25, Surabaya',
                'role' => 'customer',
                'is_active' => true,
            ],
            [
                'name' => 'Mike Wilson',
                'email' => 'mike@example.com',
                'password' => Hash::make('password123'),
                'phone' => '+62841234567',
                'address' => 'Jl. Workout Lane No. 5, Medan',
                'role' => 'customer',
                'is_active' => true,
            ],
        ];

        foreach ($customers as $customer) {
            User::create($customer);
        }
    }
}
