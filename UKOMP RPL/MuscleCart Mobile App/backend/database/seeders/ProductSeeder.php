<?php

namespace Database\Seeders;

use App\Models\Product;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class ProductSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $products = [
            // Cardio Equipment (Category ID: 1)
            [
                'name' => 'Treadmill Pro X1',
                'description' => 'Professional grade treadmill with advanced digital display and heart rate monitoring. Perfect for home and commercial use.',
                'price' => 15999000.00,
                'stock_quantity' => 10,
                'image_url' => 'treadmill-pro.jpg',
                'category_id' => 1,
                'is_active' => 1,
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
            [
                'name' => 'Exercise Bike Elite',
                'description' => 'High-quality stationary bike with magnetic resistance and LCD monitor for tracking your fitness goals.',
                'price' => 4599000.00,
                'stock_quantity' => 15,
                'image_url' => 'exercise-bike.jpg',
                'category_id' => 1,
                'is_active' => 1,
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
            // Strength Training (Category ID: 2)
            [
                'name' => 'Multi Station Home Gym',
                'description' => 'Complete home gym station with multiple exercise options. Build your entire body with this versatile equipment.',
                'price' => 12999000.00,
                'stock_quantity' => 5,
                'image_url' => 'multi-station.jpg',
                'category_id' => 2,
                'is_active' => 1,
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
            [
                'name' => 'Power Rack Station',
                'description' => 'Heavy-duty power rack for serious strength training. Perfect for squats, bench press, and pull-ups.',
                'price' => 8999000.00,
                'stock_quantity' => 8,
                'image_url' => 'power-rack.jpg',
                'category_id' => 2,
                'is_active' => 1,
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
            // Free Weights (Category ID: 3)
            [
                'name' => 'Adjustable Dumbbell Set',
                'description' => 'Professional adjustable dumbbell set with quick-change weight system. Replaces entire dumbbell rack.',
                'price' => 3499000.00,
                'stock_quantity' => 20,
                'image_url' => 'adjustable-dumbbell.jpg',
                'category_id' => 3,
                'is_active' => 1,
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
            [
                'name' => 'Barbell Set with Plates',
                'description' => 'Complete barbell set with Olympic bar and weight plates. Essential for serious strength training.',
                'price' => 5999000.00,
                'stock_quantity' => 12,
                'image_url' => 'barbell-set.jpg',
                'category_id' => 3,
                'is_active' => 1,
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
            // Fitness Accessories (Category ID: 4)
            [
                'name' => 'Yoga Mat Premium',
                'description' => 'High-quality non-slip yoga mat perfect for yoga, pilates, and floor exercises. Eco-friendly material.',
                'price' => 299000.00,
                'stock_quantity' => 50,
                'image_url' => 'yoga-mat.jpg',
                'category_id' => 4,
                'is_active' => 1,
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
            [
                'name' => 'Resistance Band Set',
                'description' => 'Complete resistance band set with multiple resistance levels. Perfect for home workouts and travel.',
                'price' => 199000.00,
                'stock_quantity' => 30,
                'image_url' => 'resistance-bands.jpg',
                'category_id' => 4,
                'is_active' => 1,
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
            // Home Gym (Category ID: 5)
            [
                'name' => 'Complete Home Gym Package',
                'description' => 'All-in-one home gym solution including cardio and strength training equipment. Perfect starter package.',
                'price' => 25999000.00,
                'stock_quantity' => 3,
                'image_url' => 'home-gym-package.jpg',
                'category_id' => 5,
                'is_active' => 1,
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
            [
                'name' => 'Compact Home Gym',
                'description' => 'Space-saving home gym solution perfect for apartments and small spaces. Maximum efficiency.',
                'price' => 7999000.00,
                'stock_quantity' => 7,
                'image_url' => 'compact-gym.jpg',
                'category_id' => 5,
                'is_active' => 1,
                'created_at' => round(microtime(true) * 1000),
                'updated_at' => round(microtime(true) * 1000),
            ],
        ];

        foreach ($products as $product) {
            \DB::table('products')->insert($product);
        }
    }
}
