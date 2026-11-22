package com.example.unishare.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object UniversitySelection : Screen("university_selection")
    object StudentForm : Screen("student_form/{universityId}") {
        fun createRoute(universityId: String): String = "student_form/$universityId"
    }
    object Dashboard : Screen("dashboard")
    object Notes : Screen("notes/{universityId}") {
        fun createRoute(universityId: String): String = "notes/$universityId"
    }
    object Syllabus : Screen("syllabus/{universityId}") {
        fun createRoute(universityId: String): String = "syllabus/$universityId"
    }
    object Pyq : Screen("pyq/{universityId}") {
        fun createRoute(universityId: String): String = "pyq/$universityId"
    }
    object Profile : Screen("profile")
    object PdfViewer : Screen("pdf_viewer/{documentId}") {
        fun createRoute(documentId: String): String = "pdf_viewer/$documentId"
    }
}
