package com.example.unishare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "universities")
data class UniversityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val abbreviation: String,
    val logoResourceName: String
)
