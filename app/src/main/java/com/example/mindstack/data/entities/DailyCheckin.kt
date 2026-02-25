package com.example.mindstack.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_checkin",
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["id_user"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = MoodEntity::class, parentColumns = ["id"], childColumns = ["id_mood"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Status::class, parentColumns = ["id"], childColumns = ["id_status"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DailyCheckin(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "id_user")
    val idUser: Int,
    @ColumnInfo(name = "sleep_start")
    val sleepStart: String?,
    @ColumnInfo(name = "sleep_end")
    val sleepEnd: String?,
    @ColumnInfo(name = "hours_sleep")
    val hoursSleep: Float,
    @ColumnInfo(name = "id_mood")
    val idMood: Int,
    @ColumnInfo(name = "id_status")
    val idStatus: Int,
    @ColumnInfo(name = "date_time")
    val dateTime: String,
    @ColumnInfo(name = "sleep_debt")
    val sleepDebt: Float,
    val battery: Int
)
