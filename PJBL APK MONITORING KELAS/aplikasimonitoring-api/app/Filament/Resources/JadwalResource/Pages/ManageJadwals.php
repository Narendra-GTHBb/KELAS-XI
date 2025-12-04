<?php

namespace App\Filament\Resources\JadwalResource\Pages;

use App\Filament\Resources\JadwalResource;
use App\Filament\Imports\JadwalImporter;
use Filament\Actions\CreateAction;
use Filament\Actions\ImportAction;
use Filament\Resources\Pages\ManageRecords;

class ManageJadwals extends ManageRecords
{
    protected static string $resource = JadwalResource::class;

    protected function getHeaderActions(): array
    {
        return [
            ImportAction::make()
                ->importer(JadwalImporter::class)
                ->label('Import CSV'),
            CreateAction::make(),
        ];
    }
}
