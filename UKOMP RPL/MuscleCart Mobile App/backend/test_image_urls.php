<?php
// Test URL gambar dari berbagai sources
require __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';
$app->make(Illuminate\Contracts\Console\Kernel::class)->bootstrap();

echo "\n=== IMAGE URL TESTING ===\n\n";

$product = \App\Models\Product::first();
if ($product) {
    echo "Product: {$product->name}\n";
    echo "Raw image_url: {$product->getAttributes()['image_url']}\n";
    echo "Processed image_url: {$product->image_url}\n";
    
    // Test storage URLs
    $baseUrls = [
        'http://localhost:8000/storage/',
        'http://127.0.0.1:8000/storage/', 
        'http://10.0.2.2:8000/storage/',
        config('app.url') . '/storage/'
    ];
    
    $rawImagePath = $product->getAttributes()['image_url'];
    
    foreach ($baseUrls as $baseUrl) {
        $fullUrl = $baseUrl . $rawImagePath;
        echo "\nTesting: $fullUrl";
        
        $context = stream_context_create([
            'http' => [
                'timeout' => 5,
                'method' => 'HEAD'
            ]
        ]);
        
        $headers = @get_headers($fullUrl, 1, $context);
        if ($headers && strpos($headers[0], '200') !== false) {
            echo " ✅ OK";
        } else {
            echo " ❌ FAIL";
        }
    }
    
    echo "\n\n";
}