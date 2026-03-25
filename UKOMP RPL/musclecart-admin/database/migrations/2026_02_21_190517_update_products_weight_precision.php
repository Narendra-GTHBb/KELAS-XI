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
        Schema::table('products', function (Blueprint $table) {
            // Change weight precision from decimal(8,2) to decimal(8,3)
            // This allows storing weights like 0.912 kg (912 grams) without rounding
            $table->decimal('weight', 8, 3)->nullable()->change();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('products', function (Blueprint $table) {
            // Revert back to decimal(8,2)
            $table->decimal('weight', 8, 2)->nullable()->change();
        });
    }
};
