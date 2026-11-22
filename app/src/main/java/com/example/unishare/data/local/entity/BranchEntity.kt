package com.example.unishare.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "branches",
    indices = [Index("universityId")]
)
data class BranchEntity(
    @PrimaryKey val id: String,
    val universityId: String,
    val name: String,
    val abbreviation: String
)
