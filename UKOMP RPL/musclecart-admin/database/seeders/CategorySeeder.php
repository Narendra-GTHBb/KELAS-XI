<?php

namespace Database\Seeders;

use App\Models\Category;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Carbon\Carbon;

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
                'is_active' => true,
            ],
            [
                'name' => 'Strength Training',
                'description' => 'Weight training equipment for building muscle and strength',
                'is_active' => true,
            ],
            [
                'name' => 'Free Weights',
                'description' => 'Dumbbells, barbells, and weight plates for versatile workouts',
                'is_active' => true,
            ],
            [
                'name' => 'Fitness Accessories',
                'description' => 'Supporting equipment for enhanced workout experience',
                'is_active' => true,
            ],
            [
                'name' => 'Home Gym',
                'description' => 'Complete gym solutions for home fitness enthusiasts',
                'is_active' => true,
            ],
            [
                'name' => 'Supplements',
                'description' => 'Nutritional supplements for fitness and health goals',
                'is_active' => true,
            ],
            [
                'name' => 'Yoga & Pilates',
                'description' => 'Equipment for mindful movement and flexibility training',
                'is_active' => false,
            ],
            [
                'name' => 'Outdoor Fitness',
                'description' => 'Equipment for outdoor workouts and adventures',
                'is_active' => true,
            ]
        ];

        foreach ($categories as $category) {
            Category::create($category);
        }
    }
}
