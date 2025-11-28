package com.guardian.gamex.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BenchmarkDao {
    @Insert
    suspend fun insert(result: BenchmarkResult)

    @Query("SELECT * FROM benchmark_results ORDER BY timestamp DESC LIMIT 20")
    fun getRecentResults(): Flow<List<BenchmarkResult>>

    @Query("SELECT * FROM benchmark_results WHERE profileUsed = :profile ORDER BY timestamp DESC LIMIT 5")
    fun getResultsByProfile(profile: String): Flow<List<BenchmarkResult>>

    @Query("DELETE FROM benchmark_results")
    suspend fun clearAll()
}