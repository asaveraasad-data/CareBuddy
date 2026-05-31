package com.carebuddy.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val name: String,
    val dosage: String,
    val timeSlot: String,       // "Morning", "Afternoon", "Evening", "Night"
    val timeValue: String,      // "08:00", "14:00", "18:00", "22:00"
    val repeatType: String,     // "Every Day", "Weekly", "Alternate Days", "Custom"
    val startDate: String,
    val endDate: String,
    val reminderEnabled: Boolean = true,
    val isActive: Boolean = true
)
