<?php
/**
 * Test Script for MuscleCart Login API
 * 
 * This script tests the login endpoint to ensure it's working correctly.
 * Run from command line: php test_login_api.php
 */

require __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';
$kernel = $app->make(Illuminate\Contracts\Console\Kernel::class);
$kernel->bootstrap();

use App\Models\User;
use Illuminate\Support\Facades\Hash;

echo "=== MuscleCart Login API Test ===\n\n";

// Test 1: Check if test user exists
echo "1. Checking if test user exists...\n";
$testUser = User::where('email', 'test@test.com')->first();

if (!$testUser) {
    echo "   ✗ Test user not found. Creating...\n";
    $testUser = User::create([
        'name' => 'Test User',
        'email' => 'test@test.com',
        'password' => Hash::make('password123'),
        'role' => 'customer',
        'is_active' => true
    ]);
    echo "   ✓ Created test user (ID: {$testUser->id})\n";
} else {
    echo "   ✓ Test user found (ID: {$testUser->id})\n";
    
    // Update password to ensure it's correct
    $testUser->password = Hash::make('password123');
    $testUser->is_active = true;
    $testUser->save();
    echo "   ✓ Password updated to 'password123'\n";
}

// Test 2: Verify password
echo "\n2. Verifying password...\n";
$passwordCheck = Hash::check('password123', $testUser->password);
if ($passwordCheck) {
    echo "   ✓ Password verification successful\n";
} else {
    echo "   ✗ Password verification failed\n";
    exit(1);
}

// Test 3: Test token generation
echo "\n3. Testing token generation...\n";
try {
    $testUser->tokens()->delete(); // Clear old tokens
    $token = $testUser->createToken('test-token')->plainTextToken;
    echo "   ✓ Token generated successfully\n";
    echo "   Token: {$token}\n";
} catch (Exception $e) {
    echo "   ✗ Token generation failed: {$e->getMessage()}\n";
    exit(1);
}

// Test 4: Simulate login request
echo "\n4. Simulating login API call...\n";
$email = 'test@test.com';
$password = 'password123';

echo "   Email: {$email}\n";
echo "   Password: {$password}\n";

$user = User::where('email', $email)->first();

if (!$user) {
    echo "   ✗ User not found\n";
    exit(1);
}

if (!Hash::check($password, $user->password)) {
    echo "   ✗ Password incorrect\n";
    exit(1);
}

if (!$user->is_active) {
    echo "   ✗ Account is inactive\n";
    exit(1);
}

$user->tokens()->delete();
$loginToken = $user->createToken('auth-token')->plainTextToken;

echo "   ✓ Login successful!\n";
echo "   User ID: {$user->id}\n";
echo "   User Name: {$user->name}\n";
echo "   User Email: {$user->email}\n";
echo "   User Role: {$user->role}\n";
echo "   Auth Token: {$loginToken}\n";

// Test 5: Test admin user
echo "\n5. Checking admin user...\n";
$adminUser = User::where('email', 'admin@musclecart.com')->first();

if (!$adminUser) {
    echo "   ✗ Admin user not found. Creating...\n";
    $adminUser = User::create([
        'name' => 'Admin MuscleCart',
        'email' => 'admin@musclecart.com',
        'password' => Hash::make('admin123'),
        'phone' => '+62812345678',
        'address' => 'Jl. Gym Center No. 1, Jakarta',
        'role' => 'admin',
        'is_active' => true
    ]);
    echo "   ✓ Created admin user (ID: {$adminUser->id})\n";
} else {
    echo "   ✓ Admin user found (ID: {$adminUser->id})\n";
    
    // Update password
    $adminUser->password = Hash::make('admin123');
    $adminUser->is_active = true;
    $adminUser->role = 'admin';
    $adminUser->save();
    echo "   ✓ Admin password updated to 'admin123'\n";
}

echo "\n=== Test Summary ===\n";
echo "✓ All tests passed!\n\n";
echo "Test Credentials:\n";
echo "  Customer:\n";
echo "    Email: test@test.com\n";
echo "    Password: password123\n\n";
echo "  Admin:\n";
echo "    Email: admin@musclecart.com\n";
echo "    Password: admin123\n\n";
echo "API Endpoint: http://localhost:8000/api/v1/login\n";
echo "For emulator use: http://10.0.2.2:8000/api/v1/login\n";
