package com.example.unishare.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subjects",
    indices = [
        Index("branchId"),
        Index("semesterId")
    ]
)
data class SubjectEntity(
    @PrimaryKey val id: String,
    val branchId: String,
    val semesterId: String,
    val subjectName: String,
    val subjectCode: String,
    val description: String
)
