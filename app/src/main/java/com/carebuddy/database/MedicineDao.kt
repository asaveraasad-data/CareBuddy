package com.carebuddy.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.carebuddy.models.Medicine

@Dao
interface MedicineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(medicine: Medicine): Long

    @Update
    suspend fun update(medicine: Medicine)

    @Delete
    suspend fun delete(medicine: Medicine)

    @Query("SELECT * FROM medicines WHERE userId = :userId AND isActive = 1 ORDER BY id DESC")
    fun getMedicinesForUser(userId: Int): LiveData<List<Medicine>>

    @Query("SELECT * FROM medicines WHERE userId = :userId AND isActive = 1")
    suspend fun getMedicinesForUserSync(userId: Int): List<Medicine>

    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getMedicineById(id: Int): Medicine?

    @Query("UPDATE medicines SET isActive = 0 WHERE id = :id")
    suspend fun softDelete(id: Int)

    @Query("SELECT * FROM medicines WHERE userId = :userId AND timeSlot = :timeSlot AND isActive = 1")
    suspend fun getMedicinesByTimeSlot(userId: Int, timeSlot: String): List<Medicine>
}
