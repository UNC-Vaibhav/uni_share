package com.example.unishare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profile")
data class StudentProfileEntity(
    @PrimaryKey val id: Long,
    val fullName: String,
    val usnOrCollegeId: String,
    val email: String,
    val mobileNumber: String?,
    val academicYear: String,
    val universityId: String
)
