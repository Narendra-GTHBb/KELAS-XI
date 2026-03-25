<?php
require __DIR__ . '/musclecart-admin/vendor/autoload.php';

$app = require_once __DIR__ . '/musclecart-admin/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

echo "Recently Updated Products:\n";
echo str_repeat("=", 80) . "\n";

$products = \App\Models\Product::orderBy('updated_at', 'desc')
    ->take(10)
    ->get(['id', 'name', 'price', 'updated_at']);

foreach ($products as $p) {
    echo sprintf(
        "ID: %-3d | Price: Rp %10s | Updated: %s\nName: %s\n%s\n",
        $p->id,
        number_format($p->price, 0, ',', '.'),
        $p->updated_at->format('Y-m-d H:i:s'),
        $p->name,
        str_repeat("-", 80)
    );
}
