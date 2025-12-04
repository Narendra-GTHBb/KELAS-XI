<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Guru;
use App\Models\GuruIzin;
use App\Models\GuruPengganti;
use App\Models\Jadwal;
use App\Models\KehadiranSiswa;
use App\Models\Kelas;
use Illuminate\Http\Request;

class KepsekController extends Controller
{
    /**
     * Get all jadwal
     * GET /api/kepsek/jadwal
     */
    public function getJadwal(Request $request)
    {
        $hari = $request->query('hari');
        $kelasId = $request->query('kelas_id');
        $guruId = $request->query('guru_id');

        $jadwals = Jadwal::with(['guruMengajar.guru', 'guruMengajar.mapel', 'guruMengajar.kelas'])
            ->when($hari, function ($q) use ($hari) {
                $q->where('hari', $hari);
            })
            ->when($kelasId, function ($q) use ($kelasId) {
                $q->whereHas('guruMengajar', function ($q2) use ($kelasId) {
                    $q2->where('kelas_id', $kelasId);
                });
            })
            ->when($guruId, function ($q) use ($guruId) {
                $q->whereHas('guruMengajar', function ($q2) use ($guruId) {
                    $q2->where('guru_id', $guruId);
                });
            })
            ->orderByRaw("FIELD(hari, 'Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu')")
            ->orderBy('jam_mulai')
            ->get();

        return response()->json([
            'success' => true,
            'data' => $jadwals
        ]);
    }

    /**
     * Get kehadiran guru summary
     * GET /api/kepsek/kehadiran-guru
     */
    public function getKehadiranGuru(Request $request)
    {
        $tanggal = $request->query('tanggal', now()->toDateString());

        $guruIzin = GuruIzin::with('guru')
            ->whereDate('tanggal', $tanggal)
            ->get();

        $guruHadir = Guru::whereNotIn('id', $guruIzin->pluck('guru_id'))
            ->get(['id', 'kode_guru', 'guru']);

        return response()->json([
            'success' => true,
            'data' => [
                'tanggal' => $tanggal,
                'total_guru' => Guru::count(),
                'guru_hadir' => [
                    'count' => $guruHadir->count(),
                    'data' => $guruHadir
                ],
                'guru_izin' => [
                    'count' => $guruIzin->count(),
                    'data' => $guruIzin
                ]
            ]
        ]);
    }

    /**
     * Get guru pengganti list
     * GET /api/kepsek/guru-pengganti
     */
    public function getGuruPengganti(Request $request)
    {
        $tanggal = $request->query('tanggal');
        $startDate = $request->query('start_date');
        $endDate = $request->query('end_date');

        $pengganti = GuruPengganti::with(['jadwal.mapel', 'jadwal.kelas', 'jadwal.guru', 'guruAsli', 'guruPengganti', 'guruIzin'])
            ->when($tanggal, function ($q) use ($tanggal) {
                $q->whereDate('tanggal', $tanggal);
            })
            ->when($startDate && $endDate, function ($q) use ($startDate, $endDate) {
                $q->whereBetween('tanggal', [$startDate, $endDate]);
            })
            ->orderBy('tanggal', 'desc')
            ->get();

        return response()->json([
            'success' => true,
            'data' => $pengganti
        ]);
    }

    /**
     * Get kehadiran siswa summary
     * GET /api/kepsek/kehadiran-siswa
     */
    public function getKehadiranSiswa(Request $request)
    {
        $tanggal = $request->query('tanggal', now()->toDateString());
        $kelasId = $request->query('kelas_id');

        $kehadiran = KehadiranSiswa::with(['kelas', 'jadwal.mapel', 'jadwal.guru', 'reportedBy'])
            ->whereDate('tanggal', $tanggal)
            ->when($kelasId, function ($q) use ($kelasId) {
                $q->where('kelas_id', $kelasId);
            })
            ->get();

        // Calculate summary
        $summary = [
            'total_hadir' => $kehadiran->sum('jumlah_hadir'),
            'total_sakit' => $kehadiran->sum('jumlah_sakit'),
            'total_izin' => $kehadiran->sum('jumlah_izin'),
            'total_alpha' => $kehadiran->sum('jumlah_alpha'),
        ];
        $summary['total_siswa'] = $summary['total_hadir'] + $summary['total_sakit'] + $summary['total_izin'] + $summary['total_alpha'];
        $summary['persentase_hadir'] = $summary['total_siswa'] > 0 
            ? round(($summary['total_hadir'] / $summary['total_siswa']) * 100, 2) 
            : 0;

        return response()->json([
            'success' => true,
            'data' => [
                'tanggal' => $tanggal,
                'summary' => $summary,
                'detail' => $kehadiran
            ]
        ]);
    }

    /**
     * Get guru izin list
     * GET /api/kepsek/guru-izin
     */
    public function getGuruIzin(Request $request)
    {
        $status = $request->query('status');
        $startDate = $request->query('start_date');
        $endDate = $request->query('end_date');

        $izins = GuruIzin::with(['guru', 'approvedBy'])
            ->when($status, function ($q) use ($status) {
                $q->where('status', $status);
            })
            ->when($startDate && $endDate, function ($q) use ($startDate, $endDate) {
                $q->whereBetween('tanggal', [$startDate, $endDate]);
            })
            ->orderBy('tanggal', 'desc')
            ->get();

        return response()->json([
            'success' => true,
            'data' => $izins
        ]);
    }

    /**
     * Get dashboard summary
     * GET /api/kepsek/dashboard
     */
    public function dashboard()
    {
        $today = now()->toDateString();

        // Guru statistics
        $totalGuru = Guru::count();
        $guruIzinToday = GuruIzin::whereDate('tanggal', $today)->where('status', 'disetujui')->count();
        
        // Kelas statistics
        $totalKelas = Kelas::count();
        
        // Kehadiran siswa today
        $kehadiranSiswa = KehadiranSiswa::whereDate('tanggal', $today)->get();
        $siswaSummary = [
            'total_hadir' => $kehadiranSiswa->sum('jumlah_hadir'),
            'total_tidak_hadir' => $kehadiranSiswa->sum('jumlah_sakit') + $kehadiranSiswa->sum('jumlah_izin') + $kehadiranSiswa->sum('jumlah_alpha'),
        ];

        // Guru pengganti today
        $guruPenggantiToday = GuruPengganti::whereDate('tanggal', $today)->count();

        // Pending izin
        $pendingIzin = GuruIzin::where('status', 'pending')->count();

        return response()->json([
            'success' => true,
            'data' => [
                'tanggal' => $today,
                'guru' => [
                    'total' => $totalGuru,
                    'hadir' => $totalGuru - $guruIzinToday,
                    'izin' => $guruIzinToday
                ],
                'kelas' => [
                    'total' => $totalKelas
                ],
                'siswa' => $siswaSummary,
                'guru_pengganti' => $guruPenggantiToday,
                'pending_izin' => $pendingIzin
            ]
        ]);
    }
}
