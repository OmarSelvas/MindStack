package com.example.mindstack.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "juego")
data class Juego(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String
)
