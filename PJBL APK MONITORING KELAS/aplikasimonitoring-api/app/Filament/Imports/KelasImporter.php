<?php

namespace App\Filament\Imports;

use App\Models\Kelas;
use Filament\Actions\Imports\ImportColumn;
use Filament\Actions\Imports\Importer;
use Filament\Actions\Imports\Models\Import;

class KelasImporter extends Importer
{
    protected static ?string $model = Kelas::class;

    public static function getColumns(): array
    {
        return [
            ImportColumn::make('kelas')
                ->label('Nama Kelas')
                ->requiredMapping()
                ->rules(['required', 'string', 'max:255']),
        ];
    }

    public function resolveRecord(): ?Kelas
    {
        $existing = Kelas::where('kelas', $this->data['kelas'])->first();
        
        if ($existing) {
            return $existing;
        }

        $kelas = new Kelas();
        $kelas->kelas = $this->data['kelas'];

        return $kelas;
    }

    public function fillRecord(): void
    {
        // Override to prevent filling with raw CSV data
    }

    public static function getCompletedNotificationBody(Import $import): string
    {
        $body = 'Import kelas selesai. ' . number_format($import->successful_rows) . ' baris berhasil diimport.';

        if ($failedRowsCount = $import->getFailedRowsCount()) {
            $body .= ' ' . number_format($failedRowsCount) . ' baris gagal diimport.';
        }

        return $body;
    }
}
