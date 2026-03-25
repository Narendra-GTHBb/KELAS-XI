<?php
// Direct API test - simulate cart endpoint
require __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';
$kernel = $app->make(Illuminate\Contracts\Http\Kernel::class);

// Create request
$request = Illuminate\Http\Request::create('/api/v1/cart', 'GET', [], [], [], [
    'HTTP_AUTHORIZATION' => 'Bearer 1|z53oaH6leLTH8Bsk75xMmh76yfBBAASKjNdXd0vDea0f193f',
    'HTTP_ACCEPT' => 'application/json',
]);

$response = $kernel->handle($request);

echo "Status: " . $response->getStatusCode() . "\n";
echo "Content: " . $response->getContent() . "\n";

$kernel->terminate($request, $response);
