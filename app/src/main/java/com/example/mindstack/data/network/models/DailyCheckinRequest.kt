package com.example.mindstack.data.network

import retrofit2.Response
import retrofit2.http.*

interface CheckinApiService {
    @POST("api/v1/checkin")
    suspend fun submitCheckin(
        @Header("Authorization") token: String,
        @Body request: DailyCheckinRequest
    ): Response<DailyCheckinResponse>

    @GET("api/v1/checkin/history")
    suspend fun getHistory(
        @Header("Authorization") token: String
    ): Response<List<DailyCheckinResponse>>

    @GET("api/v1/dashboard")
    suspend fun getDashboard(
        @Header("Authorization") token: String
    ): Response<DashboardResponse>

    @GET("api/v1/checkin/battery")
    suspend fun getCombinedBattery(
        @Header("Authorization") token: String
    ): Response<CombinedBatteryResponse>
}

data class DailyCheckinRequest(
    val sleepStart: String,
    val sleepEnd: String,
    val moodScore: Int
)

data class DailyCheckinResponse(
    val checkinId: Int,
    val hoursSleep: Double,
    val sleepDebt: Double,
    val sleepPercent: Double,
    val moodScore: Int,
    val semaphore: SemaphoreResponse,
    val batteryCog: Int,
    val fatiga: Int,
    val message: String,
    val personalizedMessage: PersonalizedMessage? = null
)

data class SemaphoreResponse(
    val color: String,
    val label: String,
    val recommendation: String
)

data class CombinedBatteryResponse(
    val finalBattery: Int,
    val fatiga: Int,
    val semaphoreColor: String,
    val cognitiveSemaphore: String,
    val globalRecommendation: String,
    val personalizedMessage: PersonalizedMessage? = null
)

data class PersonalizedMessage(
    val title: String,
    val body: String
)

data class CheckinResponse(val message: String)
