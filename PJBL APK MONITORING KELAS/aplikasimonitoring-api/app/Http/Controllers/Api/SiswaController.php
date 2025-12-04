<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\GuruMengajar;
use App\Models\GuruPengganti;
use App\Models\Jadwal;
use App\Models\KehadiranSiswa;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Validator;

class SiswaController extends Controller
{
    /**
     * Get jadwal for the logged-in siswa's kelas
     * GET /api/siswa/jadwal
     */
    public function jadwal(Request $request)
    {
        $user = Auth::user();
        
        if (!$user->kelas_id) {
            return response()->json([
                'success' => false,
                'message' => 'User tidak terhubung dengan data kelas'
            ], 400);
        }

        $hari = $request->query('hari'); // Optional filter by day
        
        // Query dari GuruMengajar dengan relasi jadwal (yang punya guru, mapel, kelas)
        $guruMengajars = GuruMengajar::with(['jadwal.guru', 'jadwal.mapel', 'jadwal.kelas', 'guruPengganti'])
            ->whereHas('jadwal', function ($q) use ($user, $hari) {
                $q->where('kelas_id', $user->kelas_id);
                if ($hari) {
                    $q->where('hari', $hari);
                }
            })
            ->get()
            ->sortBy(function ($gm) {
                $hariOrder = ['Senin' => 1, 'Selasa' => 2, 'Rabu' => 3, 'Kamis' => 4, 'Jumat' => 5, 'Sabtu' => 6];
                return ($hariOrder[$gm->jadwal?->hari] ?? 99) * 100 + intval($gm->jadwal?->jam_ke ?? 0);
            })
            ->values();

        // Format response sesuai dengan GuruMengajarResponse di Android
        $data = $guruMengajars->map(function ($gm) {
            return [
                'id' => $gm->id,
                'jadwal_id' => $gm->jadwal_id,
                'kode_guru' => $gm->jadwal?->guru?->kode_guru ?? '',
                'nama_guru' => $gm->jadwal?->guru?->guru ?? '',
                'mapel' => $gm->jadwal?->mapel?->mapel ?? '',
                'jam_ke' => $gm->jadwal?->jam_ke ?? '',
                'hari' => $gm->jadwal?->hari ?? '',
                'kelas_id' => $gm->jadwal?->kelas_id ?? 0,
                'guru_id' => $gm->jadwal?->guru_id ?? 0,
                'mapel_id' => $gm->jadwal?->mapel_id ?? 0,
                'keterangan' => $gm->keterangan ?? '',
                'status' => $gm->status ?? '',
                'guru_pengganti_id' => $gm->guru_pengganti_id,
                'guru_pengganti' => $gm->guruPengganti ? [
                    'id' => $gm->guruPengganti->id,
                    'kode_guru' => $gm->guruPengganti->kode_guru,
                    'guru' => $gm->guruPengganti->guru,
                ] : null,
                'tanggal_mulai_izin' => $gm->tanggal_mulai_izin?->format('Y-m-d'),
                'tanggal_selesai_izin' => $gm->tanggal_selesai_izin?->format('Y-m-d'),
                'durasi_izin' => $gm->durasi_izin,
            ];
        });

        return response()->json([
            'success' => true,
            'data' => $data
        ]);
    }

    /**
     * Report kehadiran guru (guru hadir/tidak hadir)
     * POST /api/siswa/kehadiran-guru
     */
    public function laporKehadiranGuru(Request $request)
    {
        $user = Auth::user();
        
        if (!$user->kelas_id) {
            return response()->json([
                'success' => false,
                'message' => 'User tidak terhubung dengan data kelas'
            ], 400);
        }

        $validator = Validator::make($request->all(), [
            'jadwal_id' => 'required|exists:jadwals,id',
            'status' => 'required|in:hadir,tidak_hadir',
            'keterangan' => 'nullable|string'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validasi gagal',
                'errors' => $validator->errors()
            ], 422);
        }

        // TODO: Implement actual kehadiran guru tracking
        // For now just return success
        return response()->json([
            'success' => true,
            'message' => 'Laporan kehadiran guru berhasil dikirim'
        ]);
    }

    /**
     * Report kehadiran siswa (siswa tidak hadir)
     * POST /api/siswa/kehadiran-siswa
     */
    public function laporKehadiranSiswa(Request $request)
    {
        $user = Auth::user();
        
        if (!$user->kelas_id) {
            return response()->json([
                'success' => false,
                'message' => 'User tidak terhubung dengan data kelas'
            ], 400);
        }

        $validator = Validator::make($request->all(), [
            'jadwal_id' => 'required|exists:jadwals,id',
            'jumlah_hadir' => 'required|integer|min:0',
            'jumlah_sakit' => 'required|integer|min:0',
            'jumlah_izin' => 'required|integer|min:0',
            'jumlah_alpha' => 'required|integer|min:0',
            'keterangan' => 'nullable|string'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validasi gagal',
                'errors' => $validator->errors()
            ], 422);
        }

        $kehadiran = KehadiranSiswa::updateOrCreate(
            [
                'jadwal_id' => $request->jadwal_id,
                'kelas_id' => $user->kelas_id,
                'tanggal' => now()->toDateString()
            ],
            [
                'jumlah_hadir' => $request->jumlah_hadir,
                'jumlah_sakit' => $request->jumlah_sakit,
                'jumlah_izin' => $request->jumlah_izin,
                'jumlah_alpha' => $request->jumlah_alpha,
                'keterangan' => $request->keterangan,
                'reported_by' => $user->id
            ]
        );

        return response()->json([
            'success' => true,
            'message' => 'Laporan kehadiran siswa berhasil disimpan',
            'data' => $kehadiran
        ]);
    }

    /**
     * Get kehadiran siswa for siswa's kelas
     * GET /api/siswa/kehadiran-siswa
     */
    public function getKehadiranSiswa(Request $request)
    {
        $user = Auth::user();
        
        if (!$user->kelas_id) {
            return response()->json([
                'success' => false,
                'message' => 'User tidak terhubung dengan data kelas'
            ], 400);
        }

        $tanggal = $request->query('tanggal', now()->toDateString());

        $kehadiran = KehadiranSiswa::with(['kelas', 'jadwal.mapel', 'jadwal.guru'])
            ->where('kelas_id', $user->kelas_id)
            ->when($tanggal, function ($q) use ($tanggal) {
                $q->whereDate('tanggal', $tanggal);
            })
            ->orderBy('tanggal', 'desc')
            ->get();

        return response()->json([
            'success' => true,
            'data' => $kehadiran
        ]);
    }

    /**
     * Get guru pengganti for siswa's kelas
     * GET /api/siswa/guru-pengganti
     * 
     * Mengambil data dari guru_mengajars yang sudah memiliki guru_pengganti_id
     * (Data guru pengganti yang sudah ditentukan oleh Kurikulum)
     */
    public function getGuruPengganti(Request $request)
    {
        $user = Auth::user();
        
        if (!$user->kelas_id) {
            return response()->json([
                'success' => false,
                'message' => 'User tidak terhubung dengan data kelas'
            ], 400);
        }

        $hari = $request->query('hari');

        // Ambil data guru_mengajar yang SUDAH memiliki guru pengganti untuk kelas siswa
        $data = \App\Models\GuruMengajar::with(['jadwal.guru', 'jadwal.mapel', 'jadwal.kelas', 'guruPengganti'])
            ->whereNotNull('guru_pengganti_id')
            ->whereHas('jadwal', function ($q) use ($user) {
                $q->where('kelas_id', $user->kelas_id);
            })
            ->when($hari, function ($q) use ($hari) {
                $q->whereHas('jadwal', function ($j) use ($hari) {
                    $j->where('hari', $hari);
                });
            })
            ->get()
            ->map(function ($gm) {
                return [
                    'id' => $gm->id,
                    'jadwal_id' => $gm->jadwal_id,
                    'hari' => $gm->jadwal?->hari,
                    'jam_ke' => $gm->jadwal?->jam_ke,
                    'kelas' => $gm->jadwal?->kelas?->kelas,
                    'mapel' => $gm->jadwal?->mapel?->mapel,
                    'guru_asli' => [
                        'id' => $gm->jadwal?->guru?->id,
                        'nama' => $gm->jadwal?->guru?->guru,
                        'kode' => $gm->jadwal?->guru?->kode_guru,
                    ],
                    'guru_pengganti' => [
                        'id' => $gm->guruPengganti?->id,
                        'nama' => $gm->guruPengganti?->guru,
                        'kode' => $gm->guruPengganti?->kode_guru,
                    ],
                    'status' => $gm->status,
                    'keterangan' => $gm->keterangan,
                ];
            });

        return response()->json([
            'success' => true,
            'data' => $data
        ]);
    }
}
