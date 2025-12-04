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
        Schema::create('guru_penggantis', function (Blueprint $table) {
            $table->id();
            $table->foreignId('jadwal_id')->constrained('jadwals')->cascadeOnDelete();
            $table->foreignId('guru_izin_id')->nullable()->constrained('guru_izins')->nullOnDelete();
            $table->foreignId('guru_asli_id')->constrained('gurus')->cascadeOnDelete();
            $table->foreignId('guru_pengganti_id')->constrained('gurus')->cascadeOnDelete();
            $table->date('tanggal');
            $table->text('keterangan')->nullable();
            $table->foreignId('created_by')->nullable()->constrained('users')->nullOnDelete();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('guru_penggantis');
    }
};
