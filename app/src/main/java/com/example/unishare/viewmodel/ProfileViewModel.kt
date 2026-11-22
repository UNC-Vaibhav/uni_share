package com.example.unishare.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unishare.data.local.entity.StudentProfileEntity
import com.example.unishare.data.local.entity.UniversityEntity
import com.example.unishare.data.repository.StudentProfileRepository
import com.example.unishare.data.repository.UniversityRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profile: StudentProfileEntity? = null,
    val university: UniversityEntity? = null,
    val isEditing: Boolean = false,
    val editFullName: String = "",
    val editUsn: String = "",
    val editEmail: String = "",
    val editMobile: String = "",
    val editAcademicYear: String = "",
    val fullNameError: String? = null,
    val usnError: String? = null,
    val emailError: String? = null,
    val mobileError: String? = null,
    val academicYearError: String? = null,
    val isSaving: Boolean = false,
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    private val studentProfileRepository: StudentProfileRepository,
    private val universityRepository: UniversityRepository
) : ViewModel() {

    private val _editState = MutableStateFlow(ProfileUiState())
    val editState: StateFlow<ProfileUiState> = _editState.asStateFlow()

    val profileAndUniState: StateFlow<Pair<StudentProfileEntity?, UniversityEntity?>> =
        studentProfileRepository.profile.flatMapLatest { prof ->
            if (prof != null) {
                universityRepository.getUniversity(prof.universityId).flatMapLatest { uni ->
                    flowOf(Pair(prof, uni))
                }
            } else {
                flowOf(Pair(null, null))
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Pair(null, null)
        )

    fun startEditing(currentProfile: StudentProfileEntity) {
        _editState.update {
            it.copy(
                isEditing = true,
                editFullName = currentProfile.fullName,
                editUsn = currentProfile.usnOrCollegeId,
                editEmail = currentProfile.email,
                editMobile = currentProfile.mobileNumber ?: "",
                editAcademicYear = currentProfile.academicYear,
                fullNameError = null,
                usnError = null,
                emailError = null,
                mobileError = null,
                academicYearError = null
            )
        }
    }

    fun cancelEditing() {
        _editState.update { it.copy(isEditing = false) }
    }

    fun onFullNameChange(value: String) {
        _editState.update { it.copy(editFullName = value, fullNameError = null) }
    }

    fun onUsnChange(value: String) {
        _editState.update { it.copy(editUsn = value, usnError = null) }
    }

    fun onEmailChange(value: String) {
        _editState.update { it.copy(editEmail = value, emailError = null) }
    }

    fun onMobileChange(value: String) {
        _editState.update { it.copy(editMobile = value, mobileError = null) }
    }

    fun onAcademicYearChange(value: String) {
        _editState.update { it.copy(editAcademicYear = value, academicYearError = null) }
    }

    fun saveProfile(universityId: String) {
        val state = _editState.value
        val nameValid = state.editFullName.isNotBlank()
        val usnValid = state.editUsn.isNotBlank()
        val emailValid = state.editEmail.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(state.editEmail.trim()).matches()
        val mobileValid = state.editMobile.isBlank() || state.editMobile.trim().matches(Regex("^[0-9]{10}$"))
        val yearValid = state.editAcademicYear.isNotBlank()

        if (!nameValid || !usnValid || !emailValid || !mobileValid || !yearValid) {
            _editState.update {
                it.copy(
                    fullNameError = if (!nameValid) "Full name is required" else null,
                    usnError = if (!usnValid) "USN or College ID is required" else null,
                    emailError = if (state.editEmail.isBlank()) "Email address is required" else if (!emailValid) "Please enter a valid email address" else null,
                    mobileError = if (!mobileValid) "Enter a valid 10-digit mobile number" else null,
                    academicYearError = if (!yearValid) "Please select your academic year" else null
                )
            }
            return
        }

        viewModelScope.launch {
            _editState.update { it.copy(isSaving = true) }
            val updated = StudentProfileEntity(
                id = 1L,
                fullName = state.editFullName.trim(),
                usnOrCollegeId = state.editUsn.trim().uppercase(),
                email = state.editEmail.trim(),
                mobileNumber = state.editMobile.trim().ifBlank { null },
                academicYear = state.editAcademicYear,
                universityId = universityId
            )
            studentProfileRepository.saveProfile(updated)
            _editState.update { it.copy(isSaving = false, isEditing = false) }
        }
    }
}
