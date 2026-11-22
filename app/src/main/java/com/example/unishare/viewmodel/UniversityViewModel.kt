package com.example.unishare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unishare.data.local.entity.UniversityEntity
import com.example.unishare.data.repository.UniversityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class UniversityUiState(
    val universities: List<UniversityEntity> = emptyList(),
    val filteredUniversities: List<UniversityEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedUniversity: UniversityEntity? = null,
    val isLoading: Boolean = true
)

class UniversityViewModel(
    private val universityRepository: UniversityRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedUniversity = MutableStateFlow<UniversityEntity?>(null)
    val selectedUniversity: StateFlow<UniversityEntity?> = _selectedUniversity

    val uiState: StateFlow<UniversityUiState> = combine(
        universityRepository.universities,
        _searchQuery,
        _selectedUniversity
    ) { universities, query, selected ->
        val filtered = if (query.isBlank()) {
            universities
        } else {
            val trimmed = query.trim().lowercase()
            universities.filter {
                it.name.lowercase().contains(trimmed) ||
                it.abbreviation.lowercase().contains(trimmed)
            }
        }
        UniversityUiState(
            universities = universities,
            filteredUniversities = filtered,
            searchQuery = query,
            selectedUniversity = selected,
            isLoading = universities.isEmpty()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UniversityUiState()
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onClearSearch() {
        _searchQuery.value = ""
    }

    fun onSelectUniversity(university: UniversityEntity) {
        _selectedUniversity.value = university
    }
}
