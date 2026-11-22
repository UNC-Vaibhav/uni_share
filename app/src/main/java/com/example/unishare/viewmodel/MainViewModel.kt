package com.example.unishare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unishare.data.repository.StudentProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class InitialNavState {
    object Loading : InitialNavState()
    data class Ready(val startDestination: String) : InitialNavState()
}

class MainViewModel(
    private val studentProfileRepository: StudentProfileRepository
) : ViewModel() {

    private val _initialNavState = MutableStateFlow<InitialNavState>(InitialNavState.Loading)
    val initialNavState: StateFlow<InitialNavState> = _initialNavState.asStateFlow()

    init {
        determineStartDestination()
    }

    private fun determineStartDestination() {
        viewModelScope.launch {
            val profile = studentProfileRepository.getProfileDirect()
            if (profile != null) {
                _initialNavState.value = InitialNavState.Ready("dashboard")
            } else {
                _initialNavState.value = InitialNavState.Ready("university_selection")
            }
        }
    }
}
