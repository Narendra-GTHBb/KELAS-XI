<?php

namespace App\Filament\Resources\KelasResource\Pages;

use App\Filament\Resources\KelasResource;
use App\Filament\Imports\KelasImporter;
use Filament\Actions\CreateAction;
use Filament\Actions\ImportAction;
use Filament\Resources\Pages\ManageRecords;

class ManageKelas extends ManageRecords
{
    protected static string $resource = KelasResource::class;

    protected function getHeaderActions(): array
    {
        return [
            ImportAction::make()
                ->importer(KelasImporter::class)
                ->label('Import CSV'),
            CreateAction::make(),
        ];
    }
}
