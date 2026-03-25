<?php

namespace Database\Seeders;

use App\Models\Product;
use App\Models\Category;
use Illuminate\Database\Seeder;

class SampleProductSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $categories = Category::all();
        
        if ($categories->isEmpty()) {
            echo "Please seed categories first!\n";
            return;
        }

        $productNames = [
            'Pro Adjustable Dumbbells',
            'Olympic Weight Bar',
            'Resistance Band Set',
            'Yoga Mat Premium',
            'Foam Roller',
            'Jump Rope Speed',
            'Kettlebell Set',
            'Pull-Up Bar',
            'Ab Wheel Roller',
            'Medicine Ball',
            'Battle Rope',
            'Suspension Trainer',
            'Agility Ladder',
            'Exercise Ball',
            'Ankle Weights',
            'Wrist Wraps',
            'Lifting Belt',
            'Gym Gloves',
            'Water Bottle',
            'Sports Towel',
            'Protein Shaker',
            'Weight Plates Set',
            'Barbell Collars',
            'Bench Press',
            'Squat Rack'
        ];

        foreach ($productNames as $index => $name) {
            $category = $categories->random();
            
            Product::create([
                'name' => $name,
                'description' => 'High-quality ' . strtolower($name) . ' for professional and home fitness training. Durable construction and ergonomic design for optimal performance.',
                'price' => rand(2999, 99999) / 100, // Random price between 29.99 and 999.99
                'stock_quantity' => rand(10, 100),
                'category_id' => $category->id,
                'brand' => collect(['Nike', 'Adidas', 'Reebok', 'Under Armour', 'Puma', 'MuscleCart'])->random(),
                'weight' => rand(5, 500) / 10, // Random weight
                'is_active' => rand(0, 10) > 1, // 90% active
                'is_featured' => rand(0, 10) > 7, // 30% featured
            ]);
        }

        echo "Created " . count($productNames) . " sample products!\n";
    }
}
