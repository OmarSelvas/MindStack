package com.example.mindstack.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["id_user"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "id_user")
    val idUser: Int,
    val content: String,
    @ColumnInfo(name = "date_time")
    val dateTime: Date,
    @ColumnInfo(name = "is_from_user")
    val isFromUser: Boolean
)
