<?php

require __DIR__ . '/vendor/autoload.php';
$app = require_once __DIR__ . '/bootstrap/app.php';
$app->make('Illuminate\Contracts\Console\Kernel')->bootstrap();

use App\Models\Mapel;

// Mapping nama mapel ke singkatan
$singkatan = [
    'Bahasa Indonesia' => 'BIN',
    'Matematika' => 'MTK',
    'IPA' => 'IPA',
    'IPS' => 'IPS',
    'Bahasa Inggris' => 'BING',
    'Pemrograman Web' => 'PW',
    'Pemrograman Mobile' => 'PM',
    'Basis Data' => 'BD',
];

$mapels = Mapel::all();

foreach ($mapels as $mapel) {
    if (isset($singkatan[$mapel->mapel])) {
        $newKode = $singkatan[$mapel->mapel];
        $mapel->update(['kode_mapel' => $newKode]);
        echo "{$mapel->mapel} -> {$newKode}\n";
    } else {
        // Buat singkatan otomatis dari huruf awal setiap kata
        $words = explode(' ', $mapel->mapel);
        $newKode = '';
        foreach ($words as $word) {
            $newKode .= strtoupper(substr($word, 0, 1));
        }
        $mapel->update(['kode_mapel' => $newKode]);
        echo "{$mapel->mapel} -> {$newKode} (auto)\n";
    }
}

echo "\nSelesai!\n";
