package com.gymecommerce.musclecart.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {
    
    // Example migration from version 1 to 2
    // This will be used when we need to update the database schema
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Example: Add a new column to products table
            // database.execSQL("ALTER TABLE products ADD COLUMN is_featured INTEGER NOT NULL DEFAULT 0")
        }
    }
    
    // Example migration from version 2 to 3
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Example: Create a new table
            // database.execSQL("CREATE TABLE IF NOT EXISTS `reviews` ...")
        }
    }
    
    // Add all migrations to this array when needed
    val ALL_MIGRATIONS = arrayOf<Migration>(
        // MIGRATION_1_2,
        // MIGRATION_2_3
    )
}