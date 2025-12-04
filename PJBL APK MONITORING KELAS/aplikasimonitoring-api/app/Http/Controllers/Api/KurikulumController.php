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

class KurikulumController extends Controller
{
    /**
     * Get list of guru izin (pending)
     * GET /api/kurikulum/guru-izin
     */
    public function getGuruIzin(Request $request)
    {
        $status = $request->query('status'); // pending, disetujui, ditolak
        $tanggal = $request->query('tanggal');

        $izins = GuruIzin::with(['guru', 'approvedBy'])
            ->when($status, function ($q) use ($status) {
                $q->where('status', $status);
            })
            ->when($tanggal, function ($q) use ($tanggal) {
                $q->whereDate('tanggal', $tanggal);
            })
            ->orderBy('tanggal', 'desc')
            ->get();

        return response()->json([
            'success' => true,
            'data' => $izins
        ]);
    }

    /**
     * Approve or reject guru izin
     * PUT /api/kurikulum/guru-izin/{id}
     */
    public function updateGuruIzin(Request $request, $id)
    {
        $validator = Validator::make($request->all(), [
            'status' => 'required|in:disetujui,ditolak',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validasi gagal',
                'errors' => $validator->errors()
            ], 422);
        }

        $izin = GuruIzin::find($id);

        if (!$izin) {
            return response()->json([
                'success' => false,
                'message' => 'Data izin tidak ditemukan'
            ], 404);
        }

        $izin->update([
            'status' => $request->status,
            'approved_by' => Auth::id(),
            'approved_at' => now()
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Status izin berhasil diperbarui',
            'data' => $izin->load(['guru', 'approvedBy'])
        ]);
    }

    /**
     * Set guru pengganti untuk guru yang tidak masuk/izin
     * POST /api/kurikulum/guru-pengganti
     */
    public function createGuruPengganti(Request $request)
    {
        $validator = Validator::make($request->all(), [
            'guru_mengajar_id' => 'required|exists:guru_mengajars,id',
            'guru_pengganti_id' => 'required|exists:gurus,id',
        ]);

        if ($validator->fails()) {
            return response()->json([
                'success' => false,
                'message' => 'Validasi gagal',
                'errors' => $validator->errors()
            ], 422);
        }

        // Update guru_mengajar dengan guru pengganti
        $guruMengajar = \App\Models\GuruMengajar::find($request->guru_mengajar_id);
        $guruMengajar->update([
            'guru_pengganti_id' => $request->guru_pengganti_id
        ]);

        $guruMengajar->load(['jadwal.guru', 'jadwal.mapel', 'jadwal.kelas', 'guruPengganti']);

        return response()->json([
            'success' => true,
            'message' => 'Guru pengganti berhasil ditambahkan',
            'data' => [
                'id' => $guruMengajar->id,
                'jadwal_id' => $guruMengajar->jadwal_id,
                'hari' => $guruMengajar->jadwal?->hari,
                'jam_ke' => $guruMengajar->jadwal?->jam_ke,
                'kelas' => $guruMengajar->jadwal?->kelas?->kelas,
                'mapel' => $guruMengajar->jadwal?->mapel?->mapel,
                'guru_asli' => [
                    'id' => $guruMengajar->jadwal?->guru?->id,
                    'nama' => $guruMengajar->jadwal?->guru?->guru,
                    'kode' => $guruMengajar->jadwal?->guru?->kode_guru,
                ],
                'guru_pengganti' => [
                    'id' => $guruMengajar->guruPengganti?->id,
                    'nama' => $guruMengajar->guruPengganti?->guru,
                    'kode' => $guruMengajar->guruPengganti?->kode_guru,
                ],
                'status' => $guruMengajar->status,
                'keterangan' => $guruMengajar->keterangan,
            ]
        ], 201);
    }

    /**
     * Get list of guru pengganti (dari guru_mengajars yang ada guru_pengganti_id)
     * GET /api/kurikulum/guru-pengganti
     */
    public function getGuruPengganti(Request $request)
    {
        $hari = $request->query('hari');
        $kelasId = $request->query('kelas_id');

        // Ambil data guru_mengajar yang SUDAH memiliki guru pengganti
        $data = \App\Models\GuruMengajar::with(['jadwal.guru', 'jadwal.mapel', 'jadwal.kelas', 'guruPengganti'])
            ->whereNotNull('guru_pengganti_id')
            ->when($hari, function ($q) use ($hari) {
                $q->whereHas('jadwal', function ($j) use ($hari) {
                    $j->where('hari', $hari);
                });
            })
            ->when($kelasId, function ($q) use ($kelasId) {
                $q->whereHas('jadwal', function ($j) use ($kelasId) {
                    $j->where('kelas_id', $kelasId);
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

    /**
     * Get kehadiran guru tidak hadir (based on reports)
     * GET /api/kurikulum/kehadiran-guru
     */
    public function getKehadiranGuru(Request $request)
    {
        $tanggal = $request->query('tanggal', now()->toDateString());

        // Get all guru izin for today (those who should not be present)
        $guruTidakHadir = GuruIzin::with('guru')
            ->whereDate('tanggal', $tanggal)
            ->where('status', 'disetujui')
            ->get();

        return response()->json([
            'success' => true,
            'data' => [
                'tanggal' => $tanggal,
                'guru_tidak_hadir' => $guruTidakHadir
            ]
        ]);
    }

    /**
     * Approve guru izin
     * PATCH /api/kurikulum/guru-izin/{id}/approve
     */
    public function approveGuruIzin($id)
    {
        $izin = GuruIzin::find($id);

        if (!$izin) {
            return response()->json([
                'success' => false,
                'message' => 'Data izin tidak ditemukan'
            ], 404);
        }

        $izin->update([
            'status' => 'disetujui',
            'approved_by' => Auth::id(),
            'approved_at' => now()
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Izin berhasil disetujui',
            'data' => $izin->load(['guru', 'approvedBy'])
        ]);
    }

    /**
     * Reject guru izin
     * PATCH /api/kurikulum/guru-izin/{id}/reject
     */
    public function rejectGuruIzin($id)
    {
        $izin = GuruIzin::find($id);

        if (!$izin) {
            return response()->json([
                'success' => false,
                'message' => 'Data izin tidak ditemukan'
            ], 404);
        }

        $izin->update([
            'status' => 'ditolak',
            'approved_by' => Auth::id(),
            'approved_at' => now()
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Izin berhasil ditolak',
            'data' => $izin->load(['guru', 'approvedBy'])
        ]);
    }

    /**
     * Get all gurus for dropdown selection
     * GET /api/kurikulum/gurus
     */
    public function getGurus()
    {
        $gurus = Guru::orderBy('guru')->get(['id', 'kode_guru', 'guru', 'telepon']);

        return response()->json([
            'success' => true,
            'data' => $gurus
        ]);
    }

    /**
     * Get all jadwal
     * GET /api/kurikulum/jadwal
     */
    public function getJadwal(Request $request)
    {
        $hari = $request->query('hari');
        $guruId = $request->query('guru_id');

        $jadwals = Jadwal::with(['guruMengajar.guru', 'guruMengajar.mapel', 'guruMengajar.kelas'])
            ->when($hari, function ($q) use ($hari) {
                $q->where('hari', $hari);
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
}
