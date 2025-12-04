<?php

namespace App\Filament\Resources\UserResource\Pages;

use App\Filament\Resources\UserResource;
use App\Filament\Imports\UserImporter;
use Filament\Actions;
use Filament\Actions\ImportAction;
use Filament\Resources\Pages\ListRecords;

class ListUsers extends ListRecords
{
    protected static string $resource = UserResource::class;

    protected function getHeaderActions(): array
    {
        return [
            ImportAction::make()
                ->importer(UserImporter::class)
                ->label('Import CSV'),
            Actions\CreateAction::make()
                ->label('Tambah User'),
        ];
    }
}
