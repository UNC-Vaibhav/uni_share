package com.example.unishare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unishare.data.local.entity.BranchEntity
import com.example.unishare.data.local.entity.DocumentEntity
import com.example.unishare.data.local.entity.SemesterEntity
import com.example.unishare.data.local.entity.SubjectEntity
import com.example.unishare.data.repository.AcademicRepository
import com.example.unishare.data.repository.DocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class BrowsingStep {
    SEMESTER,
    BRANCH,
    SUBJECT,
    DOCUMENTS
}

data class BrowsingUiState(
    val step: BrowsingStep = BrowsingStep.SEMESTER,
    val documentType: String,
    val universityId: String,
    val semesters: List<SemesterEntity> = emptyList(),
    val branches: List<BranchEntity> = emptyList(),
    val subjects: List<SubjectEntity> = emptyList(),
    val documents: List<DocumentEntity> = emptyList(),
    val selectedSemester: SemesterEntity? = null,
    val selectedBranch: BranchEntity? = null,
    val selectedSubject: SubjectEntity? = null,
    val subjectDocumentCounts: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = false
)

class BrowsingViewModel(
    val documentType: String,
    val universityId: String,
    private val academicRepository: AcademicRepository,
    private val documentRepository: DocumentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        BrowsingUiState(documentType = documentType, universityId = universityId)
    )
    val uiState: StateFlow<BrowsingUiState> = _uiState.asStateFlow()

    init {
        loadSemesters()
    }

    private fun loadSemesters() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            academicRepository.getSemesters(universityId).collect { semesters ->
                _uiState.update {
                    it.copy(
                        semesters = semesters,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectSemester(semester: SemesterEntity) {
        _uiState.update {
            it.copy(
                selectedSemester = semester,
                selectedBranch = null,
                selectedSubject = null,
                step = BrowsingStep.BRANCH,
                isLoading = true
            )
        }
        viewModelScope.launch {
            academicRepository.getBranches(universityId).collect { branches ->
                _uiState.update {
                    it.copy(
                        branches = branches,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectBranch(branch: BranchEntity) {
        val currentSemester = _uiState.value.selectedSemester ?: return
        _uiState.update {
            it.copy(
                selectedBranch = branch,
                selectedSubject = null,
                step = BrowsingStep.SUBJECT,
                isLoading = true
            )
        }
        viewModelScope.launch {
            academicRepository.getSubjects(branch.id, currentSemester.id).collect { subjects ->
                val counts = mutableMapOf<String, Int>()
                for (s in subjects) {
                    val count = documentRepository.getDocumentCountDirect(s.id, documentType)
                    counts[s.id] = count
                }
                _uiState.update {
                    it.copy(
                        subjects = subjects,
                        subjectDocumentCounts = counts,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectSubject(subject: SubjectEntity) {
        _uiState.update {
            it.copy(
                selectedSubject = subject,
                step = BrowsingStep.DOCUMENTS,
                isLoading = true
            )
        }
        viewModelScope.launch {
            documentRepository.getDocuments(subject.id, documentType).collect { docs ->
                _uiState.update {
                    it.copy(
                        documents = docs,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun navigateBack(): Boolean {
        val currentStep = _uiState.value.step
        return when (currentStep) {
            BrowsingStep.DOCUMENTS -> {
                _uiState.update {
                    it.copy(
                        selectedSubject = null,
                        documents = emptyList(),
                        step = BrowsingStep.SUBJECT
                    )
                }
                true
            }
            BrowsingStep.SUBJECT -> {
                _uiState.update {
                    it.copy(
                        selectedBranch = null,
                        subjects = emptyList(),
                        step = BrowsingStep.BRANCH
                    )
                }
                true
            }
            BrowsingStep.BRANCH -> {
                _uiState.update {
                    it.copy(
                        selectedSemester = null,
                        branches = emptyList(),
                        step = BrowsingStep.SEMESTER
                    )
                }
                true
            }
            BrowsingStep.SEMESTER -> {
                false
            }
        }
    }

    fun jumpToStep(targetStep: BrowsingStep) {
        when (targetStep) {
            BrowsingStep.SEMESTER -> {
                _uiState.update {
                    it.copy(
                        selectedSemester = null,
                        selectedBranch = null,
                        selectedSubject = null,
                        branches = emptyList(),
                        subjects = emptyList(),
                        documents = emptyList(),
                        step = BrowsingStep.SEMESTER
                    )
                }
            }
            BrowsingStep.BRANCH -> {
                _uiState.update {
                    it.copy(
                        selectedBranch = null,
                        selectedSubject = null,
                        subjects = emptyList(),
                        documents = emptyList(),
                        step = BrowsingStep.BRANCH
                    )
                }
            }
            BrowsingStep.SUBJECT -> {
                _uiState.update {
                    it.copy(
                        selectedSubject = null,
                        documents = emptyList(),
                        step = BrowsingStep.SUBJECT
                    )
                }
            }
            BrowsingStep.DOCUMENTS -> Unit
        }
    }
}
