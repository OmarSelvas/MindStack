package com.example.mindstack.data.network

import com.google.gson.annotations.SerializedName
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
    @SerializedName("sleep_start") val sleepStart: String,
    @SerializedName("sleep_end") val sleepEnd: String,
    @SerializedName("mood_score") val moodScore: Int
)

data class DailyCheckinResponse(
    @SerializedName("checkin_id", alternate = ["id"]) val checkinId: Int,
    @SerializedName("hours_sleep") val hoursSleep: Double,
    @SerializedName("sleep_debt") val sleepDebt: Double,
    @SerializedName("sleep_percent") val sleepPercent: Double,
    @SerializedName("mood_score") val moodScore: Int,
    val semaphore: SemaphoreResponse,
    @SerializedName("battery_cog", alternate = ["battery", "batteryCog"]) val batteryCog: Int,
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
    @SerializedName("final_battery") val finalBattery: Int,
    val fatiga: Int,
    @SerializedName("semaphore_color") val semaphoreColor: String,
    @SerializedName("cognitive_semaphore") val cognitiveSemaphore: String,
    @SerializedName("global_recommendation") val globalRecommendation: String,
    val personalizedMessage: PersonalizedMessage
)

data class PersonalizedMessage(
    val title: String,
    val content: String
)

data class CheckinResponse(val message: String)
