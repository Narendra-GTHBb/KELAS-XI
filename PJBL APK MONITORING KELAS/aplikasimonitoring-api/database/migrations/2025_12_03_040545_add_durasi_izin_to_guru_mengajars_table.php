<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::table('guru_mengajars', function (Blueprint $table) {
            $table->date('tanggal_mulai_izin')->nullable()->after('guru_pengganti_id');
            $table->date('tanggal_selesai_izin')->nullable()->after('tanggal_mulai_izin');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('guru_mengajars', function (Blueprint $table) {
            $table->dropColumn(['tanggal_mulai_izin', 'tanggal_selesai_izin']);
        });
    }
};
