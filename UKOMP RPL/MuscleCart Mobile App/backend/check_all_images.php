<?php
require __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';
$app->make(Illuminate\Contracts\Console\Kernel::class)->bootstrap();

echo "\n=== ALL PRODUCTS IMAGE CHECK ===\n\n";

$products = \App\Models\Product::where('is_active', true)->get();

foreach ($products as $product) {
    $rawImageUrl = $product->getAttributes()['image_url'];
    echo "ID {$product->id}: {$product->name}\n";
    echo "  DB Path: {$rawImageUrl}\n";
    
    // Check if file exists
    $storagePath = storage_path('app/public/' . $rawImageUrl);
    $exists = file_exists($storagePath);
    echo "  Storage: " . ($exists ? "✅ EXISTS" : "❌ NOT FOUND") . "\n";
    
    if (!$exists) {
        echo "  Looking for: {$storagePath}\n";
    }
    
    echo "  URL: http://10.0.2.2:8000/storage/{$rawImageUrl}\n\n";
}
