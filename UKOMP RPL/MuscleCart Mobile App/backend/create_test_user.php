<?php
require __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';
$kernel = $app->make(Illuminate\Contracts\Console\Kernel::class);
$kernel->bootstrap();

use App\Models\User;
use Illuminate\Support\Facades\Hash;

// Create or update test user
$user = User::where('email', 'test@test.com')->first();
if (!$user) {
    $user = User::create([
        'name' => 'Test User',
        'email' => 'test@test.com',
        'password' => Hash::make('password123'),
        'role' => 'customer',
        'is_active' => true
    ]);
    echo "Created new user: {$user->id} - {$user->email}\n";
} else {
    // Update password to known value
    $user->password = Hash::make('password123');
    $user->save();
    echo "Updated existing user: {$user->id} - {$user->email}\n";
}

// Test login
$loginSuccess = Hash::check('password123', $user->password);
echo "Password check: " . ($loginSuccess ? "OK" : "FAILED") . "\n";

// Generate token
$token = $user->createToken('test-token')->plainTextToken;
echo "Test token: {$token}\n";
