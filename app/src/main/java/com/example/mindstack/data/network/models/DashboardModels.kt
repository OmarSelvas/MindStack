package com.example.mindstack.data.network

import kotlinx.serialization.Serializable

@Serializable
data class DashboardResponse(
    val todayCheckin: DailyCheckinResponse?,
    val streak: StreakResponse,
    val weekSleepAvgHours: Double,
    val weekBatteryAvg: Double,
    val hasPendingSleepStart: Boolean,
    val pendingCheckinId: Int?
)

@Serializable
data class StreakResponse(
    val currentStreak: Int,
    val lastCheckinDate: String?,
    val isActiveToday: Boolean
)

@Serializable
data class DailyCheckinResponse(
    val checkinId: Int,
    val hoursSleep: Double,
    val sleepDebt: Double,
    val batteryCog: Int,
    val semaphore: SemaphoreResponse,
    val message: String
)

@Serializable
data class SemaphoreResponse(
    val color: String, // "Verde", "Amarillo", "Rojo"
    val label: String,
    val recommendation: String
)