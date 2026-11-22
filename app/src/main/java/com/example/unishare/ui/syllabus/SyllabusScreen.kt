package com.example.unishare.ui.syllabus

import androidx.compose.runtime.Composable
import com.example.unishare.ui.components.AcademicBrowsingScreen
import com.example.unishare.viewmodel.BrowsingViewModel

@Composable
fun SyllabusScreen(
    viewModel: BrowsingViewModel,
    onNavigateBack: () -> Unit,
    onOpenPdf: (String) -> Unit
) {
    AcademicBrowsingScreen(
        title = "Course Syllabus",
        viewModel = viewModel,
        onNavigateBack = onNavigateBack,
        onOpenPdf = onOpenPdf
    )
}
