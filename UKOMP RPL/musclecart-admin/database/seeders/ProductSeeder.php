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
        // Get category IDs dynamically
        $cardioId = \App\Models\Category::where('name', 'LIKE', '%Cardio%')->first()->id ?? null;
        $strengthId = \App\Models\Category::where('name', 'LIKE', '%Strength%')->first()->id ?? null;
        $weightsId = \App\Models\Category::where('name', 'LIKE', '%Weights%')->first()->id ?? null;
        $accessoriesId = \App\Models\Category::where('name', 'LIKE', '%Accessories%')->first()->id ?? null;
        $homeGymId = \App\Models\Category::where('name', 'LIKE', '%Home Gym%')->first()->id ?? null;
        $supplementsId = \App\Models\Category::where('name', 'LIKE', '%Supplements%')->first()->id ?? null;
        
        if (!$cardioId || !$strengthId || !$weightsId) {
            echo "Please seed categories first!\n";
            return;
        }
        
        $products = [
            // Cardio Equipment
            [
                'name' => 'Treadmill Pro X1',
                'description' => 'Professional grade treadmill with advanced digital display and heart rate monitoring. Perfect for home and commercial use.',
                'price' => 1599.99,
                'stock_quantity' => 10,
                'category_id' => $cardioId,
                'is_active' => true,
            ],
            [
                'name' => 'Exercise Bike Elite',
                'description' => 'Premium stationary bike with adjustable resistance and built-in workout programs.',
                'price' => 899.99,
                'stock_quantity' => 15,
                'category_id' => $cardioId,
                'is_active' => true,
            ],
            // Strength Training
            [
                'name' => 'Adjustable Dumbbell Set',
                'description' => 'Professional adjustable dumbbells from 5-52.5 lbs per hand.',
                'price' => 299.99,
                'stock_quantity' => 25,
                'category_id' => $weightsId,
                'is_active' => true,
            ],
            [
                'name' => 'Olympic Barbell',
                'description' => 'Professional 45lb Olympic barbell, 7 feet long.',
                'price' => 199.99,
                'stock_quantity' => 20,
                'category_id' => $weightsId,
                'is_active' => true,
            ],
            // Accessories
            [
                'name' => 'Yoga Mat Premium',
                'description' => 'Extra thick yoga mat with carrying strap.',
                'price' => 39.99,
                'stock_quantity' => 50,
                'category_id' => $accessoriesId,
                'is_active' => true,
            ],
        ];

        foreach ($products as $product) {
            Product::create($product);
        }
    }
}
