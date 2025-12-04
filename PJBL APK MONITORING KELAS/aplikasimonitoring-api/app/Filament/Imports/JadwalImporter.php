<?php

namespace App\Filament\Imports;

use App\Models\Jadwal;
use App\Models\Guru;
use App\Models\Mapel;
use App\Models\Kelas;
use App\Models\TahunAjaran;
use Filament\Actions\Imports\ImportColumn;
use Filament\Actions\Imports\Importer;
use Filament\Actions\Imports\Models\Import;

class JadwalImporter extends Importer
{
    protected static ?string $model = Jadwal::class;

    public static function getColumns(): array
    {
        return [
            ImportColumn::make('guru')
                ->label('Nama Guru')
                ->requiredMapping()
                ->rules(['required', 'string']),
            ImportColumn::make('mapel')
                ->label('Nama Mapel')
                ->requiredMapping()
                ->rules(['required', 'string']),
            ImportColumn::make('kelas')
                ->label('Nama Kelas')
                ->requiredMapping()
                ->rules(['required', 'string']),
            ImportColumn::make('tahun_ajaran')
                ->label('Tahun Ajaran')
                ->rules(['nullable', 'string']),
            ImportColumn::make('jam_ke')
                ->label('Jam Ke')
                ->requiredMapping()
                ->rules(['required', 'string', 'max:255']),
            ImportColumn::make('hari')
                ->label('Hari')
                ->requiredMapping()
                ->rules(['required', 'string', 'in:Senin,Selasa,Rabu,Kamis,Jumat,Sabtu']),
        ];
    }

    public function resolveRecord(): ?Jadwal
    {
        // Find or create related records
        $guru = Guru::firstOrCreate(['guru' => $this->data['guru']]);
        $mapel = Mapel::firstOrCreate(['mapel' => $this->data['mapel']]);
        $kelas = Kelas::firstOrCreate(['kelas' => $this->data['kelas']]);
        
        // Get tahun ajaran (use first if not specified)
        $tahunAjaran = null;
        if (!empty($this->data['tahun_ajaran'])) {
            $tahunAjaran = TahunAjaran::firstOrCreate(['tahun' => $this->data['tahun_ajaran']]);
        } else {
            $tahunAjaran = TahunAjaran::first();
        }

        // Check if jadwal already exists
        $jadwal = Jadwal::where([
            'guru_id' => $guru->id,
            'mapel_id' => $mapel->id,
            'kelas_id' => $kelas->id,
            'tahun_ajaran_id' => $tahunAjaran?->id ?? 1,
            'jam_ke' => $this->data['jam_ke'],
            'hari' => $this->data['hari'],
        ])->first();

        if ($jadwal) {
            return $jadwal;
        }

        // Create new jadwal with only valid columns
        $newJadwal = new Jadwal();
        $newJadwal->guru_id = $guru->id;
        $newJadwal->mapel_id = $mapel->id;
        $newJadwal->kelas_id = $kelas->id;
        $newJadwal->tahun_ajaran_id = $tahunAjaran?->id ?? 1;
        $newJadwal->jam_ke = $this->data['jam_ke'];
        $newJadwal->hari = $this->data['hari'];

        return $newJadwal;
    }

    public function fillRecord(): void
    {
        // Override to prevent filling with raw CSV data
    }

    public static function getCompletedNotificationBody(Import $import): string
    {
        $body = 'Import jadwal selesai. ' . number_format($import->successful_rows) . ' baris berhasil diimport.';

        if ($failedRowsCount = $import->getFailedRowsCount()) {
            $body .= ' ' . number_format($failedRowsCount) . ' baris gagal diimport.';
        }

        return $body;
    }
}
