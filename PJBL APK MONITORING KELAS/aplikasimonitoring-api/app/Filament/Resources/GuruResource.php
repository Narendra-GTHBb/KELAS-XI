<?php

namespace App\Filament\Resources;

use App\Filament\Resources\GuruResource\Pages\ManageGurus;
use App\Models\Guru;
use Filament\Actions\BulkActionGroup;
use Filament\Actions\DeleteAction;
use Filament\Actions\DeleteBulkAction;
use Filament\Actions\EditAction;
use Filament\Forms\Components\TextInput;
use Filament\Resources\Resource;
use Filament\Schemas\Schema;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Table;

class GuruResource extends Resource
{
    protected static ?string $model = Guru::class;

    protected static string|\BackedEnum|null $navigationIcon = 'heroicon-o-user-group';
    
    protected static ?string $navigationLabel = 'Guru';

    public static function form(Schema $schema): Schema
    {
        return $schema
            ->components([
                TextInput::make('kode_guru')
                    ->label('Kode Guru')
                    ->disabled()
                    ->visibleOn('edit'),
                TextInput::make('guru')
                    ->label('Nama Guru')
                    ->required()
                    ->maxLength(255),
                TextInput::make('telepon')
                    ->label('Telepon')
                    ->maxLength(255),
            ]);
    }

    public static function table(Table $table): Table
    {
        return $table
            ->columns([
                TextColumn::make('kode_guru')
                    ->label('Kode Guru')
                    ->searchable()
                    ->sortable(),
                TextColumn::make('guru')
                    ->label('Nama Guru')
                    ->searchable()
                    ->sortable(),
                TextColumn::make('telepon')
                    ->label('Telepon')
                    ->searchable(),
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
            'index' => ManageGurus::route('/'),
        ];
    }
}
