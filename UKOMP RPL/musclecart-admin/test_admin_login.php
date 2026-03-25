<?php
/**
 * Test and Fix Admin Login
 * Run from: musclecart-admin folder
 * Command: php test_admin_login.php
 */

require __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';
$kernel = $app->make(Illuminate\Contracts\Console\Kernel::class);
$kernel->bootstrap();

use App\Models\User;
use Illuminate\Support\Facades\Hash;

echo "=== MuscleCart Admin Login Test ===\n\n";

// Test 1: Check if admin user exists
echo "1. Checking for admin user...\n";
$admin = User::where('email', 'admin@musclecart.com')->first();

if (!$admin) {
    echo "   ✗ Admin user not found. Creating new admin...\n";
    $admin = User::create([
        'name' => 'Admin MuscleCart',
        'email' => 'admin@musclecart.com',
        'password' => Hash::make('admin123'),
        'phone' => '081234567890',
        'address' => 'Jakarta, Indonesia',
        'role' => 'admin',
        'is_active' => true,
    ]);
    echo "   ✓ Admin created successfully (ID: {$admin->id})\n";
} else {
    echo "   ✓ Admin user found (ID: {$admin->id})\n";
    echo "   - Name: {$admin->name}\n";
    echo "   - Email: {$admin->email}\n";
    echo "   - Role: {$admin->role}\n";
    
    // Update password to ensure it's correct
    $admin->password = Hash::make('admin123');
    $admin->role = 'admin';
    $admin->is_active = true;
    $admin->save();
    echo "   ✓ Admin password updated to 'admin123'\n";
}

// Test 2: Verify password
echo "\n2. Testing password verification...\n";
$passwordCheck = Hash::check('admin123', $admin->password);
if ($passwordCheck) {
    echo "   ✓ Password 'admin123' verified successfully\n";
} else {
    echo "   ✗ Password verification failed!\n";
    exit(1);
}

// Test 3: Check admin role
echo "\n3. Checking admin role...\n";
if ($admin->role === 'admin') {
    echo "   ✓ User has admin role\n";
} else {
    echo "   ✗ User role is '{$admin->role}', not 'admin'\n";
    echo "   Updating role to 'admin'...\n";
    $admin->role = 'admin';
    $admin->save();
    echo "   ✓ Role updated\n";
}

// Test 4: Check is_active status
echo "\n4. Checking active status...\n";
if ($admin->is_active) {
    echo "   ✓ Admin account is active\n";
} else {
    echo "   ✗ Admin account is inactive\n";
    echo "   Activating account...\n";
    $admin->is_active = true;
    $admin->save();
    echo "   ✓ Account activated\n";
}

// Test 5: Simulate login
echo "\n5. Simulating login process...\n";
$email = 'admin@musclecart.com';
$password = 'admin123';

$user = User::where('email', $email)->first();
if (!$user) {
    echo "   ✗ User not found\n";
    exit(1);
}

if (!Hash::check($password, $user->password)) {
    echo "   ✗ Password mismatch\n";
    exit(1);
}

if ($user->role !== 'admin') {
    echo "   ✗ User is not admin (role: {$user->role})\n";
    exit(1);
}

echo "   ✓ Login simulation successful!\n";

// Alternative admin credentials
echo "\n6. Checking alternative admin...\n";
$altAdmin = User::where('email', 'admin@admin.com')->first();
if (!$altAdmin) {
    echo "   Creating alternative admin: admin@admin.com\n";
    User::create([
        'name' => 'Administrator',
        'email' => 'admin@admin.com',
        'password' => Hash::make('admin'),
        'phone' => '081234567891',
        'address' => 'Jakarta',
        'role' => 'admin',
        'is_active' => true,
    ]);
    echo "   ✓ Alternative admin created\n";
} else {
    echo "   ✓ Alternative admin exists\n";
    $altAdmin->password = Hash::make('admin');
    $altAdmin->role = 'admin';
    $altAdmin->is_active = true;
    $altAdmin->save();
    echo "   ✓ Alternative admin password updated\n";
}

echo "\n=== Test Complete ===\n";
echo "✓ All tests passed!\n\n";

echo "Admin Login Credentials:\n";
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n";
echo "Option 1:\n";
echo "  Email:    admin@musclecart.com\n";
echo "  Password: admin123\n";
echo "\n";
echo "Option 2:\n";
echo "  Email:    admin@admin.com\n";
echo "  Password: admin\n";
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n";

echo "Login URL: http://127.0.0.1:8001/login\n";
echo "(Pastikan server Laravel sedang berjalan)\n";
