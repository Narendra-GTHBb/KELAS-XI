<?php

namespace Database\Seeders;

use App\Models\Category;
use App\Models\Product;
use App\Models\User;
use App\Models\Order;
use App\Models\OrderItem;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;
use Carbon\Carbon;

class AdminSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Create admin user
        $admin = User::create([
            'name' => 'Admin MuscleCart',
            'email' => 'admin@musclecart.com',
            'password' => Hash::make('admin123'),
            'phone' => '081234567890',
            'address' => 'Jakarta, Indonesia',
            'role' => 'admin',
        ]);

        // Create regular users
        $users = [];
        for ($i = 1; $i <= 5; $i++) {
            $users[] = User::create([
                'name' => "Customer $i",
                'email' => "customer$i@example.com",
                'password' => Hash::make('password123'),
                'phone' => "08123456789$i",
                'address' => "Address for Customer $i",
                'role' => 'customer',
            ]);
        }

        // Create categories
        $categories = [
            ['name' => 'Protein Powder', 'description' => 'Various protein supplements', 'is_active' => true],
            ['name' => 'Pre-Workout', 'description' => 'Pre-workout supplements', 'is_active' => true],
            ['name' => 'Post-Workout', 'description' => 'Post-workout recovery supplements', 'is_active' => true],
            ['name' => 'Vitamins', 'description' => 'Vitamins and minerals', 'is_active' => true],
            ['name' => 'Equipment', 'description' => 'Gym equipment and accessories', 'is_active' => true],
        ];

        foreach ($categories as $categoryData) {
            Category::create($categoryData);
        }

        // Create products
        $products = [
            [
                'name' => 'Whey Protein Gold Standard',
                'description' => 'Premium whey protein with great taste and quality',
                'price' => 599000,
                'stock_quantity' => 50,
                'image_url' => 'https://example.com/whey-protein.jpg',
                'category_id' => 1,
                'is_active' => true,
            ],
            [
                'name' => 'Creatine Monohydrate',
                'description' => 'Pure creatine monohydrate for muscle building',
                'price' => 299000,
                'stock_quantity' => 30,
                'image_url' => 'https://example.com/creatine.jpg',
                'category_id' => 2,
                'is_active' => true,
            ],
            [
                'name' => 'BCAA Energy Drink',
                'description' => 'Branch chain amino acids for recovery',
                'price' => 399000,
                'stock_quantity' => 5, // Low stock for testing
                'image_url' => 'https://example.com/bcaa.jpg',
                'category_id' => 3,
                'is_active' => true,
            ],
            [
                'name' => 'Multivitamin Complex',
                'description' => 'Complete multivitamin for daily nutrition',
                'price' => 149000,
                'stock_quantity' => 100,
                'image_url' => 'https://example.com/multivitamin.jpg',
                'category_id' => 4,
                'is_active' => true,
            ],
            [
                'name' => 'Resistance Bands Set',
                'description' => 'Set of resistance bands for home workouts',
                'price' => 199000,
                'stock_quantity' => 25,
                'image_url' => 'https://example.com/resistance-bands.jpg',
                'category_id' => 5,
                'is_active' => true,
            ],
            [
                'name' => 'Mass Gainer Protein',
                'description' => 'High calorie protein for weight gain',
                'price' => 799000,
                'stock_quantity' => 20,
                'image_url' => 'https://example.com/mass-gainer.jpg',
                'category_id' => 1,
                'is_active' => true,
            ],
        ];

        $createdProducts = [];
        foreach ($products as $productData) {
            $createdProducts[] = Product::create($productData);
        }

        // Create sample orders
        foreach ($users as $index => $user) {
            if ($index < 3) { // Only create orders for first 3 users
                $orderNumber = 'ORD-' . date('Ymd') . '-' . str_pad(($index + 1), 4, '0', STR_PAD_LEFT);
                $shippingAddress = [
                    'name' => $user->name,
                    'phone' => $user->phone,
                    'address' => $user->address,
                    'city' => 'Jakarta',
                    'postal_code' => '12345'
                ];
                
                $order = Order::create([
                    'order_number' => $orderNumber,
                    'user_id' => $user->id,
                    'status' => ['pending', 'processing', 'delivered'][array_rand(['pending', 'processing', 'delivered'])],
                    'payment_status' => ['pending', 'paid'][array_rand(['pending', 'paid'])],
                    'payment_method' => ['cash', 'transfer', 'e_wallet'][array_rand(['cash', 'transfer', 'e_wallet'])],
                    'total_amount' => 0, // Will be calculated
                    'shipping_address' => json_encode($shippingAddress),
                    'billing_address' => json_encode($shippingAddress),
                ]);

                // Add order items
                $orderTotal = 0;
                $selectedProducts = array_rand($createdProducts, rand(1, 3));
                if (!is_array($selectedProducts)) {
                    $selectedProducts = [$selectedProducts];
                }

                foreach ($selectedProducts as $productIndex) {
                    $product = $createdProducts[$productIndex];
                    $quantity = rand(1, 3);
                    $total = $product->price * $quantity;
                    $orderTotal += $total;

                    OrderItem::create([
                        'order_id' => $order->id,
                        'product_id' => $product->id,
                        'product_name' => $product->name,
                        'quantity' => $quantity,
                        'price' => $product->price,
                        'total' => $total,
                    ]);
                }

                // Update order total
                $order->update(['total_amount' => $orderTotal]);
            }
        }

        echo "✅ Sample data created successfully!\n";
        echo "🔐 Admin Login: admin@musclecart.com / admin123\n";
        echo "👤 Customer Login: customer1@example.com / password123\n";
    }
}