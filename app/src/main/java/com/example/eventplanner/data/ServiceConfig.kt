package com.example.eventplanner.data

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ServiceEntry(
    val id: String,
    val displayName: String,
    val imageResourceNames: List<String>,
    val basePrice: Int,
    val pricePerExtraPerson: Double,
    val pricePerPersonExtraHours: Double
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class TuktukEntry(
    val id: String,
    val displayName: String
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ServicesConfig(
    val services: List<ServiceEntry>,
    val allImages: List<TuktukEntry>
)