<?php

namespace App\Filament\Resources;

use App\Filament\Resources\MapelResource\Pages\ManageMapels;
use App\Models\Mapel;
use BackedEnum;
use Filament\Actions\BulkActionGroup;
use Filament\Actions\DeleteAction;
use Filament\Actions\DeleteBulkAction;
use Filament\Actions\EditAction;
use Filament\Forms\Components\TextInput;
use Filament\Resources\Resource;
use Filament\Schemas\Schema;
use Filament\Support\Icons\Heroicon;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Table;

class MapelResource extends Resource
{
    protected static ?string $model = Mapel::class;

    protected static string|\BackedEnum|null $navigationIcon = 'heroicon-o-book-open';
    
    protected static ?string $navigationLabel = 'Mata Pelajaran';

    public static function form(Schema $schema): Schema
    {
        return $schema
            ->components([
                TextInput::make('kode_mapel')
                    ->label('Kode Mapel')
                    ->disabled()
                    ->visibleOn('edit'),
                TextInput::make('mapel')
                    ->label('Nama Mapel')
                    ->required()
                    ->maxLength(255),
            ]);
    }

    public static function table(Table $table): Table
    {
        return $table
            ->columns([
                TextColumn::make('kode_mapel')
                    ->label('Kode Mapel')
                    ->searchable()
                    ->sortable(),
                TextColumn::make('mapel')
                    ->label('Nama Mapel')
                    ->searchable()
                    ->sortable(),
            ])
            ->filters([
                //
            ])
            ->recordActions([
                EditAction::make(),
                DeleteAction::make(),
            ])
            ->toolbarActions([
                BulkActionGroup::make([
                    DeleteBulkAction::make(),
                ]),
            ]);
    }

    public static function getPages(): array
    {
        return [
            'index' => ManageMapels::route('/'),
        ];
    }
}
