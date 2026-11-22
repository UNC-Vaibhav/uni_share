package com.example.unishare.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.unishare.data.local.entity.UniversityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UniversityDao {
    @Query("SELECT * FROM universities ORDER BY name ASC")
    fun getAllUniversities(): Flow<List<UniversityEntity>>

    @Query("SELECT * FROM universities WHERE id = :id LIMIT 1")
    fun getUniversityById(id: String): Flow<UniversityEntity?>

    @Query("SELECT * FROM universities WHERE id = :id LIMIT 1")
    suspend fun getUniversityByIdDirect(id: String): UniversityEntity?

    @Query("SELECT COUNT(*) FROM universities")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUniversities(universities: List<UniversityEntity>): List<Long>
}
