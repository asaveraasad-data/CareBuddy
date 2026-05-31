package com.carebuddy.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.carebuddy.models.HealthLog

@Dao
interface HealthLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: HealthLog): Long

    @Update
    suspend fun update(log: HealthLog)

    @Delete
    suspend fun delete(log: HealthLog)

    @Query("SELECT * FROM health_logs WHERE userId = :userId ORDER BY timestamp DESC")
    fun getLogsForUser(userId: Int): LiveData<List<HealthLog>>

    @Query("SELECT * FROM health_logs WHERE userId = :userId AND metricType = :type ORDER BY timestamp DESC")
    fun getLogsByType(userId: Int, type: String): LiveData<List<HealthLog>>

    @Query("SELECT * FROM health_logs WHERE userId = :userId AND metricType = :type ORDER BY timestamp DESC LIMIT 7")
    suspend fun getLast7LogsByType(userId: Int, type: String): List<HealthLog>

    @Query("SELECT * FROM health_logs WHERE userId = :userId ORDER BY timestamp DESC LIMIT 10")
    suspend fun getRecentLogs(userId: Int): List<HealthLog>

    @Query("DELETE FROM health_logs WHERE id = :id")
    suspend fun deleteById(id: Int)
}
