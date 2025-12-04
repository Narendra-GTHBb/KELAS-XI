<?php

require __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use App\Models\User;
use Illuminate\Support\Facades\Hash;

// Update password untuk siswa
$siswa = User::where('username', 'siswa')->first();
if ($siswa) {
    $siswa->password = Hash::make('123');
    $siswa->save();
    echo "Siswa (username: siswa) - Password updated to 123\n";
} else {
    // Create if not exists
    User::create([
        'nama' => 'Siswa',
        'username' => 'siswa',
        'email' => 'siswa@sekolah.com',
        'password' => Hash::make('123'),
        'role' => 'Siswa',
    ]);
    echo "Siswa (username: siswa) - Created with password 123\n";
}

// Update password untuk kepsek
$kepsek = User::where('username', 'kepsek')->first();
if ($kepsek) {
    $kepsek->password = Hash::make('123');
    $kepsek->save();
    echo "Kepala Sekolah (username: kepsek) - Password updated to 123\n";
} else {
    User::create([
        'nama' => 'Kepala Sekolah',
        'username' => 'kepsek',
        'email' => 'kepsek@sekolah.com',
        'password' => Hash::make('123'),
        'role' => 'Kepala Sekolah',
    ]);
    echo "Kepala Sekolah (username: kepsek) - Created with password 123\n";
}

// Update password untuk kurikulum
$kurikulum = User::where('username', 'kurikulum')->first();
if ($kurikulum) {
    $kurikulum->password = Hash::make('123');
    $kurikulum->save();
    echo "Kurikulum (username: kurikulum) - Password updated to 123\n";
} else {
    User::create([
        'nama' => 'Kurikulum',
        'username' => 'kurikulum',
        'email' => 'kurikulum@sekolah.com',
        'password' => Hash::make('123'),
        'role' => 'Kurikulum',
    ]);
    echo "Kurikulum (username: kurikulum) - Created with password 123\n";
}

echo "\n=== DONE ===\n";
echo "Login credentials:\n";
echo "- Siswa: username=siswa, password=123\n";
echo "- Kepala Sekolah: username=kepsek, password=123\n";
echo "- Kurikulum: username=kurikulum, password=123\n";
