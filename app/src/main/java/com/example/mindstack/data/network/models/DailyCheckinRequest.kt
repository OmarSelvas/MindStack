package com.example.mindstack.data.network

import retrofit2.Response
import retrofit2.http.*

interface CheckinApiService {
    @POST("api/v1/checkin")
    suspend fun submitCheckin(
        @Header("Authorization") token: String,
        @Body request: DailyCheckinRequest
    ): Response<CheckinResponse>

    @GET("api/v1/checkin/history")
    suspend fun getHistory(
        @Header("Authorization") token: String
    ): Response<List<DailyCheckinResponse>>

    @GET("api/v1/dashboard")
    suspend fun getDashboard(
        @Header("Authorization") token: String
    ): Response<DashboardResponse>
}

// Modelos para el POST
data class DailyCheckinRequest(
    val sleepStart: String,
    val sleepEnd: String,
    val moodScore: Int
)

// Respuesta estándar del Check-in
data class DailyCheckinResponse(
    val checkinId: Int,
    val hoursSleep: Double,
    val sleepDebt: Double,
    val sleepPercent: Double? = 0.0,
    val batteryCog: Int,
    val moodScore: Int = 3,
    val semaphore: SemaphoreResponse,
    val message: String
)

data class CheckinResponse(val message: String)