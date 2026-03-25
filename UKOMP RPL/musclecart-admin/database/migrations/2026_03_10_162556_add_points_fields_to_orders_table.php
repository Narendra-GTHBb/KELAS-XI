<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('orders', function (Blueprint $table) {
            $table->unsignedBigInteger('points_earned')->default(0)->after('discount_amount');
            $table->unsignedBigInteger('points_used')->default(0)->after('points_earned');
            $table->decimal('final_price', 15, 2)->default(0)->after('points_used');
        });
    }

    public function down(): void
    {
        Schema::table('orders', function (Blueprint $table) {
            $table->dropColumn(['points_earned', 'points_used', 'final_price']);
        });
    }
};
