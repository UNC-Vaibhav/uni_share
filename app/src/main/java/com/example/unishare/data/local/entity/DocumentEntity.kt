package com.example.unishare.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "documents",
    indices = [
        Index("universityId"),
        Index("branchId"),
        Index("semesterId"),
        Index("subjectId"),
        Index("documentType")
    ]
)
data class DocumentEntity(
    @PrimaryKey val id: String,
    val universityId: String,
    val branchId: String,
    val semesterId: String,
    val subjectId: String,
    val documentTitle: String,
    val description: String,
    val documentType: String,
    val fileName: String,
    val relativeFilePath: String,
    val fileSize: Long,
    val addedAt: Long
)
