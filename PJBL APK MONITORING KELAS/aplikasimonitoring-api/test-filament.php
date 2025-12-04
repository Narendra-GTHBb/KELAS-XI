<?php

require __DIR__.'/vendor/autoload.php';
$app = require_once __DIR__.'/bootstrap/app.php';
$app->make(\Illuminate\Contracts\Console\Kernel::class)->bootstrap();

echo "=== FILAMENT RESOURCES CHECK ===\n\n";

$panel = \Filament\Facades\Filament::getCurrentPanel();
$resources = $panel ? $panel->getResources() : [];

if (empty($resources)) {
    echo "ERROR: No resources found!\n";
} else {
    echo "Found " . count($resources) . " resources:\n";
    foreach ($resources as $resource) {
        echo "✓ " . $resource . "\n";
    }
}

echo "\n=== USER CHECK ===\n\n";
$users = \App\Models\User::where('role', 'Admin')->get(['email', 'nama', 'role']);
echo "Admin users:\n";
foreach ($users as $user) {
    echo "- {$user->email} ({$user->nama}) - {$user->role}\n";
}
