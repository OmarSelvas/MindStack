package com.example.mindstack.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood")
data class MoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val mood: String
)
