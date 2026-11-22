package com.example.unishare.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.unishare.data.local.entity.StudentProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentProfileDao {
    @Query("SELECT * FROM student_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<StudentProfileEntity?>

    @Query("SELECT * FROM student_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileDirect(): StudentProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: StudentProfileEntity): Long

    @Query("DELETE FROM student_profile")
    suspend fun deleteProfile(): Int
}
