package com.carebuddy.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fullName: String,
    val email: String,
    val password: String,
    val role: String,           // "Senior", "Family", "Doctor"
    val age: Int = 0,
    val bloodGroup: String = "",
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val emergencyContactRelation: String = "",
    val medicalConditions: String = "",   // comma-separated
    val allergies: String = "",           // comma-separated
    val currentMedications: String = "",  // comma-separated
    val profileImagePath: String = ""
)
