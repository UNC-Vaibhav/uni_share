package com.example.unishare.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.unishare.data.local.entity.BranchEntity
import com.example.unishare.data.local.entity.SemesterEntity
import com.example.unishare.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademicDao {
    @Query("SELECT * FROM semesters WHERE universityId = :universityId ORDER BY semesterNumber ASC")
    fun getSemesters(universityId: String): Flow<List<SemesterEntity>>

    @Query("SELECT * FROM branches WHERE universityId = :universityId ORDER BY name ASC")
    fun getBranches(universityId: String): Flow<List<BranchEntity>>

    @Query("SELECT * FROM subjects WHERE branchId = :branchId AND semesterId = :semesterId ORDER BY subjectName ASC")
    fun getSubjects(branchId: String, semesterId: String): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :subjectId LIMIT 1")
    fun getSubjectById(subjectId: String): Flow<SubjectEntity?>

    @Query("SELECT * FROM subjects WHERE id = :subjectId LIMIT 1")
    suspend fun getSubjectByIdDirect(subjectId: String): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSemesters(semesters: List<SemesterEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranches(branches: List<BranchEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>): List<Long>
}
