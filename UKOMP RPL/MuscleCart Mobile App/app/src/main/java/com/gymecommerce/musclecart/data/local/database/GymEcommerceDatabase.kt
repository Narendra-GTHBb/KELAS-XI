package com.gymecommerce.musclecart.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.gymecommerce.musclecart.data.local.dao.*
import com.gymecommerce.musclecart.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        ProductEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        CartItemEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class GymEcommerceDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun cartDao(): CartDao
    
    companion object {
        const val DATABASE_NAME = "gym_ecommerce_database"
        
        @Volatile
        private var INSTANCE: GymEcommerceDatabase? = null
        
        fun getDatabase(context: Context): GymEcommerceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GymEcommerceDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(*getAllMigrations())
                    .fallbackToDestructiveMigration() // Only for development
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        private fun getAllMigrations(): Array<Migration> {
            return arrayOf(
                MIGRATION_2_3
            )
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE products ADD COLUMN avgRating REAL NOT NULL DEFAULT 0.0")
                database.execSQL("ALTER TABLE products ADD COLUMN totalReviews INTEGER NOT NULL DEFAULT 0")
            }
        }
        
        // Helper method to clear database (for testing/development)
        suspend fun clearDatabase(context: Context) {
            val database = getDatabase(context)
            database.clearAllTables()
        }
    }
}