<?php
require 'vendor/autoload.php';
$app = require 'bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

echo "=== DATA DI DATABASE ===\n\n";

echo "Guru:\n";
foreach(App\Models\Guru::all() as $guru) {
    echo "- ID: {$guru->id}, Nama: {$guru->guru}\n";
}

echo "\nMapel:\n";
foreach(App\Models\Mapel::all() as $mapel) {
    echo "- ID: {$mapel->id}, Nama: {$mapel->mapel}\n";
}

echo "\nKelas:\n";
foreach(App\Models\Kelas::all() as $kelas) {
    echo "- ID: {$kelas->id}, Nama: {$kelas->kelas}\n";
}

echo "\nTahun Ajaran:\n";
foreach(App\Models\TahunAjaran::all() as $ta) {
    echo "- ID: {$ta->id}, Tahun: {$ta->tahun}\n";
}
