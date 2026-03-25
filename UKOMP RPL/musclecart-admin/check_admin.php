<?php

require __DIR__.'/vendor/autoload.php';

$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use App\Models\User;

echo "Checking admin user...\n\n";

$admin = User::where('email', 'admin@musclecart.com')->first();

if ($admin) {
    echo "✓ Admin user found!\n";
    echo "  - ID: {$admin->id}\n";
    echo "  - Name: {$admin->name}\n";
    echo "  - Email: {$admin->email}\n";
    echo "  - Role: {$admin->role}\n";
    echo "  - Has password: " . (!empty($admin->password) ? 'Yes' : 'No') . "\n";
} else {
    echo "✗ Admin user NOT found!\n";
}

echo "\n";
echo "All users in database:\n";
$users = User::all();
foreach ($users as $user) {
    echo "  - {$user->email} ({$user->role})\n";
}
