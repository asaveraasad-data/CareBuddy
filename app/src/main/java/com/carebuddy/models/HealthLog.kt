package com.carebuddy.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_logs")
data class HealthLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val metricType: String,     // "Blood Pressure", "Blood Sugar", "Heart Rate", "Temperature"
    val value: String,          // e.g. "120/80", "95", "72", "98.6"
    val unit: String,           // "mmHg", "mg/dL", "bpm", "°F"
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
