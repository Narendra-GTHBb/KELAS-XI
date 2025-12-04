<?php

namespace App\Filament\Resources\MapelResource\Pages;

use App\Filament\Resources\MapelResource;
use App\Filament\Imports\MapelImporter;
use Filament\Actions\CreateAction;
use Filament\Actions\ImportAction;
use Filament\Resources\Pages\ManageRecords;

class ManageMapels extends ManageRecords
{
    protected static string $resource = MapelResource::class;

    protected function getHeaderActions(): array
    {
        return [
            ImportAction::make()
                ->importer(MapelImporter::class)
                ->label('Import CSV'),
            CreateAction::make(),
        ];
    }
}
