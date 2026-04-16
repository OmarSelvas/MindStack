package com.example.mindstack.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Endpoints de Autenticación
interface AuthApiService {
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<PreAuthResponse>

    @POST("api/v1/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<AuthResponse>
}

// Modelos para Login OTP
data class PreAuthResponse(
    val preAuthToken: String,
    val message: String
)

data class VerifyOtpRequest(
    val preAuthToken: String,
    val code: String
)

// Modelos Generales
data class AuthResponse(
    val token: String,
    val userId: Int,
    val name: String,
    val lastName: String?,
    val dateOfBirth: String?,
    val idealSleepHours: Double?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val lastName: String,
    val email: String,
    val password: String,
    val dateOfBirth: String,
    val gender: String,
    val idealSleepHours: Double
)