package com.example.eventplanner.data.remote.ors

data class ORSResponse(
    val routes: List<Route>
)

data class Route(
    val summary: Summary,
    val geometry: String
)

data class Summary(
    val distance: Double, // meters
    val duration: Double  // seconds
)

data class ORSRequest(
    val coordinates: List<List<Double>>
)