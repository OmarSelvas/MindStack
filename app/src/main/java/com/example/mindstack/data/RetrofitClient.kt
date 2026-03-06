package com.example.mindstack.data

import com.example.mindstack.data.network.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

// --- INTERFACES: Único lugar donde deben existir ---

interface AuthApiService {
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}

interface CheckinApiService {
    @POST("api/v1/checkin")
    suspend fun submitCheckin(
        @Header("Authorization") token: String,
        @Body request: DailyCheckinRequest
    ): Response<CheckinResponse>
}

interface DashboardApiService {
    @GET("api/v1/dashboard")
    suspend fun getDashboard(@Header("Authorization") token: String): Response<DashboardResponse>
}

interface GameApiService {
    @POST("api/v1/games/memory")
    suspend fun submitMemoryGame(
        @Header("Authorization") token: String,
        @Body request: MemoryGameRequest
    ): Response<MemoryGameResponse>

    @POST("api/v1/games/neuro-reflex")
    suspend fun submitNeuroReflex(
        @Header("Authorization") token: String,
        @Body request: NeuroReflexRequest
    ): Response<NeuroReflexResponse>
}

// --- OBJETO CLIENTE ---

object RetrofitClient {
    private const val BASE_URL = "https://mindstack-back.onrender.com/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(90, TimeUnit.SECONDS) // Aumentado a 90 para aguantar el lag de Render
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // GSON para que no se pelee con los modelos
            .build()
    }

    val authService: AuthApiService by lazy { retrofit.create(AuthApiService::class.java) }
    val checkinService: CheckinApiService by lazy { retrofit.create(CheckinApiService::class.java) }
    val dashboardService: DashboardApiService by lazy { retrofit.create(DashboardApiService::class.java) }
    val gameService: GameApiService by lazy { retrofit.create(GameApiService::class.java) }
}