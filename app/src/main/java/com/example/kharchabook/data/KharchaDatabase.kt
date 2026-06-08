package com.kharchabook.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Expense::class, Book::class],
    version = 2,
    exportSchema = false
)
abstract class KharchaDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: KharchaDatabase? = null

        fun getDatabase(context: Context): KharchaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KharchaDatabase::class.java,
                    "kharcha_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}