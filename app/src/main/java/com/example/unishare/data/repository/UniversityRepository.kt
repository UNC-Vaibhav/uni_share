package com.example.unishare.data.repository

import com.example.unishare.data.local.dao.UniversityDao
import com.example.unishare.data.local.entity.UniversityEntity
import kotlinx.coroutines.flow.Flow

class UniversityRepository(private val universityDao: UniversityDao) {
    val universities: Flow<List<UniversityEntity>> = universityDao.getAllUniversities()

    fun getUniversity(id: String): Flow<UniversityEntity?> = universityDao.getUniversityById(id)

    suspend fun getUniversityDirect(id: String): UniversityEntity? = universityDao.getUniversityByIdDirect(id)
}
