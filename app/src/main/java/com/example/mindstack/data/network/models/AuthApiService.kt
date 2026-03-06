package com.example.mindstack.data.network

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}

@Serializable
data class AuthResponse(
    val token: String,
    val userId: Int,
    val name: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val name: String,
    val lastName: String,
    val email: String,
    val password: String,
    val dateOfBirth: String,
    val gender: String,
    val idealSleepHours: Double
)