<?php

require __DIR__.'/vendor/autoload.php';

$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use App\Models\User;
use Illuminate\Support\Facades\Hash;

echo "Testing admin password...\n\n";

$admin = User::where('email', 'admin@musclecart.com')->first();

if ($admin) {
    echo "Admin user found: {$admin->email}\n";
    echo "Password hash: " . substr($admin->password, 0, 30) . "...\n\n";
    
    $testPassword = 'admin123';
    
    echo "Testing password: '$testPassword'\n";
    $check = Hash::check($testPassword, $admin->password);
    
    if ($check) {
        echo "✓ Password matches!\n";
    } else {
        echo "✗ Password does NOT match!\n\n";
        echo "Creating new hash for 'admin123':\n";
        $newHash = Hash::make('admin123');
        echo substr($newHash, 0, 50) . "...\n\n";
        
        echo "Updating admin password...\n";
        $admin->password = $newHash;
        $admin->save();
        echo "✓ Password updated!\n";
    }
} else {
    echo "Admin user not found!\n";
}
