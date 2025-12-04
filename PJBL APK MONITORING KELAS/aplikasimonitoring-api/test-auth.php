<?php

require 'vendor/autoload.php';
$app = require_once 'bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use App\Models\User;
use Filament\Facades\Filament;
use Illuminate\Support\Facades\Auth;

// Login as admin
$admin = User::where('email', 'admin1@sekolah.com')->first();
if (!$admin) {
    echo "Admin user not found!" . PHP_EOL;
    exit(1);
}

Auth::login($admin);
echo "Logged in as: " . $admin->email . " (Role: " . $admin->role . ")" . PHP_EOL;

echo PHP_EOL . "=== CHECKING RESOURCE AUTHORIZATION ===" . PHP_EOL;

foreach (Filament::getResources() as $resource) {
    echo PHP_EOL . "Resource: " . class_basename($resource) . PHP_EOL;
    
    // Check if user can access
    try {
        $canViewAny = $resource::canViewAny();
        echo "  canViewAny: " . ($canViewAny ? 'YES' : 'NO') . PHP_EOL;
    } catch (\Exception $e) {
        echo "  canViewAny ERROR: " . $e->getMessage() . PHP_EOL;
    }
    
    // Check navigation registration
    try {
        $shouldRegister = $resource::shouldRegisterNavigation();
        echo "  shouldRegisterNavigation: " . ($shouldRegister ? 'YES' : 'NO') . PHP_EOL;
    } catch (\Exception $e) {
        echo "  shouldRegisterNavigation ERROR: " . $e->getMessage() . PHP_EOL;
    }
}

echo PHP_EOL . "=== DONE ===" . PHP_EOL;
