<?php

namespace App\Filament\Resources\GuruMengajarResource\Pages;

use App\Filament\Resources\GuruMengajarResource;
use Filament\Actions\CreateAction;
use Filament\Resources\Pages\ManageRecords;

class ManageGuruMengajars extends ManageRecords
{
    protected static string $resource = GuruMengajarResource::class;

    protected function getHeaderActions(): array
    {
        return [
            CreateAction::make(),
        ];
    }
}
