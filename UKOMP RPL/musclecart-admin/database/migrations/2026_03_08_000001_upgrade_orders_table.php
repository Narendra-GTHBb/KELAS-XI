<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        // Add tracking_number + completed_at to orders
        Schema::table('orders', function (Blueprint $table) {
            // Expand status enum to include 'paid' and 'completed'
            $table->string('tracking_number')->nullable()->after('notes');
            $table->string('courier')->nullable()->after('tracking_number');
            $table->timestamp('paid_at')->nullable()->after('courier');
            $table->timestamp('completed_at')->nullable()->after('paid_at');
            $table->softDeletes();
        });

        // Create order_status_histories table
        Schema::create('order_status_histories', function (Blueprint $table) {
            $table->id();
            $table->foreignId('order_id')->constrained()->onDelete('cascade');
            $table->string('status');
            $table->string('previous_status')->nullable();
            $table->text('note')->nullable();
            $table->unsignedBigInteger('changed_by')->nullable(); // user/admin id
            $table->string('changed_by_role')->nullable(); // 'user', 'admin'
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('order_status_histories');
        Schema::table('orders', function (Blueprint $table) {
            $table->dropColumn(['tracking_number', 'courier', 'paid_at', 'completed_at', 'deleted_at']);
        });
    }
};
