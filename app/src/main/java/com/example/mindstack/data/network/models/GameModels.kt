package com.example.mindstack.data.network

import kotlinx.serialization.Serializable

@Serializable
data class MemoryGameRequest(
    val idDailyCheckin: Int,
    val correctHits: Int,
    val totalRequired: Int
)

@Serializable
data class MemoryGameResponse(
    val sessionId: Int,
    val accuracyPercent: Double,
    val battery: Int,
    val label: String,
    val recommendation: String
)

@Serializable
data class NeuroReflexRequest(
    val idDailyCheckin: Int,
    val reactionTime1Ms: Double,
    val reactionTime2Ms: Double,
    val reactionTime3Ms: Double
)

@Serializable
data class NeuroReflexResponse(
    val sessionId: Int,
    val averageMs: Double,
    val battery: Int,
    val label: String,
    val recommendation: String
)