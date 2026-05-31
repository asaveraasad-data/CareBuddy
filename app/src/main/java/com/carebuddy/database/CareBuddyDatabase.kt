package com.carebuddy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.carebuddy.models.*

@Database(
    entities = [User::class, Medicine::class, HealthLog::class, MoodEntry::class],
    version = 1,
    exportSchema = false
)
abstract class CareBuddyDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun medicineDao(): MedicineDao
    abstract fun healthLogDao(): HealthLogDao
    abstract fun moodDao(): MoodDao

    companion object {
        @Volatile
        private var INSTANCE: CareBuddyDatabase? = null

        fun getDatabase(context: Context): CareBuddyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CareBuddyDatabase::class.java,
                    "carebuddy_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
