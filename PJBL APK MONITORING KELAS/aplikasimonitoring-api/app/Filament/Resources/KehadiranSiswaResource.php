<?php

namespace App\Filament\Resources;

use App\Filament\Resources\KehadiranSiswaResource\Pages;
use App\Models\KehadiranSiswa;
use Filament\Actions\BulkActionGroup;
use Filament\Actions\DeleteAction;
use Filament\Actions\DeleteBulkAction;
use Filament\Actions\EditAction;
use Filament\Forms\Components\Select;
use Filament\Forms\Components\DatePicker;
use Filament\Forms\Components\TextInput;
use Filament\Forms\Components\Textarea;
use Filament\Resources\Resource;
use Filament\Schemas\Schema;
use Filament\Tables\Table;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Filters\SelectFilter;

class KehadiranSiswaResource extends Resource
{
    protected static ?string $model = KehadiranSiswa::class;

    protected static string|\BackedEnum|null $navigationIcon = 'heroicon-o-user-group';
    
    protected static ?string $navigationLabel = 'Kehadiran Siswa';
    
    protected static ?string $modelLabel = 'Kehadiran Siswa';
    
    protected static ?string $pluralModelLabel = 'Kehadiran Siswa';
    
    protected static string|\UnitEnum|null $navigationGroup = 'Monitoring';
    
    protected static ?int $navigationSort = 3;

    public static function form(Schema $schema): Schema
    {
        return $schema
            ->components([
                Select::make('jadwal_id')
                    ->label('Jadwal')
                    ->relationship('jadwal', 'id')
                    ->getOptionLabelFromRecordUsing(fn ($record) => 
                        "{$record->hari} - {$record->jam_mulai} - {$record->guruMengajar?->mapel?->mapel}"
                    )
                    ->searchable()
                    ->preload()
                    ->required(),
                Select::make('kelas_id')
                    ->label('Kelas')
                    ->relationship('kelas', 'kelas')
                    ->searchable()
                    ->preload()
                    ->required(),
                DatePicker::make('tanggal')
                    ->label('Tanggal')
                    ->required()
                    ->default(now()),
                TextInput::make('jumlah_hadir')
                    ->label('Jumlah Hadir')
                    ->numeric()
                    ->required()
                    ->default(0),
                TextInput::make('jumlah_sakit')
                    ->label('Jumlah Sakit')
                    ->numeric()
                    ->required()
                    ->default(0),
                TextInput::make('jumlah_izin')
                    ->label('Jumlah Izin')
                    ->numeric()
                    ->required()
                    ->default(0),
                TextInput::make('jumlah_alpha')
                    ->label('Jumlah Alpha')
                    ->numeric()
                    ->required()
                    ->default(0),
                Textarea::make('keterangan')
                    ->label('Keterangan')
                    ->rows(3),
            ]);
    }

    public static function table(Table $table): Table
    {
        return $table
            ->columns([
                TextColumn::make('tanggal')
                    ->label('Tanggal')
                    ->date('d M Y')
                    ->sortable(),
                TextColumn::make('kelas.kelas')
                    ->label('Kelas')
                    ->searchable()
                    ->sortable(),
                TextColumn::make('jadwal.mapel.mapel')
                    ->label('Mapel'),
                TextColumn::make('jumlah_hadir')
                    ->label('Hadir')
                    ->alignCenter()
                    ->badge()
                    ->color('success'),
                TextColumn::make('jumlah_sakit')
                    ->label('Sakit')
                    ->alignCenter()
                    ->badge()
                    ->color('danger'),
                TextColumn::make('jumlah_izin')
                    ->label('Izin')
                    ->alignCenter()
                    ->badge()
                    ->color('warning'),
                TextColumn::make('jumlah_alpha')
                    ->label('Alpha')
                    ->alignCenter()
                    ->badge()
                    ->color('gray'),
                TextColumn::make('reportedBy.nama')
                    ->label('Dilaporkan Oleh')
                    ->placeholder('-'),
            ])
            ->filters([
                SelectFilter::make('kelas_id')
                    ->label('Kelas')
                    ->relationship('kelas', 'kelas'),
            ])
            ->recordActions([
                EditAction::make(),
                DeleteAction::make(),
            ])
            ->toolbarActions([
                BulkActionGroup::make([
                    DeleteBulkAction::make(),
                ]),
            ])
            ->defaultSort('tanggal', 'desc');
    }

    public static function getPages(): array
    {
        return [
            'index' => Pages\ManageKehadiranSiswas::route('/'),
        ];
    }
}
