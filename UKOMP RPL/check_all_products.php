<?php
require __DIR__ . '/musclecart-admin/vendor/autoload.php';

$app = require_once __DIR__ . '/musclecart-admin/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

echo "\n=== ALL ACTIVE PRODUCTS IN DATABASE ===\n";
echo str_repeat("=", 80) . "\n";

$products = \App\Models\Product::where('is_active', true)
    ->orderBy('id', 'asc')
    ->get(['id', 'name', 'price', 'stock_quantity', 'is_active', 'created_at']);

echo "Total Active Products: " . $products->count() . "\n\n";

foreach ($products as $p) {
    echo sprintf(
        "ID: %-3d | Price: Rp %10s | Stock: %-4d\nName: %s\n%s\n",
        $p->id,
        number_format($p->price, 0, ',', '.'),
        $p->stock_quantity,
        $p->name,
        str_repeat("-", 80)
    );
}
