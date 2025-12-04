<?php

namespace App\Http\Controllers;

use App\Models\Jadwal;
use Illuminate\Http\Request;

class JadwalController extends Controller
{
    public function index()
    {
        $data = Jadwal::with(['guru', 'mapel', 'tahunAjaran', 'kelas'])->get();
        
        return response()->json($data->map(function ($item) {
            return [
                'id' => $item->id,
                'guru_id' => $item->guru_id,
                'mapel_id' => $item->mapel_id,
                'tahun_ajaran_id' => $item->tahun_ajaran_id,
                'kelas_id' => $item->kelas_id,
                'jam_ke' => $item->jam_ke,
                'hari' => $item->hari,
                'created_at' => $item->created_at,
                'updated_at' => $item->updated_at,
                // Flat fields untuk kemudahan akses
                'kode_guru' => $item->guru?->kode_guru,
                'nama_guru' => $item->guru?->guru,
                'nama_mapel' => $item->mapel?->mapel,
                'nama_kelas' => $item->kelas?->kelas,
                'tahun_ajaran' => $item->tahunAjaran?->tahun_ajaran,
                // Nested untuk backward compatibility
                'guru' => $item->guru,
                'mapel' => $item->mapel,
                'kelas' => $item->kelas,
                'tahun_ajaran_detail' => $item->tahunAjaran,
            ];
        }));
    }

    public function store(Request $request)
    {
        $data = $request->validate([
            'guru_id' => 'required|exists:gurus,id',
            'mapel_id' => 'required|exists:mapels,id',
            'tahun_ajaran_id' => 'required|exists:tahun_ajarans,id',
            'kelas_id' => 'required|exists:kelas,id',
            'jam_ke' => 'required|string',
            'hari' => 'required|string',
        ]);

        $jadwal = Jadwal::create($data);
        return response()->json($jadwal, 201);
    }

    public function show(Jadwal $jadwal)
    {
        return response()->json($jadwal->load(['guru', 'mapel', 'tahunAjaran', 'kelas']));
    }

    public function update(Request $request, Jadwal $jadwal)
    {
        $data = $request->validate([
            'guru_id' => 'required|exists:gurus,id',
            'mapel_id' => 'required|exists:mapels,id',
            'tahun_ajaran_id' => 'required|exists:tahun_ajarans,id',
            'kelas_id' => 'required|exists:kelas,id',
            'jam_ke' => 'required|string',
            'hari' => 'required|string',
        ]);

        $jadwal->update($data);
        return response()->json($jadwal);
    }

    // ■ Fungsi 1: Jam Ke, Mata Pelajaran, Kode Guru, Nama Guru berdasarkan kelas_id & hari
    public function getByClassAndDay($kelas_id, $hari)
    {
        $data = Jadwal::with(['guru', 'mapel'])
            ->where('kelas_id', $kelas_id)
            ->where('hari', $hari)
            ->get()
            ->map(function ($item) {
                return [
                    'jadwal_id' => $item->id,
                    'jam_ke' => $item->jam_ke,
                    'mata_pelajaran' => $item->mapel->mapel ?? '',
                    'kode_guru' => $item->guru->kode_guru ?? '',
                    'nama_guru' => $item->guru->guru ?? '',
                    'guru_id' => $item->guru_id,
                    'mapel_id' => $item->mapel_id,
                ];
            });

        return response()->json($data);
    }

    // ■ Fungsi 2: Nama Guru, Mapel, Tahun Ajaran, Jam Ke berdasarkan kelas_id & hari 📋 Salin kode
    public function getScheduleByClassAndDay($kelas_id, $hari)
    {
        $data = Jadwal::with(['guru', 'mapel', 'tahunAjaran'])
            ->where('kelas_id', $kelas_id)
            ->where('hari', $hari)
            ->get()
            ->map(function ($item) {
                return [
                    'nama_guru' => $item->guru->guru ?? '',
                    'mata_pelajaran' => $item->mapel->mapel ?? '',
                    'tahun_ajaran' => $item->tahunAjaran->tahun ?? '',
                    'jam_ke' => $item->jam_ke,
                ];
            });

        return response()->json($data);
    }

    public function destroy(Jadwal $jadwal)
    {
        $jadwal->delete();
        return response()->json(['message' => 'Deleted']);
    }
}
