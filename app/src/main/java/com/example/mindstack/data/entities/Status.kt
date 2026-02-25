package com.example.mindstack.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "status")
data class Status(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val color: String,
    val description: String
)
