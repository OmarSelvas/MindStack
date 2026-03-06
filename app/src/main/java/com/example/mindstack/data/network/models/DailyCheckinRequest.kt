package com.example.mindstack.data.network

import retrofit2.Response
import retrofit2.http.*

interface CheckinApiService {
    @POST("api/v1/checkin")
    suspend fun submitCheckin(
        @Header("Authorization") token: String,
        @Body request: DailyCheckinRequest
    ): Response<CheckinResponse>
}

data class DailyCheckinRequest(
    val sleepStart: String,
    val sleepEnd: String,
    val moodScore: Int
)

data class CheckinResponse(
    val message: String
)