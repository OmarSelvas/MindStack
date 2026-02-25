package com.example.mindstack.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rol")
data class Rol(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val rol: String
)
