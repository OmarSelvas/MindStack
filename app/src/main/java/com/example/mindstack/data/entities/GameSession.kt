package com.example.mindstack.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "game_session",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["id_user"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Juego::class, parentColumns = ["id"], childColumns = ["id_juego"], onDelete = ForeignKey.CASCADE)
    ]
)
data class GameSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "id_user")
    val idUser: Int,
    @ColumnInfo(name = "id_juego")
    val idJuego: Int,
    val score: Int,
    @ColumnInfo(name = "date_time")
    val dateTime: String
)
