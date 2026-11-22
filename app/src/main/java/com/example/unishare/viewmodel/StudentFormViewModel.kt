package com.example.unishare.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unishare.data.local.entity.StudentProfileEntity
import com.example.unishare.data.local.entity.UniversityEntity
import com.example.unishare.data.repository.StudentProfileRepository
import com.example.unishare.data.repository.UniversityRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentFormUiState(
    val fullName: String = "",
    val usnOrCollegeId: String = "",
    val email: String = "",
    val mobileNumber: String = "",
    val academicYear: String = "",
    val fullNameError: String? = null,
    val usnError: String? = null,
    val emailError: String? = null,
    val mobileError: String? = null,
    val academicYearError: String? = null,
    val university: UniversityEntity? = null,
    val isSaving: Boolean = false
)

class StudentFormViewModel(
    private val universityId: String,
    private val studentProfileRepository: StudentProfileRepository,
    private val universityRepository: UniversityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentFormUiState())
    val uiState: StateFlow<StudentFormUiState> = _uiState.asStateFlow()

    private val _saveSuccessEvent = MutableSharedFlow<Unit>()
    val saveSuccessEvent: SharedFlow<Unit> = _saveSuccessEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            universityRepository.getUniversity(universityId).collect { uni ->
                _uiState.update { it.copy(university = uni) }
            }
        }
    }

    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, fullNameError = null) }
    }

    fun onUsnChange(value: String) {
        _uiState.update { it.copy(usnOrCollegeId = value, usnError = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onMobileChange(value: String) {
        _uiState.update { it.copy(mobileNumber = value, mobileError = null) }
    }

    fun onAcademicYearChange(value: String) {
        _uiState.update { it.copy(academicYear = value, academicYearError = null) }
    }

    fun save() {
        val state = _uiState.value
        val nameValid = state.fullName.isNotBlank()
        val usnValid = state.usnOrCollegeId.isNotBlank()
        val emailValid = state.email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches()
        val mobileValid = state.mobileNumber.isBlank() || state.mobileNumber.trim().matches(Regex("^[0-9]{10}$"))
        val yearValid = state.academicYear.isNotBlank()

        if (!nameValid || !usnValid || !emailValid || !mobileValid || !yearValid) {
            _uiState.update {
                it.copy(
                    fullNameError = if (!nameValid) "Full name is required" else null,
                    usnError = if (!usnValid) "USN or College ID is required" else null,
                    emailError = if (state.email.isBlank()) "Email address is required" else if (!emailValid) "Please enter a valid email address" else null,
                    mobileError = if (!mobileValid) "Enter a valid 10-digit mobile number" else null,
                    academicYearError = if (!yearValid) "Please select your academic year" else null
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val profile = StudentProfileEntity(
                id = 1L,
                fullName = state.fullName.trim(),
                usnOrCollegeId = state.usnOrCollegeId.trim().uppercase(),
                email = state.email.trim(),
                mobileNumber = state.mobileNumber.trim().ifBlank { null },
                academicYear = state.academicYear,
                universityId = universityId
            )
            studentProfileRepository.saveProfile(profile)
            _uiState.update { it.copy(isSaving = false) }
            _saveSuccessEvent.emit(Unit)
        }
    }
}
