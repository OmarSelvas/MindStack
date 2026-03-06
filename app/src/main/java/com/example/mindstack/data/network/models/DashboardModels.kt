package com.example.mindstack.data.network

// Modelos para la vista principal y estadísticas
data class DashboardResponse(
    val todayCheckin: DailyCheckinResponse?,
    val streak: StreakResponse,
    val weekSleepAvgHours: Double,
    val weekBatteryAvg: Double,
    val hasPendingSleepStart: Boolean,
    val pendingCheckinId: Int?
)

data class StreakResponse(
    val currentStreak: Int,
    val lastCheckinDate: String?,
    val isActiveToday: Boolean
)

data class SemaphoreResponse(
    val color: String, // "Verde", "Amarillo", "Rojo"
    val label: String,
    val recommendation: String
)