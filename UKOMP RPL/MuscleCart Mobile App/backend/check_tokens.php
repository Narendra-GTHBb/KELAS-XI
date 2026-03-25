<?php
require __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';
$kernel = $app->make(Illuminate\Contracts\Http\Kernel::class);
$kernel->bootstrap();

use Laravel\Sanctum\PersonalAccessToken;
use App\Models\User;

// Check personal access tokens
$tokensCount = PersonalAccessToken::count();
echo "Number of tokens: {$tokensCount}\n";

$latestToken = PersonalAccessToken::latest()->first();
if ($latestToken) {
    echo "Latest token ID: {$latestToken->id}\n";
    echo "Token name: {$latestToken->name}\n";
    echo "Tokenable type: {$latestToken->tokenable_type}\n";
    echo "Tokenable ID: {$latestToken->tokenable_id}\n";
    
    // Verify the token works
    $user = $latestToken->tokenable;
    if ($user) {
        echo "User found: {$user->name} ({$user->email})\n";
    } else {
        echo "No user found for token!\n";
    }
}

// Test token manually
$testTokenValue = "z53oaH6leLTH8Bsk75xMmh76yfBBAASKjNdXd0vDea0f193f";
$tokenRecord = PersonalAccessToken::findToken($testTokenValue);
if ($tokenRecord) {
    echo "\nToken validation: SUCCESS\n";
    echo "Token belongs to user ID: {$tokenRecord->tokenable_id}\n";
} else {
    echo "\nToken validation: FAILED - Token not found in database\n";
}
