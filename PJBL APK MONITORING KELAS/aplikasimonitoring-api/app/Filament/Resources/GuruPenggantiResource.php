<?php

namespace App\Filament\Resources;

use App\Filament\Resources\GuruPenggantiResource\Pages;
use App\Models\GuruPengganti;
use Filament\Actions\BulkActionGroup;
use Filament\Actions\DeleteAction;
use Filament\Actions\DeleteBulkAction;
use Filament\Actions\EditAction;
use Filament\Forms\Components\Select;
use Filament\Forms\Components\DatePicker;
use Filament\Forms\Components\Textarea;
use Filament\Resources\Resource;
use Filament\Schemas\Schema;
use Filament\Tables\Table;
use Filament\Tables\Columns\TextColumn;

class GuruPenggantiResource extends Resource
{
    protected static ?string $model = GuruPengganti::class;

    protected static string|\BackedEnum|null $navigationIcon = 'heroicon-o-arrow-path';
    
    protected static ?string $navigationLabel = 'Guru Pengganti';
    
    protected static ?string $modelLabel = 'Guru Pengganti';
    
    protected static ?string $pluralModelLabel = 'Guru Pengganti';
    
    protected static string|\UnitEnum|null $navigationGroup = 'Monitoring';
    
    protected static ?int $navigationSort = 2;

    public static function form(Schema $schema): Schema
    {
        return $schema
            ->components([
                Select::make('jadwal_id')
                    ->label('Jadwal')
                    ->relationship('jadwal', 'id')
                    ->getOptionLabelFromRecordUsing(fn ($record) => 
                        "{$record->hari} - {$record->jam_mulai} - {$record->guruMengajar?->mapel?->mapel} ({$record->guruMengajar?->kelas?->kelas})"
                    )
                    ->searchable()
                    ->preload()
                    ->required(),
                Select::make('guru_izin_id')
                    ->label('Referensi Izin')
                    ->relationship('guruIzin', 'id')
                    ->getOptionLabelFromRecordUsing(fn ($record) => 
                        "{$record->guru?->guru} - {$record->tanggal->format('d M Y')} ({$record->jenis_izin})"
                    )
                    ->searchable()
                    ->preload(),
                Select::make('guru_asli_id')
                    ->label('Guru Asli')
                    ->relationship('guruAsli', 'guru')
                    ->searchable()
                    ->preload()
                    ->required(),
                Select::make('guru_pengganti_id')
                    ->label('Guru Pengganti')
                    ->relationship('guruPengganti', 'guru')
                    ->searchable()
                    ->preload()
                    ->required(),
                DatePicker::make('tanggal')
                    ->label('Tanggal')
                    ->required(),
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
                TextColumn::make('jadwal.hari')
                    ->label('Hari'),
                TextColumn::make('jadwal.jam_mulai')
                    ->label('Jam'),
                TextColumn::make('jadwal.mapel.mapel')
                    ->label('Mapel'),
                TextColumn::make('jadwal.kelas.kelas')
                    ->label('Kelas'),
                TextColumn::make('guruAsli.guru')
                    ->label('Guru Asli')
                    ->searchable(),
                TextColumn::make('guruPengganti.guru')
                    ->label('Guru Pengganti')
                    ->searchable(),
                TextColumn::make('createdBy.nama')
                    ->label('Dibuat Oleh')
                    ->placeholder('-'),
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
            ])
            ->defaultSort('tanggal', 'desc');
    }

    public static function getPages(): array
    {
        return [
            'index' => Pages\ManageGuruPenggantis::route('/'),
        ];
    }
}
