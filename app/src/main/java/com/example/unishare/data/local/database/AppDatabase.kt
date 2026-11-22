package com.example.unishare.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.unishare.data.local.dao.AcademicDao
import com.example.unishare.data.local.dao.DocumentDao
import com.example.unishare.data.local.dao.StudentProfileDao
import com.example.unishare.data.local.dao.UniversityDao
import com.example.unishare.data.local.entity.BranchEntity
import com.example.unishare.data.local.entity.DocumentEntity
import com.example.unishare.data.local.entity.SemesterEntity
import com.example.unishare.data.local.entity.StudentProfileEntity
import com.example.unishare.data.local.entity.SubjectEntity
import com.example.unishare.data.local.entity.UniversityEntity

@Database(
    entities = [
        UniversityEntity::class,
        StudentProfileEntity::class,
        BranchEntity::class,
        SemesterEntity::class,
        SubjectEntity::class,
        DocumentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun universityDao(): UniversityDao
    abstract fun studentProfileDao(): StudentProfileDao
    abstract fun academicDao(): AcademicDao
    abstract fun documentDao(): DocumentDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "unishare_database.db"
                ).build().also { instance = it }
            }
        }
    }
}
