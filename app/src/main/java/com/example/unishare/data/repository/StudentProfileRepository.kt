package com.example.unishare.data.repository

import com.example.unishare.data.local.dao.StudentProfileDao
import com.example.unishare.data.local.entity.StudentProfileEntity
import kotlinx.coroutines.flow.Flow

class StudentProfileRepository(private val studentProfileDao: StudentProfileDao) {
    val profile: Flow<StudentProfileEntity?> = studentProfileDao.getProfile()

    suspend fun getProfileDirect(): StudentProfileEntity? = studentProfileDao.getProfileDirect()

    suspend fun saveProfile(profile: StudentProfileEntity) {
        studentProfileDao.insertOrUpdate(profile)
    }

    suspend fun clearProfile() {
        studentProfileDao.deleteProfile()
    }
}
