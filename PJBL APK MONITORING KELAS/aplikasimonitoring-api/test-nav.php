<?php

require 'vendor/autoload.php';
$app = require_once 'bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use Filament\Facades\Filament;

echo "=== TESTING RESOURCES ===" . PHP_EOL;

foreach (Filament::getResources() as $resource) {
    echo PHP_EOL . "Resource: " . $resource . PHP_EOL;
    
    try {
        $items = $resource::getNavigationItems();
        echo "  Nav Items count: " . count($items) . PHP_EOL;
        
        foreach ($items as $item) {
            echo "  - Label: " . $item->getLabel() . PHP_EOL;
            echo "  - Icon: " . ($item->getIcon() ?? 'NULL') . PHP_EOL;
            echo "  - URL: " . ($item->getUrl() ?? 'NULL') . PHP_EOL;
        }
    } catch (\Exception $e) {
        echo "  ERROR: " . $e->getMessage() . PHP_EOL;
    }
}

echo PHP_EOL . "=== DONE ===" . PHP_EOL;
