package com.example.unishare

import android.app.Application
import com.example.unishare.data.local.database.AppDatabase
import com.example.unishare.data.repository.AcademicRepository
import com.example.unishare.data.repository.DocumentRepository
import com.example.unishare.data.repository.StudentProfileRepository
import com.example.unishare.data.repository.UniversityRepository
import com.example.unishare.data.storage.FileStorageManager
import com.example.unishare.data.storage.InitialDataSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UniShareApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { AppDatabase.getInstance(this) }
    val fileStorageManager by lazy { FileStorageManager(this) }

    val universityRepository by lazy { UniversityRepository(database.universityDao()) }
    val studentProfileRepository by lazy { StudentProfileRepository(database.studentProfileDao()) }
    val academicRepository by lazy { AcademicRepository(database.academicDao()) }
    val documentRepository by lazy { DocumentRepository(database.documentDao(), fileStorageManager) }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            val seeder = InitialDataSeeder(this@UniShareApplication, database, fileStorageManager)
            seeder.seedIfNeeded()
        }
    }
}
