<?php

namespace App\Filament\Imports;

use App\Models\Mapel;
use Filament\Actions\Imports\ImportColumn;
use Filament\Actions\Imports\Importer;
use Filament\Actions\Imports\Models\Import;

class MapelImporter extends Importer
{
    protected static ?string $model = Mapel::class;

    public static function getColumns(): array
    {
        return [
            ImportColumn::make('mapel')
                ->label('Nama Mapel')
                ->requiredMapping()
                ->rules(['required', 'string', 'max:255']),
            ImportColumn::make('kode_mapel')
                ->label('Kode Mapel')
                ->rules(['nullable', 'string', 'max:20']),
        ];
    }

    public function resolveRecord(): ?Mapel
    {
        $existing = Mapel::where('mapel', $this->data['mapel'])->first();
        
        if ($existing) {
            return $existing;
        }

        $mapel = new Mapel();
        $mapel->mapel = $this->data['mapel'];
        $mapel->kode_mapel = $this->data['kode_mapel'] ?? null;

        return $mapel;
    }

    public function fillRecord(): void
    {
        // Override to prevent filling with raw CSV data
    }

    public static function getCompletedNotificationBody(Import $import): string
    {
        $body = 'Import mapel selesai. ' . number_format($import->successful_rows) . ' baris berhasil diimport.';

        if ($failedRowsCount = $import->getFailedRowsCount()) {
            $body .= ' ' . number_format($failedRowsCount) . ' baris gagal diimport.';
        }

        return $body;
    }
}
