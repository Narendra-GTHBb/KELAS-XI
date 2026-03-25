<?php

require __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

echo "========================================\n";
echo "API RESPONSE CHECK\n";
echo "========================================\n\n";

// Simulate API call
$products = DB::table('products')
    ->join('categories', 'products.category_id', '=', 'categories.id')
    ->where('products.is_active', true)
    ->select(
        'products.id',
        'products.name',
        'products.description',
        'products.price',
        'products.stock_quantity',
        'products.image',
        'products.category_id',
        'products.created_at',
        'products.updated_at',
        'categories.name as category_name'
    )
    ->get();

echo "Products from API:\n\n";
foreach ($products as $product) {
    echo "ID: {$product->id}\n";
    echo "Name: {$product->name}\n";
    echo "Image: " . ($product->image ?? 'NULL') . "\n";
    echo "Category: {$product->category_name}\n";
    echo "---\n";
}

echo "\n\nJSON Response (as API would return):\n";
$response = [
    'status' => 'success',
    'data' => $products->map(function($p) {
        return [
            'id' => $p->id,
            'name' => $p->name,
            'description' => $p->description,
            'price' => $p->price,
            'stock_quantity' => $p->stock_quantity,
            'image_url' => $p->image,
            'category_id' => $p->category_id,
            'created_at' => $p->created_at,
            'updated_at' => $p->updated_at,
            'category' => [
                'name' => $p->category_name
            ]
        ];
    })
];

echo json_encode($response, JSON_PRETTY_PRINT) . "\n";
