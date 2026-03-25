<?php

require __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use Illuminate\Support\Facades\DB;

// Clear existing favorites for user 8
DB::table('favorites')->where('user_id', 8)->delete();

// Get first 6 products
$products = DB::table('products')->take(6)->pluck('id');

// Add favorites
foreach ($products as $productId) {
    DB::table('favorites')->insert([
        'user_id' => 8,
        'product_id' => $productId,
        'created_at' => date('Y-m-d H:i:s'),
        'updated_at' => date('Y-m-d H:i:s')
    ]);
}

echo "Added " . $products->count() . " favorites for user 8\n";
