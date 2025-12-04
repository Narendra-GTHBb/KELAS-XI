<?php

namespace App\Http\Controllers;

use App\Models\Guru;
use Illuminate\Http\Request;

class GuruController extends Controller
{
    public function index()
    {
        return response()->json(Guru::all());
    }

    public function store(Request $request)
    {
        $data = $request->validate([
            'kode_guru' => 'required|unique:gurus,kode_guru',
            'guru' => 'required|string',
            'telepon' => 'nullable|string',
        ]);

        $guru = Guru::create($data);
        return response()->json($guru, 201);
    }

    public function show(Guru $guru)
    {
        return response()->json($guru);
    }

    public function update(Request $request, Guru $guru)
    {
        $data = $request->validate([
            'kode_guru' => 'required|unique:gurus,kode_guru,' . $guru->id,
            'guru' => 'required|string',
            'telepon' => 'nullable|string',
        ]);

        $guru->update($data);
        return response()->json($guru);
    }

    public function destroy(Guru $guru)
    {
        $guru->delete();
        return response()->json(['message' => 'Deleted']);
    }
}
