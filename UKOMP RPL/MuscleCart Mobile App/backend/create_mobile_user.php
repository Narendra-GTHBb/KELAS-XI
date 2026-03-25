<?php
/**
 * Create New Test User for Mobile App
 */

require __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';
$kernel = $app->make(Illuminate\Contracts\Console\Kernel::class);
$kernel->bootstrap();

use App\Models\User;
use Illuminate\Support\Facades\Hash;

echo "=== Creating New Test User ===\n\n";

// Delete old test users
echo "Cleaning old test users...\n";
User::whereIn('email', ['test@test.com', 'user@user.com', 'mobile@test.com'])->delete();
echo "✓ Old users deleted\n\n";

// Create new simple user
echo "Creating new test user...\n";
$user = User::create([
    'name' => 'Mobile User',
    'email' => 'user@user.com',
    'password' => Hash::make('user123'),
    'role' => 'customer',
    'is_active' => true,
]);

echo "✓ User created successfully!\n\n";

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n";
echo "📱 MOBILE APP LOGIN CREDENTIALS\n";
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n";
echo "Email:    user@user.com\n";
echo "Password: user123\n";
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n";

// Test password
echo "Testing password...\n";
if (Hash::check('user123', $user->password)) {
    echo "✓ Password verification: OK\n";
} else {
    echo "✗ Password verification: FAILED\n";
    exit(1);
}

// Generate token
echo "\nGenerating auth token...\n";
$user->tokens()->delete();
$token = $user->createToken('mobile-app')->plainTextToken;
echo "✓ Token: $token\n\n";

// Test login
echo "Testing login process...\n";
$testUser = User::where('email', 'user@user.com')->first();
if (!$testUser) {
    echo "✗ User not found\n";
    exit(1);
}

if (!Hash::check('user123', $testUser->password)) {
    echo "✗ Password check failed\n";
    exit(1);
}

echo "✓ Login test: PASSED\n\n";

echo "=== READY TO USE ===\n";
echo "Gunakan credentials di atas untuk login di mobile app!\n";
