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
        // Modify ENUM to include 'Izin'
        DB::statement("ALTER TABLE guru_mengajars MODIFY COLUMN status ENUM('Masuk', 'Tidak Masuk', 'Izin') DEFAULT 'Tidak Masuk'");
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        // Revert to original ENUM
        DB::statement("ALTER TABLE guru_mengajars MODIFY COLUMN status ENUM('Masuk', 'Tidak Masuk') DEFAULT 'Tidak Masuk'");
    }
};
