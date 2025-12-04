<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Guru;
use App\Models\GuruIzin;
use App\Models\GuruPengganti;
use App\Models\Jadwal;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Validator;

class GuruController extends Controller
{
    /**
     * Get jadwal for the logged-in guru
     * GET /api/guru/jadwal
     */
    public function jadwal(Request $request)
    {
        $user = Auth::user();
        
        if (!$user->guru_id) {
            return response()->json([
                'success' => false,
                'message' => 'User tidak terhubung dengan data guru'
            ], 400);
        }

        $hari = $request->query('hari'); // Optional filter by day
        
        $jadwals = Jadwal::with(['guruMengajar.guru', 'guruMengajar.mapel', 'guruMengajar.kelas'])
            ->whereHas('guruMengajar', function ($q) use ($user) {
                $q->where('guru_id', $user->guru_id);
            })
            ->when($hari, function ($q) use ($hari) {
                $q->where('hari', $hari);
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
     * Create izin request
     * POST /api/guru/izin
     */
    public function createIzin(Request $request)
    {
        $user = Auth::user();
        
        if (!$user->guru_id) {
            return response()->json([
                'success' => false,
                'message' => 'User tidak terhubung dengan data guru'
            ], 400);
        }

        $validator = Validator::make($request->all(), [
            'tanggal' => 'required|date',
            'jenis_izin' => 'required|in:sakit,izin,cuti,dinas_luar,lainnya',
            'keterangan' => 'nullable|string'
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validasi gagal',
                'errors' => $validator->errors()
            ], 422);
        }

        $izin = GuruIzin::create([
            'guru_id' => $user->guru_id,
            'tanggal' => $request->tanggal,
            'jenis_izin' => $request->jenis_izin,
            'keterangan' => $request->keterangan,
            'status' => 'pending'
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Permohonan izin berhasil dibuat',
            'data' => $izin->load('guru')
        ], 201);
    }

    /**
     * Get izin history for the logged-in guru
     * GET /api/guru/izin
     */
    public function getIzin(Request $request)
    {
        $user = Auth::user();
        
        if (!$user->guru_id) {
            return response()->json([
                'success' => false,
                'message' => 'User tidak terhubung dengan data guru'
            ], 400);
        }

        $izins = GuruIzin::with('approvedBy')
            ->where('guru_id', $user->guru_id)
            ->orderBy('tanggal', 'desc')
            ->get();

        return response()->json([
            'success' => true,
            'data' => $izins
        ]);
    }

    /**
     * Get list of replacement teachers for guru (who replaces this guru)
     * GET /api/guru/pengganti
     */
    public function getPengganti(Request $request)
    {
        $user = Auth::user();
        
        if (!$user->guru_id) {
            return response()->json([
                'success' => false,
                'message' => 'User tidak terhubung dengan data guru'
            ], 400);
        }

        $tanggal = $request->query('tanggal', now()->toDateString());

        $pengganti = GuruPengganti::with(['jadwal.mapel', 'jadwal.kelas', 'jadwal.guru', 'guruAsli', 'guruPengganti'])
            ->where('guru_asli_id', $user->guru_id)
            ->when($tanggal, function ($q) use ($tanggal) {
                $q->whereDate('tanggal', $tanggal);
            })
            ->orderBy('tanggal', 'desc')
            ->get();

        return response()->json([
            'success' => true,
            'data' => $pengganti
        ]);
    }

    /**
     * Get profile of logged-in guru
     * GET /api/guru/profile
     */
    public function profile()
    {
        $user = Auth::user();
        
        if (!$user->guru_id) {
            return response()->json([
                'success' => false,
                'message' => 'User tidak terhubung dengan data guru'
            ], 400);
        }

        $guru = Guru::with(['guruMengajar.mapel', 'guruMengajar.kelas'])
            ->find($user->guru_id);

        return response()->json([
            'success' => true,
            'data' => [
                'user' => $user,
                'guru' => $guru
            ]
        ]);
    }
}
