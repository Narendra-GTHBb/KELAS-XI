<?php

namespace Database\Seeders;

use App\Models\Order;
use App\Models\OrderItem;
use App\Models\User;
use App\Models\Product;
use Illuminate\Database\Seeder;
use Carbon\Carbon;

class SampleOrderSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $users = User::all();
        $products = Product::where('is_active', true)->get();
        
        if ($users->isEmpty()) {
            echo "Please create users first!\n";
            return;
        }
        
        if ($products->isEmpty()) {
            echo "Please create products first!\n";
            return;
        }

$statuses = ['pending', 'confirmed', 'processing', 'shipped', 'delivered', 'cancelled'];
        $paymentMethods = ['cash', 'transfer', 'credit_card', 'e_wallet'];

        // Create 30 sample orders
        for ($i = 1; $i <= 30; $i++) {
            $user = $users->random();
            $status = collect($statuses)->random();
            $itemCount = rand(1, 4);
            
            // Calculate total from items
            $orderProducts = $products->random($itemCount);
            $subtotal = 0;
            
            foreach ($orderProducts as $product) {
                $quantity = rand(1, 3);
                $subtotal += $product->price * $quantity;
            }
            
            $taxAmount = $subtotal * 0.1; // 10% tax
            $shippingAmount = rand(5, 20);
            $totalAmount = $subtotal + $taxAmount + $shippingAmount;
            
            $shippingAddress = [
                'name' => $user->name,
                'phone' => '08' . rand(10000000, 99999999),
                'address' => rand(100, 999) . ' Main Street',
                'city' => 'Jakarta',
                'state' => 'DKI Jakarta',
                'postal_code' => (string)rand(10000, 99999),
            ];
            
            $order = Order::create([
                'user_id' => $user->id,
                'order_number' => 'ORD-' . date('Ymd') . '-' . strtoupper(uniqid()),
                'status' => $status,
                'total_amount' => $totalAmount,
                'tax_amount' => $taxAmount,
                'shipping_amount' => $shippingAmount,
                'payment_method' => collect($paymentMethods)->random(),
                'payment_status' => $status === 'delivered' ? 'paid' : ($status === 'cancelled' ? 'failed' : 'pending'),
                'shipping_address' => json_encode($shippingAddress),
                'billing_address' => json_encode($shippingAddress),
                'notes' => rand(0, 10) > 7 ? 'Please deliver between 9 AM - 5 PM' : null,
                'shipped_at' => in_array($status, ['shipped', 'delivered']) ? Carbon::now()->subDays(rand(3, 20)) : null,
                'delivered_at' => $status === 'delivered' ? Carbon::now()->subDays(rand(1, 10)) : null,
                'created_at' => Carbon::now()->subDays(rand(1, 90)),
                'updated_at' => Carbon::now()->subDays(rand(0, 30)),
            ]);

            // Create order items
            foreach ($orderProducts as $product) {
                $quantity = rand(1, 3);
                
                OrderItem::create([
                    'order_id' => $order->id,
                    'product_id' => $product->id,
                    'product_name' => $product->name,
                    'quantity' => $quantity,
                    'price' => $product->price,
                    'total' => $product->price * $quantity,
                ]);
            }
        }

        echo "Created 30 sample orders!\n";
    }
}
