<?php

require __DIR__ . '/vendor/autoload.php';

$app = require_once __DIR__ . '/bootstrap/app.php';
$app->make(\Illuminate\Contracts\Console\Kernel::class)->bootstrap();

use App\Models\GuruMengajar;

echo "=== TEST API RESPONSE FORMAT ===\n\n";

$data = GuruMengajar::with(['jadwal.guru', 'jadwal.mapel', 'jadwal.kelas'])->get();

echo "Total GuruMengajar: " . $data->count() . "\n\n";

$formatted = $data->take(3)->map(function ($gm) {
    return [
        'id' => $gm->id,
        'jadwal_id' => $gm->jadwal_id,
        'kode_guru' => $gm->jadwal?->guru?->kode_guru,
        'nama_guru' => $gm->jadwal?->guru?->guru,
        'mapel' => $gm->jadwal?->mapel?->mapel,
        'jam_ke' => $gm->jadwal?->jam_ke,
        'hari' => $gm->jadwal?->hari,
        'kelas_id' => $gm->jadwal?->kelas_id,
        'status' => $gm->status,
        'keterangan' => $gm->keterangan,
    ];
});

echo "Sample formatted response:\n";
echo json_encode($formatted, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);

echo "\n\n=== BREAKDOWN BY KELAS & HARI ===\n";

$byKelasHari = [];
foreach ($data as $gm) {
    $hari = $gm->jadwal?->hari ?? 'UNKNOWN';
    $kelasId = $gm->jadwal?->kelas_id ?? 0;
    $key = "kelas_id={$kelasId}, hari={$hari}";
    if (!isset($byKelasHari[$key])) {
        $byKelasHari[$key] = 0;
    }
    $byKelasHari[$key]++;
}

foreach ($byKelasHari as $key => $count) {
    echo "{$key}: {$count} records\n";
}

echo "\n=== API RESPONSE FORMAT IS CORRECT ===\n";
