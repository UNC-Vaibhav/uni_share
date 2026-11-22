package com.example.unishare.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.unishare.data.local.entity.BranchEntity
import com.example.unishare.data.local.entity.DocumentEntity
import com.example.unishare.data.local.entity.SemesterEntity
import com.example.unishare.data.local.entity.SubjectEntity
import com.example.unishare.viewmodel.BrowsingStep
import com.example.unishare.viewmodel.BrowsingUiState
import com.example.unishare.viewmodel.BrowsingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicBrowsingScreen(
    title: String,
    viewModel: BrowsingViewModel,
    onNavigateBack: () -> Unit,
    onOpenPdf: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    BackHandler {
        val handled = viewModel.navigateBack()
        if (!handled) {
            onNavigateBack()
        }
    }

    val breadcrumbItems = mutableListOf<BreadcrumbItem>()
    breadcrumbItems.add(BreadcrumbItem("All Semesters") {
        viewModel.jumpToStep(BrowsingStep.SEMESTER)
    })
    uiState.selectedSemester?.let { sem ->
        breadcrumbItems.add(BreadcrumbItem(sem.displayName) {
            viewModel.jumpToStep(BrowsingStep.BRANCH)
        })
    }
    uiState.selectedBranch?.let { branch ->
        breadcrumbItems.add(BreadcrumbItem(branch.abbreviation) {
            viewModel.jumpToStep(BrowsingStep.SUBJECT)
        })
    }
    uiState.selectedSubject?.let { sub ->
        breadcrumbItems.add(BreadcrumbItem(sub.subjectCode) {})
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when (uiState.step) {
                                BrowsingStep.SEMESTER -> "Step 1: Choose Semester"
                                BrowsingStep.BRANCH -> "Step 2: Choose Branch"
                                BrowsingStep.SUBJECT -> "Step 3: Choose Subject"
                                BrowsingStep.DOCUMENTS -> "Step 4: View Documents"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        val handled = viewModel.navigateBack()
                        if (!handled) {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.step != BrowsingStep.SEMESTER) {
                BreadcrumbBar(items = breadcrumbItems)
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                when (uiState.step) {
                    BrowsingStep.SEMESTER -> {
                        SemesterSelectionView(
                            semesters = uiState.semesters,
                            onSelect = viewModel::selectSemester,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    BrowsingStep.BRANCH -> {
                        BranchSelectionView(
                            branches = uiState.branches,
                            onSelect = viewModel::selectBranch,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    BrowsingStep.SUBJECT -> {
                        SubjectSelectionView(
                            subjects = uiState.subjects,
                            documentCounts = uiState.subjectDocumentCounts,
                            documentType = uiState.documentType,
                            onSelect = viewModel::selectSubject,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    BrowsingStep.DOCUMENTS -> {
                        DocumentListView(
                            subject = uiState.selectedSubject,
                            documents = uiState.documents,
                            documentType = uiState.documentType,
                            onOpenPdf = onOpenPdf,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SemesterSelectionView(
    semesters: List<SemesterEntity>,
    onSelect: (SemesterEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (semesters.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.FolderOff,
            title = "No Semesters Available",
            message = "Semesters have not been configured for this university.",
            modifier = modifier
        )
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(semesters, key = { it.id }) { semester ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelect(semester) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${semester.semesterNumber}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = semester.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BranchSelectionView(
    branches: List<BranchEntity>,
    onSelect: (BranchEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (branches.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.FolderOff,
            title = "No Branches Available",
            message = "Branches have not been configured for this university.",
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier.padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(branches, key = { it.id }) { branch ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelect(branch) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = branch.abbreviation.take(3),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = branch.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Code: ${branch.abbreviation}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Select",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectSelectionView(
    subjects: List<SubjectEntity>,
    documentCounts: Map<String, Int>,
    documentType: String,
    onSelect: (SubjectEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (subjects.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.FolderOff,
            title = "No Subjects Available",
            message = "There are no subjects configured for this branch and semester.",
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier.padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(subjects, key = { it.id }) { subject ->
                val count = documentCounts[subject.id] ?: 0
                val countLabel = if (documentType == "NOTES") "$count notes" else "$count syllabus"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelect(subject) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = subject.subjectCode,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = countLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = subject.subjectName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (subject.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = subject.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentListView(
    subject: SubjectEntity?,
    documents: List<DocumentEntity>,
    documentType: String,
    onOpenPdf: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (documents.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.Description,
            title = if (documentType == "NOTES") "No Notes Available" else "No Syllabus Available",
            message = "Documents for ${subject?.subjectName ?: "this subject"} have not been added yet.",
            modifier = modifier
        )
    } else {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        LazyColumn(
            modifier = modifier.padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(documents, key = { it.id }) { document ->
                val sizeFormatted = formatFileSize(document.fileSize)
                val dateFormatted = dateFormat.format(Date(document.addedAt))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenPdf(document.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = document.documentTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (document.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = document.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = sizeFormatted,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "•",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = dateFormatted,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open PDF",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    return if (mb >= 1.0) {
        String.format(Locale.US, "%.1f MB", mb)
    } else {
        String.format(Locale.US, "%.0f KB", kb)
    }
}
