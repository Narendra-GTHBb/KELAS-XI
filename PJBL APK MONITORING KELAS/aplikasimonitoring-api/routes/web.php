<?php

use Illuminate\Support\Facades\Route;
use Symfony\Component\HttpFoundation\StreamedResponse;

Route::get('/', function () {
    return view('welcome');
});

// Download CSV Templates
Route::get('/download-template/{type}', function ($type) {
    $templates = [
        'guru' => [
            'filename' => 'template_guru.csv',
            'headers' => ['kode_guru', 'guru', 'telepon'],
            'examples' => [
                ['G001', 'Budi Setiawan', '081234567890'],
                ['G002', 'Siti Rahayu', '081234567891'],
            ],
        ],
        'mapel' => [
            'filename' => 'template_mapel.csv',
            'headers' => ['kode_mapel', 'mapel'],
            'examples' => [
                ['M001', 'Matematika'],
                ['M002', 'Bahasa Indonesia'],
            ],
        ],
        'kelas' => [
            'filename' => 'template_kelas.csv',
            'headers' => ['kelas'],
            'examples' => [
                ['X RPL'],
                ['XI RPL'],
                ['XII RPL'],
            ],
        ],
        'jadwal' => [
            'filename' => 'template_jadwal.csv',
            'headers' => ['hari', 'jam_ke', 'guru', 'mapel', 'kelas'],
            'examples' => [
                ['Senin', 'Jam Ke 1-2', 'G001', 'M001', 'X RPL'],
                ['Senin', 'Jam Ke 3-4', 'G002', 'M002', 'X RPL'],
            ],
        ],
        'user' => [
            'filename' => 'template_user.csv',
            'headers' => ['nama', 'username', 'email', 'role', 'password'],
            'examples' => [
                ['John Doe', 'johndoe', 'john@email.com', 'Siswa', '123'],
                ['Jane Doe', 'janedoe', 'jane@email.com', 'Guru', '123'],
            ],
        ],
    ];

    if (!isset($templates[$type])) {
        abort(404, 'Template tidak ditemukan');
    }

    $template = $templates[$type];

    return new StreamedResponse(function () use ($template) {
        $handle = fopen('php://output', 'w');
        
        // BOM untuk UTF-8
        fprintf($handle, chr(0xEF) . chr(0xBB) . chr(0xBF));
        
        // Header
        fputcsv($handle, $template['headers']);
        
        // Examples
        foreach ($template['examples'] as $example) {
            fputcsv($handle, $example);
        }
        
        fclose($handle);
    }, 200, [
        'Content-Type' => 'text/csv; charset=UTF-8',
        'Content-Disposition' => 'attachment; filename="' . $template['filename'] . '"',
    ]);
})->name('download.template');
