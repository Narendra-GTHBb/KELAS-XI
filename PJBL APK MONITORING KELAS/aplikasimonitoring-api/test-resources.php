<?php

require __DIR__.'/vendor/autoload.php';
$app = require_once __DIR__.'/bootstrap/app.php';
$kernel = $app->make(\Illuminate\Contracts\Console\Kernel::class);
$kernel->bootstrap();

echo "=== MANUAL RESOURCE TEST ===\n\n";

try {
    $guru = new \App\Filament\Resources\GuruResource();
    echo "✓ GuruResource instantiated\n";
    echo "  Model: " . $guru::getModel() . "\n";
    echo "  Navigation Label: " . ($guru::getNavigationLabel() ?? 'null') . "\n";
} catch (\Exception $e) {
    echo "✗ GuruResource ERROR: " . $e->getMessage() . "\n";
}

try {
    $jadwal = new \App\Filament\Resources\JadwalResource();
    echo "\n✓ JadwalResource instantiated\n";
    echo "  Model: " . $jadwal::getModel() . "\n";
    echo "  Navigation Label: " . ($jadwal::getNavigationLabel() ?? 'null') . "\n";
} catch (\Exception $e) {
    echo "\n✗ JadwalResource ERROR: " . $e->getMessage() . "\n";
}

echo "\n=== PANEL TEST ===\n\n";
try {
    $panelProvider = new \App\Providers\Filament\AdminPanelProvider();
    echo "✓ AdminPanelProvider loaded\n";
    
    $panel = \Filament\Facades\Filament::getPanel('admin');
    echo "✓ Admin panel found\n";
    
    $resources = $panel->getResources();
    echo "\nRegistered resources (" . count($resources) . "):\n";
    foreach ($resources as $resource) {
        echo "  - " . $resource . "\n";
    }
} catch (\Exception $e) {
    echo "✗ Panel ERROR: " . $e->getMessage() . "\n";
    echo "  " . $e->getTraceAsString() . "\n";
}
