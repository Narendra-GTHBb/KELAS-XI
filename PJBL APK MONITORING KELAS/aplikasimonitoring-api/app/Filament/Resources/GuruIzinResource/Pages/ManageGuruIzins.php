<?php

namespace App\Filament\Resources\GuruIzinResource\Pages;

use App\Filament\Resources\GuruIzinResource;
use Filament\Actions;
use Filament\Resources\Pages\ManageRecords;

class ManageGuruIzins extends ManageRecords
{
    protected static string $resource = GuruIzinResource::class;

    protected function getHeaderActions(): array
    {
        return [
            Actions\CreateAction::make(),
        ];
    }
}
