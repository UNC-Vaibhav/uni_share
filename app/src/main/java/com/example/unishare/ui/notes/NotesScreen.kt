package com.example.unishare.ui.notes

import androidx.compose.runtime.Composable
import com.example.unishare.ui.components.AcademicBrowsingScreen
import com.example.unishare.viewmodel.BrowsingViewModel

@Composable
fun NotesScreen(
    viewModel: BrowsingViewModel,
    onNavigateBack: () -> Unit,
    onOpenPdf: (String) -> Unit
) {
    AcademicBrowsingScreen(
        title = "Academic Notes",
        viewModel = viewModel,
        onNavigateBack = onNavigateBack,
        onOpenPdf = onOpenPdf
    )
}
