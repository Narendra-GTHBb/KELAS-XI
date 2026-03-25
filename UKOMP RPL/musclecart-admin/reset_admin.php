<?php

require __DIR__.'/vendor/autoload.php';

$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use App\Models\User;
use Illuminate\Support\Facades\Hash;

echo "Resetting admin password...\n\n";

$admin = User::where('email', 'admin@musclecart.com')->first();

if ($admin) {
    $admin->password = Hash::make('admin123');
    $admin->save();
    
    echo "✓ Admin password has been reset!\n";
    echo "  Email: admin@musclecart.com\n";
    echo "  Password: admin123\n";
} else {
    echo "✗ Admin user not found!\n";
    echo "Creating new admin user...\n";
    
    User::create([
        'name' => 'Admin MuscleCart',
        'email' => 'admin@musclecart.com',
        'password' => Hash::make('admin123'),
        'phone' => '081234567890',
        'address' => 'Jakarta, Indonesia',
        'role' => 'admin',
    ]);
    
    echo "✓ Admin user created!\n";
    echo "  Email: admin@musclecart.com\n";
    echo "  Password: admin123\n";
}
