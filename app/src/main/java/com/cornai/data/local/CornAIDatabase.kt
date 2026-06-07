package com.cornai.data.local

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val diseaseName: String,
    val displayName: String,
    val confidence: Float,
    val isHealthy: Boolean,
    val imageUri: String,
    val symptoms: String, // JSON string
    val treatment: String,
    val prevention: String, // JSON string
    val severity: String,
    val recoveryTime: String,
    val timestamp: Long,
    val location: String,
    val weather: String,
    val isSynced: Boolean
)

@Dao
interface ScanHistoryDao {
    @Query("SELECT * FROM scan_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getHistoryByUser(userId: String): Flow<List<ScanHistoryEntity>>

    @Query("SELECT * FROM scan_history WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getHistoryByUserSync(userId: String): List<ScanHistoryEntity>

    @Query("SELECT * FROM scan_history WHERE isSynced = 0 AND userId = :userId")
    suspend fun getUnsyncedHistory(userId: String): List<ScanHistoryEntity>

    @Query("SELECT * FROM scan_history WHERE id = :id")
    suspend fun getById(id: String): ScanHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: ScanHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(histories: List<ScanHistoryEntity>)

    @Update
    suspend fun update(history: ScanHistoryEntity)

    @Query("DELETE FROM scan_history WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM scan_history WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: String)

    @Query("UPDATE scan_history SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)

    @Query("SELECT COUNT(*) FROM scan_history WHERE userId = :userId")
    suspend fun getCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM scan_history WHERE userId = :userId AND isHealthy = 1")
    suspend fun getHealthyCount(userId: String): Int

    @Query("SELECT COUNT(*) FROM scan_history WHERE userId = :userId AND isHealthy = 0")
    suspend fun getDiseaseCount(userId: String): Int
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val name: String,
    val phone: String,
    val password: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)
}

@Database(entities = [ScanHistoryEntity::class, UserEntity::class], version = 2, exportSchema = false)
abstract class CornAIDatabase : RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun userDao(): UserDao
}