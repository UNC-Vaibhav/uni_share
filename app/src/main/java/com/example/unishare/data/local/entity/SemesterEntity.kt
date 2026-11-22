package com.example.unishare.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "semesters",
    indices = [Index("universityId")]
)
data class SemesterEntity(
    @PrimaryKey val id: String,
    val universityId: String,
    val semesterNumber: Int,
    val displayName: String
)
