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
            $table->foreignId('guru_pengganti_id')->nullable()->after('keterangan')->constrained('gurus')->nullOnDelete();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('guru_mengajars', function (Blueprint $table) {
            $table->dropForeign(['guru_pengganti_id']);
            $table->dropColumn('guru_pengganti_id');
        });
    }
};
