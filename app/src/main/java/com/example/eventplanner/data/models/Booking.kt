package com.example.eventplanner.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Booking(
    val id: String,
    val date: String,
    val numberOfPeople: Int,
    val hours: Int,
    val routeDistance: Double?,
    val selectedServices: List<Pair<String, String>>,
    val createdAt: Long = System.currentTimeMillis()
)