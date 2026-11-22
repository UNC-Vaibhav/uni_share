package com.example.unishare.data.model

enum class AcademicYear(val displayName: String) {
    FIRST_YEAR("First Year"),
    SECOND_YEAR("Second Year"),
    THIRD_YEAR("Third Year"),
    FOURTH_YEAR("Fourth Year");

    companion object {
        fun fromDisplayName(name: String): AcademicYear? {
            return entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) }
        }
    }
}
