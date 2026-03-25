<?php

namespace Database\Seeders;

use App\Models\Category;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class CategorySeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $categories = [
            [
                'name' => 'Cardio Equipment',
                'description' => 'Cardiovascular exercise machines for heart health and endurance',
                'image_url' => 'cardio.jpg',
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
            [
                'name' => 'Strength Training',
                'description' => 'Weight training equipment for building muscle and strength',
                'image_url' => 'strength.jpg',
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
            [
                'name' => 'Free Weights',
                'description' => 'Dumbbells, barbells, and weight plates for versatile workouts',
                'image_url' => 'weights.jpg',
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
            [
                'name' => 'Fitness Accessories',
                'description' => 'Supporting equipment for enhanced workout experience',
                'image_url' => 'accessories.jpg',
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
            [
                'name' => 'Home Gym',
                'description' => 'Complete gym solutions for home fitness enthusiasts',
                'image_url' => 'home-gym.jpg',
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
        ];

        foreach ($categories as $category) {
            \DB::table('categories')->insert($category);
        }
    }
}
