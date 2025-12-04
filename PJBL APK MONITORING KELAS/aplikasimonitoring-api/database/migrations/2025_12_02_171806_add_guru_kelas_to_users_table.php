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
        Schema::table('users', function (Blueprint $table) {
            $table->foreignId('guru_id')->nullable()->after('role')->constrained('gurus')->nullOnDelete();
            $table->foreignId('kelas_id')->nullable()->after('guru_id')->constrained('kelas')->nullOnDelete();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('users', function (Blueprint $table) {
            $table->dropForeign(['guru_id']);
            $table->dropForeign(['kelas_id']);
            $table->dropColumn(['guru_id', 'kelas_id']);
        });
    }
};
