<?php

namespace App\Filament\Imports;

use App\Models\Guru;
use Filament\Actions\Imports\ImportColumn;
use Filament\Actions\Imports\Importer;
use Filament\Actions\Imports\Models\Import;

class GuruImporter extends Importer
{
    protected static ?string $model = Guru::class;

    public static function getColumns(): array
    {
        return [
            ImportColumn::make('guru')
                ->label('Nama Guru')
                ->requiredMapping()
                ->rules(['required', 'string', 'max:255']),
            ImportColumn::make('telepon')
                ->label('Telepon')
                ->rules(['nullable', 'string', 'max:20']),
        ];
    }

    public function resolveRecord(): ?Guru
    {
        $existing = Guru::where('guru', $this->data['guru'])->first();
        
        if ($existing) {
            return $existing;
        }

        $guru = new Guru();
        $guru->guru = $this->data['guru'];
        $guru->telepon = $this->data['telepon'] ?? null;

        return $guru;
    }

    public function fillRecord(): void
    {
        // Override to prevent filling with raw CSV data
    }

    public static function getCompletedNotificationBody(Import $import): string
    {
        $body = 'Import guru selesai. ' . number_format($import->successful_rows) . ' baris berhasil diimport.';

        if ($failedRowsCount = $import->getFailedRowsCount()) {
            $body .= ' ' . number_format($failedRowsCount) . ' baris gagal diimport.';
        }

        return $body;
    }
}
