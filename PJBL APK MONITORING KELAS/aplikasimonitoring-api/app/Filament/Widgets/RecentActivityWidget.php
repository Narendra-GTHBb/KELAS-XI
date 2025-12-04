<?php

namespace App\Filament\Widgets;

use App\Models\GuruMengajar;
use Filament\Tables;
use Filament\Tables\Table;
use Filament\Widgets\TableWidget as BaseWidget;

class RecentActivityWidget extends BaseWidget
{
    protected static ?int $sort = 6;
    
    protected int | string | array $columnSpan = 'full';
    
    public function getHeading(): ?string
    {
        return 'Aktivitas Kehadiran Terbaru';
    }
    
    public function table(Table $table): Table
    {
        return $table
            ->query(
                GuruMengajar::query()
                    ->with(['guru', 'mapel', 'kelas', 'jadwal'])
                    ->latest()
            )
            ->columns([
                Tables\Columns\TextColumn::make('created_at')
                    ->label('Tanggal')
                    ->dateTime('d/m/Y H:i')
                    ->sortable(),
                    
                Tables\Columns\TextColumn::make('guru.nama')
                    ->label('Nama Guru')
                    ->searchable(),
                    
                Tables\Columns\TextColumn::make('mapel.nama')
                    ->label('Mata Pelajaran')
                    ->searchable(),
                    
                Tables\Columns\TextColumn::make('kelas.nama')
                    ->label('Kelas')
                    ->searchable(),
                    
                Tables\Columns\TextColumn::make('jadwal.hari')
                    ->label('Hari'),
                    
                Tables\Columns\TextColumn::make('jadwal.jam_mulai')
                    ->label('Jam')
                    ->formatStateUsing(fn ($state, $record) => 
                        $record->jadwal ? $record->jadwal->jam_mulai . ' - ' . $record->jadwal->jam_selesai : '-'
                    ),
                    
                Tables\Columns\TextColumn::make('status')
                    ->label('Status')
                    ->badge()
                    ->color(fn (string $state): string => match ($state) {
                        'Masuk' => 'success',
                        'Tidak Masuk' => 'danger',
                        default => 'gray',
                    }),
            ])
            ->defaultPaginationPageOption(5)
            ->defaultSort('created_at', 'desc');
    }
}
