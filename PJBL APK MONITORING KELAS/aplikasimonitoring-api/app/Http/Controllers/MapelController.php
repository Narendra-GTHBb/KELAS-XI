<?php

namespace App\Http\Controllers;

use App\Models\Mapel;
use Illuminate\Http\Request;

class MapelController extends Controller
{
    public function index()
    {
        return response()->json(Mapel::all());
    }

    public function store(Request $request)
    {
        $data = $request->validate([
            'kode_mapel' => 'required|unique:mapels,kode_mapel',
            'mapel' => 'required|string',
        ]);

        $mapel = Mapel::create($data);
        return response()->json($mapel, 201);
    }

    public function show(Mapel $mapel)
    {
        return response()->json($mapel);
    }

    public function update(Request $request, Mapel $mapel)
    {
        $data = $request->validate([
            'kode_mapel' => 'required|unique:mapels,kode_mapel,' . $mapel->id,
            'mapel' => 'required|string',
        ]);

        $mapel->update($data);
        return response()->json($mapel);
    }

    public function destroy(Mapel $mapel)
    {
        $mapel->delete();
        return response()->json(['message' => 'Deleted']);
    }
}
