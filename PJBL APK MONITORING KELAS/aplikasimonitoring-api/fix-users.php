<?php

require __DIR__.'/vendor/autoload.php';

$app = require_once __DIR__.'/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

// Update all users with NULL nama
$updated = DB::table('users')
    ->whereNull('nama')
    ->update(['nama' => DB::raw('email')]);

echo "Updated {$updated} users with NULL nama\n";

// Show all users
$users = DB::table('users')->select('id', 'nama', 'email', 'role')->get();
echo "\nAll users:\n";
foreach ($users as $user) {
    echo "ID: {$user->id} | Nama: {$user->nama} | Email: {$user->email} | Role: {$user->role}\n";
}
