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
        // Update NULL nama dengan email
        DB::table('users')->whereNull('nama')->update(['nama' => DB::raw('email')]);
        
        // Ubah kolom menjadi NOT NULL dengan default
        Schema::table('users', function (Blueprint $table) {
            $table->string('nama')->default('User')->change();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('users', function (Blueprint $table) {
            $table->string('nama')->nullable()->change();
        });
    }
};
