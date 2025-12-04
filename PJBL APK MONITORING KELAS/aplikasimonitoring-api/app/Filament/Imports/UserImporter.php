<?php

namespace App\Filament\Imports;

use App\Models\User;
use App\Models\Kelas;
use Filament\Actions\Imports\ImportColumn;
use Filament\Actions\Imports\Importer;
use Filament\Actions\Imports\Models\Import;
use Illuminate\Support\Facades\Hash;

class UserImporter extends Importer
{
    protected static ?string $model = User::class;

    public static function getColumns(): array
    {
        return [
            ImportColumn::make('nama')
                ->label('Nama')
                ->requiredMapping()
                ->rules(['required', 'string', 'max:255']),
            ImportColumn::make('username')
                ->label('Username')
                ->requiredMapping()
                ->rules(['required', 'string', 'max:255']),
            ImportColumn::make('email')
                ->label('Email')
                ->requiredMapping()
                ->rules(['required', 'email', 'max:255']),
            ImportColumn::make('password')
                ->label('Password')
                ->requiredMapping()
                ->rules(['required', 'string', 'min:6']),
            ImportColumn::make('role')
                ->label('Role')
                ->requiredMapping()
                ->rules(['required', 'string', 'in:Admin,Siswa,Kurikulum,Kepala Sekolah']),
            ImportColumn::make('kelas')
                ->label('Kelas (untuk Siswa)')
                ->rules(['nullable', 'string']),
        ];
    }

    public function resolveRecord(): ?User
    {
        // Check if user already exists by username or email
        $existingUser = User::where('username', $this->data['username'])
            ->orWhere('email', $this->data['email'])
            ->first();

        if ($existingUser) {
            return $existingUser;
        }

        // Get kelas_id if role is Siswa and kelas is provided
        $kelasId = null;
        if ($this->data['role'] === 'Siswa' && !empty($this->data['kelas'])) {
            $kelas = Kelas::where('kelas', $this->data['kelas'])->first();
            if ($kelas) {
                $kelasId = $kelas->id;
            }
        }

        // Create new user
        $user = new User();
        $user->nama = $this->data['nama'];
        $user->username = $this->data['username'];
        $user->email = $this->data['email'];
        $user->password = Hash::make($this->data['password']);
        $user->role = $this->data['role'];
        $user->kelas_id = $kelasId;

        return $user;
    }

    public function fillRecord(): void
    {
        // Override to prevent filling with raw CSV data
        // We've already set the proper values in resolveRecord
    }

    public static function getCompletedNotificationBody(Import $import): string
    {
        $body = 'Import user selesai. ' . number_format($import->successful_rows) . ' baris berhasil diimport.';

        if ($failedRowsCount = $import->getFailedRowsCount()) {
            $body .= ' ' . number_format($failedRowsCount) . ' baris gagal diimport.';
        }

        return $body;
    }
}
