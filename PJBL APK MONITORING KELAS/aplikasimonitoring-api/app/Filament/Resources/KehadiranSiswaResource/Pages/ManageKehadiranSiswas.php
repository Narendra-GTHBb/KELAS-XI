<?php

namespace App\Filament\Resources\KehadiranSiswaResource\Pages;

use App\Filament\Resources\KehadiranSiswaResource;
use Filament\Actions;
use Filament\Resources\Pages\ManageRecords;

class ManageKehadiranSiswas extends ManageRecords
{
    protected static string $resource = KehadiranSiswaResource::class;

    protected function getHeaderActions(): array
    {
        return [
            Actions\CreateAction::make(),
        ];
    }
}
