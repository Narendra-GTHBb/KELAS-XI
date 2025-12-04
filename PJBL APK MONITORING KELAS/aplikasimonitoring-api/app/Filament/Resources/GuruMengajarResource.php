<?php

namespace App\Filament\Resources;

use App\Filament\Resources\GuruMengajarResource\Pages\ManageGuruMengajars;
use App\Models\GuruMengajar;
use App\Models\Kelas;
use App\Models\Guru;
use Filament\Actions\BulkActionGroup;
use Filament\Actions\DeleteAction;
use Filament\Actions\DeleteBulkAction;
use Filament\Actions\EditAction;
use Filament\Forms\Components\Select;
use Filament\Forms\Components\Textarea;
use Filament\Forms\Components\DatePicker;
use Filament\Schemas\Components\Section;
use Filament\Schemas\Components\Grid;
use Filament\Resources\Resource;
use Filament\Schemas\Schema;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Table;
use Filament\Tables\Filters\SelectFilter;

class GuruMengajarResource extends Resource
{
    protected static ?string $model = GuruMengajar::class;

    protected static string|\BackedEnum|null $navigationIcon = 'heroicon-o-clipboard-document-check';
    
    protected static ?string $navigationLabel = 'Guru Mengajar';

    protected static ?string $pluralModelLabel = 'Guru Mengajar';

    public static function form(Schema $schema): Schema
    {
        return $schema
            ->components([
                Select::make('jadwal_id')
                    ->label('Jadwal')
                    ->relationship('jadwal', 'id')
                    ->getOptionLabelFromRecordUsing(fn ($record) => 
                        ($record->guru?->guru ?? 'N/A') . ' - ' . 
                        ($record->mapel?->mapel ?? 'N/A') . ' - ' . 
                        ($record->kelas?->kelas ?? 'N/A') . ' - Jam ' . 
                        $record->jam_ke . ' (' . $record->hari . ')'
                    )
                    ->preload()
                    ->searchable()
                    ->required(),
                Textarea::make('keterangan')
                    ->label('Keterangan')
                    ->rows(3),
                Select::make('status')
                    ->label('Status')
                    ->options([
                        'Masuk' => 'Masuk',
                        'Tidak Masuk' => 'Tidak Masuk',
                        'Izin' => 'Izin',
                    ])
                    ->required()
                    ->default('Tidak Masuk')
                    ->live(),
                Select::make('guru_pengganti_id')
                    ->label('Guru Pengganti')
                    ->relationship('guruPengganti', 'guru')
                    ->placeholder('Pilih Guru Pengganti')
                    ->searchable()
                    ->preload(),
                Section::make('Durasi Izin')
                    ->description('Atur rentang tanggal izin guru (untuk izin panjang)')
                    ->schema([
                        Grid::make(2)
                            ->schema([
                                DatePicker::make('tanggal_mulai_izin')
                                    ->label('Tanggal Mulai Izin')
                                    ->native(false)
                                    ->displayFormat('d/m/Y')
                                    ->placeholder('Pilih tanggal mulai'),
                                DatePicker::make('tanggal_selesai_izin')
                                    ->label('Tanggal Selesai Izin')
                                    ->native(false)
                                    ->displayFormat('d/m/Y')
                                    ->placeholder('Pilih tanggal selesai')
                                    ->afterOrEqual('tanggal_mulai_izin'),
                            ]),
                    ])
                    ->visible(fn ($get) => $get('status') === 'Izin'),
            ]);
    }

    public static function table(Table $table): Table
    {
        return $table
            ->columns([
                TextColumn::make('jadwal.guru.guru')
                    ->label('Nama Guru')
                    ->searchable()
                    ->sortable(),
                TextColumn::make('jadwal.mapel.mapel')
                    ->label('Mata Pelajaran')
                    ->searchable()
                    ->sortable(),
                TextColumn::make('jadwal.kelas.kelas')
                    ->label('Kelas')
                    ->searchable()
                    ->sortable(),
                TextColumn::make('jadwal.jam_ke')
                    ->label('Jam Ke')
                    ->sortable(),
                TextColumn::make('jadwal.hari')
                    ->label('Hari')
                    ->sortable(),
                TextColumn::make('status')
                    ->label('Status')
                    ->badge()
                    ->color(fn (string $state): string => match ($state) {
                        'Masuk' => 'success',
                        'Tidak Masuk' => 'danger',
                        'Izin' => 'warning',
                        default => 'gray',
                    }),
                TextColumn::make('guruPengganti.guru')
                    ->label('Guru Pengganti')
                    ->placeholder('-')
                    ->searchable(),
                TextColumn::make('durasi_izin_display')
                    ->label('Durasi Izin')
                    ->state(function ($record) {
                        if ($record->tanggal_mulai_izin && $record->tanggal_selesai_izin) {
                            $mulai = $record->tanggal_mulai_izin->format('d/m/Y');
                            $selesai = $record->tanggal_selesai_izin->format('d/m/Y');
                            $durasi = $record->durasi_izin;
                            return "{$mulai} - {$selesai} ({$durasi} hari)";
                        }
                        return '-';
                    })
                    ->wrap(),
                TextColumn::make('keterangan')
                    ->label('Keterangan')
                    ->limit(30),
                TextColumn::make('created_at')
                    ->label('Tanggal')
                    ->dateTime('d M Y H:i')
                    ->sortable(),
            ])
            ->filters([
                SelectFilter::make('kelas')
                    ->label('Filter Kelas')
                    ->relationship('jadwal.kelas', 'kelas')
                    ->placeholder('Semua Kelas')
                    ->preload(),
                SelectFilter::make('hari')
                    ->label('Filter Hari')
                    ->options([
                        'Senin' => 'Senin',
                        'Selasa' => 'Selasa',
                        'Rabu' => 'Rabu',
                        'Kamis' => 'Kamis',
                        'Jumat' => 'Jumat',
                    ])
                    ->query(function ($query, array $data) {
                        if (!empty($data['value'])) {
                            return $query->whereHas('jadwal', function ($q) use ($data) {
                                $q->where('hari', $data['value']);
                            });
                        }
                        return $query;
                    })
                    ->placeholder('Semua Hari'),
                SelectFilter::make('guru')
                    ->label('Filter Guru')
                    ->relationship('jadwal.guru', 'guru')
                    ->placeholder('Semua Guru')
                    ->preload()
                    ->searchable(),
                SelectFilter::make('status')
                    ->label('Filter Status')
                    ->options([
                        'Masuk' => 'Masuk',
                        'Tidak Masuk' => 'Tidak Masuk',
                    ])
                    ->placeholder('Semua Status'),
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
            'index' => ManageGuruMengajars::route('/'),
        ];
    }
}
