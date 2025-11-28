package com.guardian.gamex.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BenchmarkResult::class], version = 1, exportSchema = false)
abstract class BenchmarkDatabase : RoomDatabase() {
    abstract fun benchmarkDao(): BenchmarkDao

    companion object {
        @Volatile
        private var INSTANCE: BenchmarkDatabase? = null

        fun getDatabase(context: Context): BenchmarkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BenchmarkDatabase::class.java,
                    "gamex_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
