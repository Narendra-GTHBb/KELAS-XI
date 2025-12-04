<?php

namespace App\Http\Controllers;

use App\Models\TahunAjaran;
use Illuminate\Http\Request;

class TahunAjaranController extends Controller
{
    public function index()
    {
        return response()->json(TahunAjaran::all());
    }

    public function store(Request $request)
    {
        $data = $request->validate([
            'tahun' => 'required|string',
            'flag' => 'nullable|boolean',
        ]);

        $tahun = TahunAjaran::create($data);
        return response()->json($tahun, 201);
    }

    public function show(TahunAjaran $tahunAjaran)
    {
        return response()->json($tahunAjaran);
    }

    public function update(Request $request, TahunAjaran $tahunAjaran)
    {
        $data = $request->validate([
            'tahun' => 'required|string',
            'flag' => 'nullable|boolean',
        ]);

        $tahunAjaran->update($data);
        return response()->json($tahunAjaran);
    }

    public function destroy(TahunAjaran $tahunAjaran)
    {
        $tahunAjaran->delete();
        return response()->json(['message' => 'Deleted']);
    }
}
