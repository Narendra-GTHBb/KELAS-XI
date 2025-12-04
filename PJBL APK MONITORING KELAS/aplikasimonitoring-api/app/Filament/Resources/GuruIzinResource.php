<?php

namespace App\Filament\Resources;

use App\Filament\Resources\GuruIzinResource\Pages;
use App\Models\GuruIzin;
use Filament\Actions\BulkActionGroup;
use Filament\Actions\DeleteAction;
use Filament\Actions\DeleteBulkAction;
use Filament\Actions\EditAction;
use Filament\Actions\Action;
use Filament\Forms\Components\Select;
use Filament\Forms\Components\DatePicker;
use Filament\Forms\Components\Textarea;
use Filament\Resources\Resource;
use Filament\Schemas\Schema;
use Filament\Tables\Table;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Filters\SelectFilter;
use Illuminate\Support\Facades\Auth;

class GuruIzinResource extends Resource
{
    protected static ?string $model = GuruIzin::class;

    protected static string|\BackedEnum|null $navigationIcon = 'heroicon-o-calendar-days';
    
    protected static ?string $navigationLabel = 'Izin Guru';
    
    protected static ?string $modelLabel = 'Izin Guru';
    
    protected static ?string $pluralModelLabel = 'Izin Guru';
    
    protected static string|\UnitEnum|null $navigationGroup = 'Monitoring';
    
    protected static ?int $navigationSort = 1;

    public static function form(Schema $schema): Schema
    {
        return $schema
            ->components([
                Select::make('guru_id')
                    ->label('Guru')
                    ->relationship('guru', 'guru')
                    ->searchable()
                    ->preload()
                    ->required(),
                DatePicker::make('tanggal')
                    ->label('Tanggal Izin')
                    ->required(),
                Select::make('jenis_izin')
                    ->label('Jenis Izin')
                    ->options([
                        'sakit' => 'Sakit',
                        'izin' => 'Izin',
                        'cuti' => 'Cuti',
                        'dinas_luar' => 'Dinas Luar',
                        'lainnya' => 'Lainnya',
                    ])
                    ->required(),
                Textarea::make('keterangan')
                    ->label('Keterangan')
                    ->rows(3),
                Select::make('status')
                    ->label('Status')
                    ->options([
                        'pending' => 'Pending',
                        'disetujui' => 'Disetujui',
                        'ditolak' => 'Ditolak',
                    ])
                    ->default('pending')
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
                TextColumn::make('tanggal')
                    ->label('Tanggal')
                    ->date('d M Y')
                    ->sortable(),
                TextColumn::make('jenis_izin')
                    ->label('Jenis')
                    ->badge()
                    ->color(fn (string $state): string => match ($state) {
                        'sakit' => 'danger',
                        'izin' => 'warning',
                        'cuti' => 'info',
                        'dinas_luar' => 'primary',
                        default => 'gray',
                    }),
                TextColumn::make('status')
                    ->label('Status')
                    ->badge()
                    ->color(fn (string $state): string => match ($state) {
                        'pending' => 'warning',
                        'disetujui' => 'success',
                        'ditolak' => 'danger',
                        default => 'gray',
                    }),
                TextColumn::make('approvedBy.nama')
                    ->label('Disetujui Oleh')
                    ->placeholder('-'),
                TextColumn::make('approved_at')
                    ->label('Waktu Approval')
                    ->dateTime('d M Y H:i')
                    ->placeholder('-'),
            ])
            ->filters([
                SelectFilter::make('status')
                    ->options([
                        'pending' => 'Pending',
                        'disetujui' => 'Disetujui',
                        'ditolak' => 'Ditolak',
                    ]),
                SelectFilter::make('jenis_izin')
                    ->options([
                        'sakit' => 'Sakit',
                        'izin' => 'Izin',
                        'cuti' => 'Cuti',
                        'dinas_luar' => 'Dinas Luar',
                        'lainnya' => 'Lainnya',
                    ]),
            ])
            ->recordActions([
                EditAction::make(),
                Action::make('approve')
                    ->label('Setujui')
                    ->icon('heroicon-o-check-circle')
                    ->color('success')
                    ->requiresConfirmation()
                    ->visible(fn ($record) => $record->status === 'pending')
                    ->action(function ($record) {
                        $record->update([
                            'status' => 'disetujui',
                            'approved_by' => Auth::id(),
                            'approved_at' => now(),
                        ]);
                    }),
                Action::make('reject')
                    ->label('Tolak')
                    ->icon('heroicon-o-x-circle')
                    ->color('danger')
                    ->requiresConfirmation()
                    ->visible(fn ($record) => $record->status === 'pending')
                    ->action(function ($record) {
                        $record->update([
                            'status' => 'ditolak',
                            'approved_by' => Auth::id(),
                            'approved_at' => now(),
                        ]);
                    }),
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
            'index' => Pages\ManageGuruIzins::route('/'),
        ];
    }
}
