<?php

require __DIR__.'/vendor/autoload.php';

$app = require_once __DIR__.'/bootstrap/app.php';
$kernel = $app->make(Illuminate\Contracts\Http\Kernel::class);

// Test DB connection
try {
    Illuminate\Support\Facades\DB::connection()->getPdo();
    echo "✓ MySQL Connected\n\n";
} catch (Exception $e) {
    echo "✗ MySQL Error: " . $e->getMessage() . "\n";
    exit(1);
}

// Get test user token
$user = App\Models\User::where('email', 'test@test.com')->first();

if (!$user) {
    echo "✗ Test user not found. Creating...\n";
    $user = App\Models\User::create([
        'name' => 'Test User',
        'email' => 'test@test.com',
        'password' => bcrypt('password123'),
    ]);
}

$token = $user->createToken('test-token')->plainTextToken;

echo "✓ Test User: {$user->email}\n";
echo "✓ Token: {$token}\n\n";

// Test toggle favorite endpoint
$productId = 1;

echo "Testing POST /api/v1/favorites/{$productId}/toggle\n";
echo str_repeat('-', 50) . "\n";

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, "http://127.0.0.1:8000/api/v1/favorites/{$productId}/toggle");
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, [
    'Authorization: Bearer ' . $token,
    'Accept: application/json',
]);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

$response = curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

echo "HTTP Code: {$httpCode}\n";
echo "Response: {$response}\n\n";

if ($httpCode === 200) {
    $data = json_decode($response, true);
    if ($data['status'] === 'success') {
        echo "✓ Toggle SUCCESS - is_favorite: " . ($data['data']['is_favorite'] ? 'true' : 'false') . "\n";
    } else {
        echo "✗ API returned error: " . $data['message'] . "\n";
    }
} else {
    echo "✗ HTTP Error {$httpCode}\n";
}
