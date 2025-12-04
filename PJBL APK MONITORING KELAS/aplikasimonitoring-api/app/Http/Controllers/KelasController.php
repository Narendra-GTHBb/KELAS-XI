<?php

namespace App\Http\Controllers;

use App\Models\Kelas;
use Illuminate\Http\Request;

class KelasController extends Controller
{
    public function index()
    {
        return response()->json(Kelas::all());
    }

    public function store(Request $request)
    {
        $data = $request->validate([
            'kelas' => 'required|string',
        ]);

        $kelas = Kelas::create($data);
        return response()->json($kelas, 201);
    }

    public function show(Kelas $kela) // catatan: laravel auto plural + param pakai singular 'kela'
    {
        return response()->json($kela);
    }

    public function update(Request $request, Kelas $kela)
    {
        $data = $request->validate([
            'kelas' => 'required|string',
        ]);

        $kela->update($data);
        return response()->json($kela);
    }

    public function destroy(Kelas $kela)
    {
        $kela->delete();
        return response()->json(['message' => 'Deleted']);
    }
}
