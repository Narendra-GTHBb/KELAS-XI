<?php

namespace App\Filament\Resources\GuruResource\Pages;

use App\Filament\Resources\GuruResource;
use App\Filament\Imports\GuruImporter;
use Filament\Actions\CreateAction;
use Filament\Actions\ImportAction;
use Filament\Resources\Pages\ManageRecords;

class ManageGurus extends ManageRecords
{
    protected static string $resource = GuruResource::class;

    protected function getHeaderActions(): array
    {
        return [
            ImportAction::make()
                ->importer(GuruImporter::class)
                ->label('Import CSV'),
            CreateAction::make(),
        ];
    }
}
