package com.example.unishare.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.unishare.R
import com.example.unishare.UniShareApplication
import com.example.unishare.ui.components.AcademicBrowsingScreen
import com.example.unishare.ui.dashboard.DashboardScreen
import com.example.unishare.ui.notes.NotesScreen
import com.example.unishare.ui.onboarding.StudentFormScreen
import com.example.unishare.ui.pdfviewer.PdfViewerScreen
import com.example.unishare.ui.profile.ProfileScreen
import com.example.unishare.ui.syllabus.SyllabusScreen
import com.example.unishare.ui.university.UniversitySelectionScreen
import com.example.unishare.viewmodel.AppViewModelFactory
import com.example.unishare.viewmodel.BrowsingViewModel
import com.example.unishare.viewmodel.DashboardViewModel
import com.example.unishare.viewmodel.InitialNavState
import com.example.unishare.viewmodel.MainViewModel
import com.example.unishare.viewmodel.PdfViewerViewModel
import com.example.unishare.viewmodel.ProfileViewModel
import com.example.unishare.viewmodel.StudentFormViewModel
import com.example.unishare.viewmodel.UniversityViewModel

@Composable
fun AppNavHost(
    application: UniShareApplication,
    modifier: Modifier = Modifier
) {
    val mainViewModel: MainViewModel = viewModel(
        factory = AppViewModelFactory(application)
    )
    val navState by mainViewModel.initialNavState.collectAsState()

    when (val state = navState) {
        is InitialNavState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_logo),
                    contentDescription = "UniShare",
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
        is InitialNavState.Ready -> {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = state.startDestination,
                modifier = modifier
            ) {
                composable(Screen.UniversitySelection.route) {
                    val viewModel: UniversityViewModel = viewModel(
                        factory = AppViewModelFactory(application)
                    )
                    UniversitySelectionScreen(
                        viewModel = viewModel,
                        onUniversitySelected = { universityId ->
                            navController.navigate(Screen.StudentForm.createRoute(universityId))
                        }
                    )
                }

                composable(
                    route = Screen.StudentForm.route,
                    arguments = listOf(navArgument("universityId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val universityId = backStackEntry.arguments?.getString("universityId") ?: ""
                    val viewModel: StudentFormViewModel = viewModel(
                        factory = AppViewModelFactory(application, stringArg1 = universityId)
                    )
                    StudentFormScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onSaved = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Dashboard.route) {
                    val viewModel: DashboardViewModel = viewModel(
                        factory = AppViewModelFactory(application)
                    )
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToNotes = { universityId ->
                            navController.navigate(Screen.Notes.createRoute(universityId))
                        },
                        onNavigateToSyllabus = { universityId ->
                            navController.navigate(Screen.Syllabus.createRoute(universityId))
                        },
                        onNavigateToPyq = { universityId ->
                            navController.navigate(Screen.Pyq.createRoute(universityId))
                        },
                        onNavigateToProfile = {
                            navController.navigate(Screen.Profile.route)
                        }
                    )
                }

                composable(
                    route = Screen.Notes.route,
                    arguments = listOf(navArgument("universityId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val universityId = backStackEntry.arguments?.getString("universityId") ?: ""
                    val viewModel: BrowsingViewModel = viewModel(
                        key = "notes_$universityId",
                        factory = AppViewModelFactory(application, stringArg1 = "NOTES", stringArg2 = universityId)
                    )
                    NotesScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onOpenPdf = { documentId ->
                            navController.navigate(Screen.PdfViewer.createRoute(documentId))
                        }
                    )
                }

                composable(
                    route = Screen.Syllabus.route,
                    arguments = listOf(navArgument("universityId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val universityId = backStackEntry.arguments?.getString("universityId") ?: ""
                    val viewModel: BrowsingViewModel = viewModel(
                        key = "syllabus_$universityId",
                        factory = AppViewModelFactory(application, stringArg1 = "SYLLABUS", stringArg2 = universityId)
                    )
                    SyllabusScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onOpenPdf = { documentId ->
                            navController.navigate(Screen.PdfViewer.createRoute(documentId))
                        }
                    )
                }

                composable(
                    route = Screen.Pyq.route,
                    arguments = listOf(navArgument("universityId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val universityId = backStackEntry.arguments?.getString("universityId") ?: ""
                    val viewModel: BrowsingViewModel = viewModel(
                        key = "pyq_$universityId",
                        factory = AppViewModelFactory(application, stringArg1 = "PYQ", stringArg2 = universityId)
                    )
                    AcademicBrowsingScreen(
                        title = "Question Papers (PYQs)",
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onOpenPdf = { documentId ->
                            navController.navigate(Screen.PdfViewer.createRoute(documentId))
                        }
                    )
                }

                composable(Screen.Profile.route) {
                    val viewModel: ProfileViewModel = viewModel(
                        factory = AppViewModelFactory(application)
                    )
                    ProfileScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Screen.PdfViewer.route,
                    arguments = listOf(navArgument("documentId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val documentId = backStackEntry.arguments?.getString("documentId") ?: ""
                    val viewModel: PdfViewerViewModel = viewModel(
                        key = "pdf_$documentId",
                        factory = AppViewModelFactory(application, stringArg1 = documentId)
                    )
                    PdfViewerScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
