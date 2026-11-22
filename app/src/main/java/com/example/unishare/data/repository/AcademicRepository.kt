package com.example.unishare.data.repository

import com.example.unishare.data.local.dao.AcademicDao
import com.example.unishare.data.local.entity.BranchEntity
import com.example.unishare.data.local.entity.SemesterEntity
import com.example.unishare.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

class AcademicRepository(private val academicDao: AcademicDao) {
    fun getSemesters(universityId: String): Flow<List<SemesterEntity>> =
        academicDao.getSemesters(universityId)

    fun getBranches(universityId: String): Flow<List<BranchEntity>> =
        academicDao.getBranches(universityId)

    fun getSubjects(branchId: String, semesterId: String): Flow<List<SubjectEntity>> =
        academicDao.getSubjects(branchId, semesterId)

    fun getSubject(subjectId: String): Flow<SubjectEntity?> =
        academicDao.getSubjectById(subjectId)

    suspend fun getSubjectDirect(subjectId: String): SubjectEntity? =
        academicDao.getSubjectByIdDirect(subjectId)
}
