package com.example.unishare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.unishare.UniShareApplication

class AppViewModelFactory(
    private val application: UniShareApplication,
    private val stringArg1: String? = null,
    private val stringArg2: String? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(application.studentProfileRepository) as T
            }
            modelClass.isAssignableFrom(UniversityViewModel::class.java) -> {
                UniversityViewModel(application.universityRepository) as T
            }
            modelClass.isAssignableFrom(StudentFormViewModel::class.java) -> {
                StudentFormViewModel(
                    universityId = stringArg1 ?: "",
                    studentProfileRepository = application.studentProfileRepository,
                    universityRepository = application.universityRepository
                ) as T
            }
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(
                    studentProfileRepository = application.studentProfileRepository,
                    universityRepository = application.universityRepository
                ) as T
            }
            modelClass.isAssignableFrom(BrowsingViewModel::class.java) -> {
                BrowsingViewModel(
                    documentType = stringArg1 ?: "NOTES",
                    universityId = stringArg2 ?: "",
                    academicRepository = application.academicRepository,
                    documentRepository = application.documentRepository
                ) as T
            }
            modelClass.isAssignableFrom(PdfViewerViewModel::class.java) -> {
                PdfViewerViewModel(
                    documentId = stringArg1 ?: "",
                    documentRepository = application.documentRepository
                ) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(
                    studentProfileRepository = application.studentProfileRepository,
                    universityRepository = application.universityRepository
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
