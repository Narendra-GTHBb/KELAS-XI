<?php

namespace App\Http\Controllers;

use App\Models\GuruMengajar;
use Illuminate\Http\Request;

class GuruMengajarController extends Controller
{
    public function index()
    {
        // Return data dengan format flat + field ID untuk kemudahan update
        $data = GuruMengajar::with(['jadwal.guru', 'jadwal.mapel', 'jadwal.kelas'])->get();
        
        return response()->json($data->map(function ($gm) {
            return [
                'id' => (int) $gm->id,
                'jadwal_id' => (int) $gm->jadwal_id,
                'kode_guru' => $gm->jadwal?->guru?->kode_guru ?? '',
                'nama_guru' => $gm->jadwal?->guru?->guru ?? '',
                'mapel' => $gm->jadwal?->mapel?->mapel ?? '',
                'jam_ke' => $gm->jadwal?->jam_ke ?? '',
                'hari' => $gm->jadwal?->hari ?? '',
                'kelas_id' => (int) ($gm->jadwal?->kelas_id ?? 0),
                'guru_id' => (int) ($gm->jadwal?->guru_id ?? 0),
                'mapel_id' => (int) ($gm->jadwal?->mapel_id ?? 0),
                'status' => $gm->status ?? '',
                'keterangan' => $gm->keterangan ?? '',
            ];
        }), JSON_UNESCAPED_UNICODE);
    }

    public function store(Request $request)
    {
        // Validasi input - 7 field fleksibel
        $data = $request->validate([
            'hari' => 'required|string',
            'kelas_id' => 'required|exists:kelas,id',
            'guru_id' => 'required|exists:gurus,id',
            'mapel_id' => 'required|exists:mapels,id',
            'jam_ke' => 'required|string',
            'status' => 'required|in:Masuk,Tidak Masuk,Izin',
            'keterangan' => 'nullable|string',
        ]);

        // Cari atau buat jadwal dengan kombinasi tersebut
        $jadwal = \App\Models\Jadwal::firstOrCreate(
            [
                'hari' => $data['hari'],
                'kelas_id' => $data['kelas_id'],
                'guru_id' => $data['guru_id'],
                'mapel_id' => $data['mapel_id'],
                'jam_ke' => $data['jam_ke'],
            ],
            [
                'tahun_ajaran_id' => 1, // Default tahun ajaran aktif
            ]
        );

        // Update atau buat guru_mengajar (jika sudah ada dengan jadwal_id yang sama, update saja)
        $gm = GuruMengajar::updateOrCreate(
            ['jadwal_id' => $jadwal->id],
            [
                'status' => $data['status'],
                'keterangan' => $data['keterangan'] ?? null,
            ]
        );

        $gm->load('jadwal.guru', 'jadwal.mapel', 'jadwal.kelas');

        return response()->json([
            'success' => true,
            'message' => 'Data berhasil disimpan',
            'data' => [
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
                'created_at' => $gm->created_at,
                'updated_at' => $gm->updated_at,
            ]
        ], 201);
    }

    public function show(GuruMengajar $guruMengajar)
    {
        $gm = $guruMengajar->load('jadwal.guru', 'jadwal.mapel', 'jadwal.kelas');
        
        return response()->json([
            'id' => $gm->id,
            'jadwal_id' => $gm->jadwal_id,
            // Field flat untuk kemudahan akses di Android
            'kode_guru' => $gm->jadwal?->guru?->kode_guru,
            'nama_guru' => $gm->jadwal?->guru?->guru,
            'mapel' => $gm->jadwal?->mapel?->mapel,
            'jam_ke' => $gm->jadwal?->jam_ke,
            'hari' => $gm->jadwal?->hari,
            'kelas_id' => $gm->jadwal?->kelas_id,
            'status' => $gm->status,
            'keterangan' => $gm->keterangan,
            'created_at' => $gm->created_at,
            'updated_at' => $gm->updated_at,
            // Juga sertakan nested jadwal untuk backward compatibility
            'jadwal' => $gm->jadwal ? [
                'id' => $gm->jadwal->id,
                'guru_id' => $gm->jadwal->guru_id,
                'mapel_id' => $gm->jadwal->mapel_id,
                'tahun_ajaran_id' => $gm->jadwal->tahun_ajaran_id,
                'kelas_id' => $gm->jadwal->kelas_id,
                'jam_ke' => $gm->jadwal->jam_ke,
                'hari' => $gm->jadwal->hari,
                'created_at' => $gm->jadwal->created_at,
                'updated_at' => $gm->jadwal->updated_at,
                'guru' => $gm->jadwal->guru ? [
                    'id' => $gm->jadwal->guru->id,
                    'kode_guru' => $gm->jadwal->guru->kode_guru,
                    'guru' => $gm->jadwal->guru->guru,
                    'telepon' => $gm->jadwal->guru->telepon ?? null,
                ] : null,
                'mapel' => $gm->jadwal->mapel ? [
                    'id' => $gm->jadwal->mapel->id,
                    'mapel' => $gm->jadwal->mapel->mapel,
                ] : null,
                'kelas' => $gm->jadwal->kelas ? [
                    'id' => $gm->jadwal->kelas->id,
                    'kelas' => $gm->jadwal->kelas->kelas,
                ] : null,
            ] : null,
        ]);
    }

    public function update(Request $request, GuruMengajar $guruMengajar)
    {
        // Validasi input - 7 field fleksibel
        $data = $request->validate([
            'hari' => 'required|string',
            'kelas_id' => 'required|exists:kelas,id',
            'guru_id' => 'required|exists:gurus,id',
            'mapel_id' => 'required|exists:mapels,id',
            'jam_ke' => 'required|string',
            'status' => 'required|in:Masuk,Tidak Masuk,Izin',
            'keterangan' => 'nullable|string',
        ]);

        // Cari atau buat jadwal dengan kombinasi tersebut
        $jadwal = \App\Models\Jadwal::firstOrCreate(
            [
                'hari' => $data['hari'],
                'kelas_id' => $data['kelas_id'],
                'guru_id' => $data['guru_id'],
                'mapel_id' => $data['mapel_id'],
                'jam_ke' => $data['jam_ke'],
            ],
            [
                'tahun_ajaran_id' => 1,
            ]
        );

        // Update guru_mengajar
        $guruMengajar->update([
            'jadwal_id' => $jadwal->id,
            'status' => $data['status'],
            'keterangan' => $data['keterangan'] ?? null,
        ]);

        $guruMengajar->load('jadwal.guru', 'jadwal.mapel', 'jadwal.kelas');

        return response()->json([
            'success' => true,
            'message' => 'Data berhasil diperbarui',
            'data' => [
                'id' => $guruMengajar->id,
                'jadwal_id' => $guruMengajar->jadwal_id,
                'kode_guru' => $guruMengajar->jadwal?->guru?->kode_guru,
                'nama_guru' => $guruMengajar->jadwal?->guru?->guru,
                'mapel' => $guruMengajar->jadwal?->mapel?->mapel,
                'jam_ke' => $guruMengajar->jadwal?->jam_ke,
                'hari' => $guruMengajar->jadwal?->hari,
                'kelas_id' => $guruMengajar->jadwal?->kelas_id,
                'status' => $guruMengajar->status,
                'keterangan' => $guruMengajar->keterangan,
                'created_at' => $guruMengajar->created_at,
                'updated_at' => $guruMengajar->updated_at,
            ]
        ], 200);
    }

    public function getByHariKelas(Request $request)
    {
        $data = $request->validate([
            'hari' => 'required|string',
            'kelas_id' => 'required|exists:kelas,id',
        ]);

        $result = GuruMengajar::with(['jadwal.guru', 'jadwal.mapel'])
            ->whereHas('jadwal', function ($q) use ($data) {
                $q->where('hari', $data['hari'])
                  ->where('kelas_id', $data['kelas_id']);
            })
            ->get()
            ->map(function ($gm) {
                return [
                    'id' => $gm->id,
                    'jadwal_id' => $gm->jadwal_id,
                    'kode_guru' => $gm->jadwal && $gm->jadwal->guru ? $gm->jadwal->guru->kode_guru : null,
                    'nama_guru' => $gm->jadwal && $gm->jadwal->guru ? $gm->jadwal->guru->guru : null,
                    'mapel' => $gm->jadwal && $gm->jadwal->mapel ? $gm->jadwal->mapel->mapel : null,
                    'jam_ke' => $gm->jadwal ? $gm->jadwal->jam_ke : null,
                    'status' => $gm->status,
                    'keterangan' => $gm->keterangan,
                ];
            });

        return response()->json($result);
    }

    public function getTidakMasukByHariKelas(Request $request)
    {
        $data = $request->validate([
            'hari' => 'required|string',
            'kelas_id' => 'required|exists:kelas,id',
        ]);

        $result = GuruMengajar::with(['jadwal.guru', 'jadwal.mapel'])
            ->where('status', 'Tidak Masuk')
            ->whereHas('jadwal', function ($q) use ($data) {
                $q->where('hari', $data['hari'])
                  ->where('kelas_id', $data['kelas_id']);
            })
            ->get()
            ->map(function ($gm) {
                return [
                    'kode_guru' => $gm->jadwal && $gm->jadwal->guru ? $gm->jadwal->guru->kode_guru : null,
                    'nama_guru' => $gm->jadwal && $gm->jadwal->guru ? $gm->jadwal->guru->guru : null,
                    'mapel' => $gm->jadwal && $gm->jadwal->mapel ? $gm->jadwal->mapel->mapel : null,
                    'jam_ke' => $gm->jadwal ? $gm->jadwal->jam_ke : null,
                    'status' => $gm->status,
                    'keterangan' => $gm->keterangan,
                ];
            });

        return response()->json($result);
    }

    public function destroy(GuruMengajar $guruMengajar)
    {
        $guruMengajar->delete();
        return response()->json([
            'success' => true,
            'message' => 'Data berhasil dihapus'
        ], 200);
    }

    /**
     * Get guru_mengajar by jadwal_id
     * Digunakan untuk mengambil data kehadiran yang sudah ada untuk jadwal tertentu
     */
    public function getByJadwalId($jadwal_id)
    {
        $gm = GuruMengajar::with(['jadwal.guru', 'jadwal.mapel', 'jadwal.kelas'])
            ->where('jadwal_id', $jadwal_id)
            ->first();

        if (!$gm) {
            return response()->json([
                'found' => false,
                'data' => null
            ]);
        }

        return response()->json([
            'found' => true,
            'data' => [
                'id' => (int) $gm->id,
                'jadwal_id' => (int) $gm->jadwal_id,
                'kode_guru' => $gm->jadwal?->guru?->kode_guru ?? '',
                'nama_guru' => $gm->jadwal?->guru?->guru ?? '',
                'mapel' => $gm->jadwal?->mapel?->mapel ?? '',
                'jam_ke' => $gm->jadwal?->jam_ke ?? '',
                'hari' => $gm->jadwal?->hari ?? '',
                'kelas_id' => (int) ($gm->jadwal?->kelas_id ?? 0),
                'guru_id' => (int) ($gm->jadwal?->guru_id ?? 0),
                'mapel_id' => (int) ($gm->jadwal?->mapel_id ?? 0),
                'status' => $gm->status ?? '',
                'keterangan' => $gm->keterangan ?? '',
            ]
        ]);
    }

    /**
     * Update status dan keterangan saja (untuk role Kurikulum)
     */
    public function updateStatus(Request $request, GuruMengajar $guruMengajar)
    {
        $data = $request->validate([
            'status' => 'required|in:Masuk,Tidak Masuk,Izin',
            'keterangan' => 'nullable|string',
        ]);

        $guruMengajar->update([
            'status' => $data['status'],
            'keterangan' => $data['keterangan'] ?? null,
        ]);

        $guruMengajar->load('jadwal.guru', 'jadwal.mapel', 'jadwal.kelas');

        return response()->json([
            'success' => true,
            'message' => 'Status berhasil diperbarui',
            'data' => [
                'id' => $guruMengajar->id,
                'jadwal_id' => $guruMengajar->jadwal_id,
                'kode_guru' => $guruMengajar->jadwal?->guru?->kode_guru ?? '',
                'nama_guru' => $guruMengajar->jadwal?->guru?->guru ?? '',
                'mapel' => $guruMengajar->jadwal?->mapel?->mapel ?? '',
                'jam_ke' => $guruMengajar->jadwal?->jam_ke ?? '',
                'hari' => $guruMengajar->jadwal?->hari ?? '',
                'kelas_id' => (int) ($guruMengajar->jadwal?->kelas_id ?? 0),
                'guru_id' => (int) ($guruMengajar->jadwal?->guru_id ?? 0),
                'mapel_id' => (int) ($guruMengajar->jadwal?->mapel_id ?? 0),
                'status' => $guruMengajar->status,
                'keterangan' => $guruMengajar->keterangan ?? '',
            ]
        ], 200);
    }

    /**
     * Get guru_mengajar with filter hari dan kelas_id
     * Untuk halaman kehadiran di Kurikulum
     */
    public function getWithFilter(Request $request)
    {
        $hari = $request->query('hari');
        $kelasId = $request->query('kelas_id');

        $query = GuruMengajar::with(['jadwal.guru', 'jadwal.mapel', 'jadwal.kelas', 'guruPengganti']);

        if ($hari) {
            $query->whereHas('jadwal', function ($q) use ($hari) {
                $q->where('hari', $hari);
            });
        }

        if ($kelasId) {
            $query->whereHas('jadwal', function ($q) use ($kelasId) {
                $q->where('kelas_id', $kelasId);
            });
        }

        $data = $query->get()->map(function ($gm) {
            return [
                'id' => (int) $gm->id,
                'jadwal_id' => (int) $gm->jadwal_id,
                'kode_guru' => $gm->jadwal?->guru?->kode_guru ?? '',
                'nama_guru' => $gm->jadwal?->guru?->guru ?? '',
                'mapel' => $gm->jadwal?->mapel?->mapel ?? '',
                'jam_ke' => $gm->jadwal?->jam_ke ?? '',
                'hari' => $gm->jadwal?->hari ?? '',
                'kelas_id' => (int) ($gm->jadwal?->kelas_id ?? 0),
                'guru_id' => (int) ($gm->jadwal?->guru_id ?? 0),
                'mapel_id' => (int) ($gm->jadwal?->mapel_id ?? 0),
                'status' => $gm->status ?? '',
                'keterangan' => $gm->keterangan ?? '',
                'guru_pengganti_id' => $gm->guru_pengganti_id,
                'guru_pengganti' => $gm->guruPengganti ? [
                    'id' => $gm->guruPengganti->id,
                    'kode_guru' => $gm->guruPengganti->kode_guru,
                    'guru' => $gm->guruPengganti->guru,
                ] : null,
                // Durasi izin
                'tanggal_mulai_izin' => $gm->tanggal_mulai_izin?->format('Y-m-d'),
                'tanggal_selesai_izin' => $gm->tanggal_selesai_izin?->format('Y-m-d'),
                'durasi_izin' => $gm->durasi_izin,
                // Nested jadwal untuk backward compatibility
                'jadwal' => $gm->jadwal ? [
                    'id' => $gm->jadwal->id,
                    'guru_id' => $gm->jadwal->guru_id,
                    'mapel_id' => $gm->jadwal->mapel_id,
                    'kelas_id' => $gm->jadwal->kelas_id,
                    'jam_ke' => $gm->jadwal->jam_ke,
                    'hari' => $gm->jadwal->hari,
                    'guru' => $gm->jadwal->guru ? [
                        'id' => $gm->jadwal->guru->id,
                        'kode_guru' => $gm->jadwal->guru->kode_guru,
                        'guru' => $gm->jadwal->guru->guru,
                    ] : null,
                    'mapel' => $gm->jadwal->mapel ? [
                        'id' => $gm->jadwal->mapel->id,
                        'mapel' => $gm->jadwal->mapel->mapel,
                    ] : null,
                    'kelas' => $gm->jadwal->kelas ? [
                        'id' => $gm->jadwal->kelas->id,
                        'kelas' => $gm->jadwal->kelas->kelas,
                    ] : null,
                ] : null,
            ];
        });

        return response()->json([
            'success' => true,
            'data' => $data
        ]);
    }
}