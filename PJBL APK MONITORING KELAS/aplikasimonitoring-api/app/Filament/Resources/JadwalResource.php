<?php

namespace App\Filament\Resources;

use App\Filament\Resources\JadwalResource\Pages\ManageJadwals;
use App\Models\Jadwal;
use App\Models\Guru;
use App\Models\Mapel;
use App\Models\TahunAjaran;
use App\Models\Kelas;
use BackedEnum;
use Filament\Actions\BulkActionGroup;
use Filament\Actions\DeleteAction;
use Filament\Actions\DeleteBulkAction;
use Filament\Actions\EditAction;
use Filament\Forms\Components\TextInput;
use Filament\Forms\Components\Select;
use Filament\Resources\Resource;
use Filament\Schemas\Schema;
use Filament\Support\Icons\Heroicon;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Table;
use Filament\Tables\Filters\SelectFilter;

class JadwalResource extends Resource
{
    protected static ?string $model = Jadwal::class;

    protected static string|\BackedEnum|null $navigationIcon = 'heroicon-o-calendar-days';
    
    protected static ?string $navigationLabel = 'Jadwal';

    public static function form(Schema $schema): Schema
    {
        return $schema
            ->components([
                Select::make('guru_id')
                    ->label('Guru')
                    ->options(Guru::all()->pluck('guru', 'id'))
                    ->searchable()
                    ->required(),
                Select::make('mapel_id')
                    ->label('Mata Pelajaran')
                    ->options(Mapel::all()->pluck('mapel', 'id'))
                    ->searchable()
                    ->required(),
                Select::make('tahun_ajaran_id')
                    ->label('Tahun Ajaran')
                    ->options(TahunAjaran::all()->pluck('tahun', 'id'))
                    ->searchable()
                    ->required(),
                Select::make('kelas_id')
                    ->label('Kelas')
                    ->options(Kelas::all()->pluck('kelas', 'id'))
                    ->searchable()
                    ->required(),
                TextInput::make('jam_ke')
                    ->label('Jam Ke')
                    ->placeholder('Contoh: Jam Ke 1-2')
                    ->required()
                    ->maxLength(255),
                Select::make('hari')
                    ->label('Hari')
                    ->options([
                        'Senin' => 'Senin',
                        'Selasa' => 'Selasa',
                        'Rabu' => 'Rabu',
                        'Kamis' => 'Kamis',
                        'Jumat' => 'Jumat',
                    ])
                    ->required(),
            ]);
    }

    public static function table(Table $table): Table
    {
        return $table
            ->columns([
                TextColumn::make('guru.guru')
                    ->label('Nama Guru')
                    ->searchable()
                    ->sortable(),
                TextColumn::make('mapel.mapel')
                    ->label('Mapel')
                    ->searchable()
                    ->sortable(),
                TextColumn::make('tahunAjaran.tahun')
                    ->label('Tahun')
                    ->searchable()
                    ->sortable(),
                TextColumn::make('kelas.kelas')
                    ->label('Kelas')
                    ->searchable()
                    ->sortable(),
                TextColumn::make('jam_ke')
                    ->label('Jam Ke')
                    ->searchable(),
                TextColumn::make('hari')
                    ->label('Hari')
                    ->searchable(),
            ])
            ->filters([
                SelectFilter::make('kelas_id')
                    ->label('Filter Kelas')
                    ->options(Kelas::all()->pluck('kelas', 'id'))
                    ->placeholder('Semua Kelas'),
                SelectFilter::make('hari')
                    ->label('Filter Hari')
                    ->options([
                        'Senin' => 'Senin',
                        'Selasa' => 'Selasa',
                        'Rabu' => 'Rabu',
                        'Kamis' => 'Kamis',
                        'Jumat' => 'Jumat',
                    ])
                    ->placeholder('Semua Hari'),
                SelectFilter::make('guru_id')
                    ->label('Filter Guru')
                    ->options(Guru::all()->pluck('guru', 'id'))
                    ->placeholder('Semua Guru'),
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
            'index' => ManageJadwals::route('/'),
        ];
    }
}
