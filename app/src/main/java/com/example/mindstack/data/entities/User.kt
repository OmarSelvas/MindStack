package com.example.mindstack.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "users",
    foreignKeys = [
        ForeignKey(
            entity = Rol::class,
            parentColumns = ["id"],
            childColumns = ["id_rol"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    @ColumnInfo(name = "last_name")
    val lastName: String,
    val email: String,
    val password: String,
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: String,
    val gender: String,
    @ColumnInfo(name = "id_rol")
    val idRol: Int,
    @ColumnInfo(name = "ideal_sleep_hours")
    val idealSleepHours: Float
)
