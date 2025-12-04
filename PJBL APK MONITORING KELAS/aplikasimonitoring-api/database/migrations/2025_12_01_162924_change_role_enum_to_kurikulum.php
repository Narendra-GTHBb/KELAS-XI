<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        // Alter enum column dulu (tambahkan Kurikulum)
        DB::statement("ALTER TABLE users MODIFY COLUMN role ENUM('Siswa', 'Waka Kurikulum', 'Kurikulum', 'Kepala Sekolah', 'Admin') DEFAULT 'Admin'");
        
        // Baru update existing data
        DB::table('users')->where('role', 'Waka Kurikulum')->update(['role' => 'Kurikulum']);
        
        // Hapus Waka Kurikulum dari enum
        DB::statement("ALTER TABLE users MODIFY COLUMN role ENUM('Siswa', 'Kurikulum', 'Kepala Sekolah', 'Admin') DEFAULT 'Admin'");
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        // Revert data
        DB::table('users')->where('role', 'Kurikulum')->update(['role' => 'Waka Kurikulum']);
        
        // Revert enum
        DB::statement("ALTER TABLE users MODIFY COLUMN role ENUM('Siswa', 'Waka Kurikulum', 'Kepala Sekolah', 'Admin') DEFAULT 'Admin'");
    }
};
