<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Mapel;
use App\Models\Guru;

class UpdateKodeSeeder extends Seeder
{
    /**
     * Update semua kode mapel dan guru ke format urut nomor
     */
    public function run(): void
    {
        // Update kode mapel ke format M001, M002, dst.
        $mapels = Mapel::orderBy('id')->get();
        foreach ($mapels as $index => $mapel) {
            $newKode = 'M' . str_pad($index + 1, 3, '0', STR_PAD_LEFT);
            $mapel->update(['kode_mapel' => $newKode]);
            $this->command->info("Mapel: {$mapel->mapel} -> {$newKode}");
        }

        // Update kode guru ke format G001, G002, dst. (pastikan konsisten)
        $gurus = Guru::orderBy('id')->get();
        foreach ($gurus as $index => $guru) {
            $newKode = 'G' . str_pad($index + 1, 3, '0', STR_PAD_LEFT);
            $guru->update(['kode_guru' => $newKode]);
            $this->command->info("Guru: {$guru->guru} -> {$newKode}");
        }
    }
}
