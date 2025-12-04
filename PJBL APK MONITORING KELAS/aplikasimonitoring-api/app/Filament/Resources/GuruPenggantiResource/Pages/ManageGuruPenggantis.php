<?php

namespace App\Filament\Resources\GuruPenggantiResource\Pages;

use App\Filament\Resources\GuruPenggantiResource;
use Filament\Actions;
use Filament\Resources\Pages\ManageRecords;

class ManageGuruPenggantis extends ManageRecords
{
    protected static string $resource = GuruPenggantiResource::class;

    protected function getHeaderActions(): array
    {
        return [
            Actions\CreateAction::make(),
        ];
    }
}
