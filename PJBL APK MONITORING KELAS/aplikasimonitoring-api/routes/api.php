<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\GuruController;
use App\Http\Controllers\MapelController;
use App\Http\Controllers\TahunAjaranController;
use App\Http\Controllers\KelasController;
use App\Http\Controllers\UserController;
use App\Http\Controllers\JadwalController;
use App\Http\Controllers\GuruMengajarController;
use App\Http\Controllers\Api\GuruController as ApiGuruController;
use App\Http\Controllers\Api\SiswaController as ApiSiswaController;
use App\Http\Controllers\Api\KurikulumController as ApiKurikulumController;
use App\Http\Controllers\Api\KepsekController as ApiKepsekController;

/*
|--------------------------------------------------------------------------
| Public Routes (tanpa authentication)
|--------------------------------------------------------------------------
*/
Route::post('/login', [AuthController::class, 'login']);

/*
|--------------------------------------------------------------------------
| Protected Routes (perlu token Sanctum)
|--------------------------------------------------------------------------
*/
Route::middleware('auth:sanctum')->group(function () {
    // Auth routes
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::get('/me', [AuthController::class, 'me']);
    
    // Resource routes
    Route::apiResource('gurus', GuruController::class);
    Route::apiResource('mapels', MapelController::class);
    Route::apiResource('tahun-ajarans', TahunAjaranController::class);
    Route::apiResource('kelas', KelasController::class);
    Route::apiResource('users', UserController::class);
    Route::apiResource('jadwals', JadwalController::class);
    Route::apiResource('guru-mengajars', GuruMengajarController::class);
    
    // Custom routes
    Route::get('jadwal/kelas/{kelas_id}/{hari}', [JadwalController::class, 'getByClassAndDay']);
    Route::post('/guru-mengajar/by-hari-kelas', [GuruMengajarController::class, 'getByHariKelas']);
    Route::post('/guru-mengajar/tidak-masuk', [GuruMengajarController::class, 'getTidakMasukByHariKelas']);
    Route::patch('/guru-mengajars/{guruMengajar}/status', [GuruMengajarController::class, 'updateStatus']);
    Route::get('/guru-mengajar/by-jadwal/{jadwal_id}', [GuruMengajarController::class, 'getByJadwalId']);
    Route::get('/guru-mengajar', [GuruMengajarController::class, 'getWithFilter']);
    
    // Test route
    Route::get('/test-guru-data', function() {
        $gm = \App\Models\GuruMengajar::with(['jadwal.guru', 'jadwal.mapel'])->first();
        if (!$gm) return response()->json(['error' => 'No data']);
        return response()->json([
            'id' => $gm->id,
            'kode_guru' => $gm->jadwal?->guru?->kode_guru,
            'nama_guru' => $gm->jadwal?->guru?->guru,
            'mapel' => $gm->jadwal?->mapel?->mapel,
            'jam_ke' => $gm->jadwal?->jam_ke,
            'raw_guru' => $gm->jadwal?->guru,
        ]);
    });

    /*
    |--------------------------------------------------------------------------
    | Role-based API Routes
    |--------------------------------------------------------------------------
    */

    // Routes untuk role GURU
    Route::prefix('guru')->group(function () {
        Route::get('/profile', [ApiGuruController::class, 'profile']);
        Route::get('/jadwal', [ApiGuruController::class, 'jadwal']);
        Route::get('/izin', [ApiGuruController::class, 'getIzin']);
        Route::post('/izin', [ApiGuruController::class, 'createIzin']);
        Route::get('/pengganti', [ApiGuruController::class, 'getPengganti']);
    });

    // Routes untuk role SISWA
    Route::prefix('siswa')->group(function () {
        Route::get('/jadwal', [ApiSiswaController::class, 'jadwal']);
        Route::post('/kehadiran-guru', [ApiSiswaController::class, 'laporKehadiranGuru']);
        Route::get('/kehadiran-siswa', [ApiSiswaController::class, 'getKehadiranSiswa']);
        Route::post('/kehadiran-siswa', [ApiSiswaController::class, 'laporKehadiranSiswa']);
        Route::get('/guru-pengganti', [ApiSiswaController::class, 'getGuruPengganti']);
    });

    // Routes untuk role KURIKULUM
    Route::prefix('kurikulum')->group(function () {
        Route::get('/guru-izin', [ApiKurikulumController::class, 'getGuruIzin']);
        Route::put('/guru-izin/{id}', [ApiKurikulumController::class, 'updateGuruIzin']);
        Route::patch('/guru-izin/{id}/approve', [ApiKurikulumController::class, 'approveGuruIzin']);
        Route::patch('/guru-izin/{id}/reject', [ApiKurikulumController::class, 'rejectGuruIzin']);
        Route::get('/guru-pengganti', [ApiKurikulumController::class, 'getGuruPengganti']);
        Route::post('/guru-pengganti', [ApiKurikulumController::class, 'createGuruPengganti']);
        Route::get('/kehadiran-guru', [ApiKurikulumController::class, 'getKehadiranGuru']);
        Route::get('/gurus', [ApiKurikulumController::class, 'getGurus']);
        Route::get('/jadwal', [ApiKurikulumController::class, 'getJadwal']);
    });

    // Routes untuk role KEPSEK
    Route::prefix('kepsek')->group(function () {
        Route::get('/dashboard', [ApiKepsekController::class, 'dashboard']);
        Route::get('/jadwal', [ApiKepsekController::class, 'getJadwal']);
        Route::get('/kehadiran-guru', [ApiKepsekController::class, 'getKehadiranGuru']);
        Route::get('/guru-pengganti', [ApiKepsekController::class, 'getGuruPengganti']);
        Route::get('/kehadiran-siswa', [ApiKepsekController::class, 'getKehadiranSiswa']);
        Route::get('/guru-izin', [ApiKepsekController::class, 'getGuruIzin']);
    });
});