<?php
require __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';
$app->make(Illuminate\Contracts\Console\Kernel::class)->bootstrap();

echo "\n=== IMAGE URL CHECK ===\n\n";

$product = \App\Models\Product::first();
if ($product) {
    $rawImageUrl = $product->getAttributes()['image_url'];
    echo "Product: {$product->name}\n";
    echo "Raw image_url from DB: {$rawImageUrl}\n\n";
    
    // Check different possible locations
    $locations = [
        'storage/app/public/' . $rawImageUrl,
        'public/storage/' . $rawImageUrl,
        'public/' . $rawImageUrl,
    ];
    
    foreach ($locations as $location) {
        $fullPath = base_path($location);
        $exists = file_exists($fullPath) ? '✅ EXISTS' : '❌ NOT FOUND';
        echo "{$exists} - {$location}\n";
    }
    
    echo "\n";
    echo "URL yang harus diakses dari Android:\n";
    echo "http://10.0.2.2:8000/storage/{$rawImageUrl}\n";
}
