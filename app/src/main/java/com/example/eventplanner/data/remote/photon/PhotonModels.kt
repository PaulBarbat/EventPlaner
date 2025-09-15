package com.example.eventplanner.data.remote.photon

data class PhotonFeature(
    val geometry: Geometry,
    val properties: Properties
)

data class Geometry(val coordinates: List<Double>)
data class Properties(val name: String? = null, val city: String? = null)
data class PhotonResponse(val features: List<PhotonFeature>)
