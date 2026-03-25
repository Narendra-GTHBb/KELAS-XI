<?php
require __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';
$app->make(Illuminate\Contracts\Console\Kernel::class)->bootstrap();

$products = \App\Models\Product::select('id', 'name', 'image_url')
    ->where('is_active', true)
    ->get();

echo "\n=== Product Images ===\n\n";
foreach ($products as $product) {
    echo "[{$product->id}] {$product->name}\n";
    echo "  Image URL: " . ($product->image_url ?: 'NULL') . "\n\n";
}
