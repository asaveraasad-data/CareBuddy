package com.carebuddy.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val mood: String,           // "Great", "Good", "Okay", "Sad"
    val moodScore: Int,         // 5, 4, 3, 2
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
