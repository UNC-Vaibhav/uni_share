package com.example.unishare.viewmodel

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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val profile: StudentProfileEntity? = null,
    val university: UniversityEntity? = null,
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    studentProfileRepository: StudentProfileRepository,
    private val universityRepository: UniversityRepository
) : ViewModel() {

    private val _profileFlow = studentProfileRepository.profile

    val uiState: StateFlow<DashboardUiState> = _profileFlow.flatMapLatest { profile ->
        if (profile != null) {
            universityRepository.getUniversity(profile.universityId).flatMapLatest { uni ->
                flowOf(DashboardUiState(profile = profile, university = uni, isLoading = false))
            }
        } else {
            flowOf(DashboardUiState(isLoading = false))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )
}
