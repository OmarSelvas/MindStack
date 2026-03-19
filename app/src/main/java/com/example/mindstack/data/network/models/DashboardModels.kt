package com.example.mindstack.data.network

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
