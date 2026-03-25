<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    public function up(): void
    {
        // Expand the status ENUM to include 'paid' and 'completed' which were
        // referenced in code but missing from the original migration definition.
        DB::statement("
            ALTER TABLE orders
            MODIFY COLUMN status
            ENUM('pending','paid','confirmed','processing','shipped','delivered','completed','cancelled')
            NOT NULL DEFAULT 'pending'
        ");
    }

    public function down(): void
    {
        // Revert to original enum values (data with new statuses will be truncated)
        DB::statement("
            ALTER TABLE orders
            MODIFY COLUMN status
            ENUM('pending','confirmed','processing','shipped','delivered','cancelled')
            NOT NULL DEFAULT 'pending'
        ");
    }
};
