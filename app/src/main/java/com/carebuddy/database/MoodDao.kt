package com.carebuddy.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.carebuddy.models.MoodEntry

@Dao
interface MoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MoodEntry): Long

    @Delete
    suspend fun delete(entry: MoodEntry)

    @Query("SELECT * FROM mood_entries WHERE userId = :userId ORDER BY timestamp DESC")
    fun getMoodsForUser(userId: Int): LiveData<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE userId = :userId ORDER BY timestamp DESC LIMIT 7")
    suspend fun getLast7Moods(userId: Int): List<MoodEntry>

    @Query("SELECT AVG(moodScore) FROM mood_entries WHERE userId = :userId")
    suspend fun getAverageMoodScore(userId: Int): Float?
}
