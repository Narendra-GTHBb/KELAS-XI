<?php

require __DIR__.'/vendor/autoload.php';

$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use App\Models\User;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;

echo "=== LOGIN TEST ===\n\n";

$admin = User::where('email', 'admin@musclecart.com')->first();

echo "1. User found: " . ($admin ? 'YES' : 'NO') . "\n";
echo "2. User role: " . ($admin->role ?? 'N/A') . "\n";
echo "3. Password column value exists: " . (!empty($admin->password) ? 'YES' : 'NO') . "\n";
echo "4. Password hash starts with: " . substr($admin->password ?? '', 0, 7) . "\n";
echo "5. Hash check (admin123): " . (Hash::check('admin123', $admin->password) ? 'PASS' : 'FAIL') . "\n";
echo "6. Auth::attempt test: ";

$result = Auth::attempt(['email' => 'admin@musclecart.com', 'password' => 'admin123']);
echo ($result ? 'SUCCESS' : 'FAIL') . "\n";

if (!$result) {
    echo "\n--- DEBUGGING ---\n";
    echo "getAuthPassword(): " . $admin->getAuthPassword() . "\n";
    echo "password attribute: " . ($admin->password ?? 'NULL') . "\n";
    
    // Check all columns
    echo "\nAll user attributes:\n";
    foreach ($admin->getAttributes() as $key => $value) {
        if ($key === 'password') {
            echo "  $key: " . substr($value, 0, 30) . "...\n";
        } else {
            echo "  $key: $value\n";
        }
    }
}
