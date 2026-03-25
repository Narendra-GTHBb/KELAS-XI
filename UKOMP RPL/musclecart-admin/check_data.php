<?php

require __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

echo "========================================\n";
echo "DATABASE CHECK - Categories & Products\n";
echo "========================================\n\n";

// Check Categories
echo "CATEGORIES:\n";
echo str_repeat("-", 60) . "\n";
$categories = DB::table('categories')->orderBy('id')->get();
echo "Total: " . $categories->count() . "\n";
echo "Active: " . $categories->where('is_active', 1)->count() . "\n";
echo "Inactive: " . $categories->where('is_active', 0)->count() . "\n\n";

echo "Category List:\n";
foreach ($categories as $cat) {
    $status = $cat->is_active ? 'ACTIVE  ' : 'INACTIVE';
    printf("%2d. %-30s [%s]\n", $cat->id, $cat->name, $status);
}

echo "\n\nPRODUCTS:\n";
echo str_repeat("-", 60) . "\n";
$products = DB::table('products')->orderBy('id')->get();
echo "Total: " . $products->count() . "\n";
echo "Active: " . $products->where('is_active', 1)->count() . "\n";
echo "Inactive: " . $products->where('is_active', 0)->count() . "\n\n";

echo "Product List:\n";
foreach ($products as $prod) {
    $status = $prod->is_active ? 'ACTIVE  ' : 'INACTIVE';
    printf("%2d. %-40s [%s] Cat:%d\n", $prod->id, $prod->name, $status, $prod->category_id);
}

echo "\n========================================\n";
