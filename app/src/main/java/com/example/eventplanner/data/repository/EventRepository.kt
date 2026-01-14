package com.example.eventplanner.data.repository

import com.example.eventplanner.data.remote.ors.ORSApi
import com.example.eventplanner.data.remote.ors.ORSRequest
import com.example.eventplanner.data.remote.photon.PhotonApi
import com.example.eventplanner.data.remote.places.GooglePlacesDataSource

class EventRepository(
    private val orsApi: ORSApi,
    private val placesDataSource: GooglePlacesDataSource
) {
    suspend fun getRoute(body: ORSRequest, apiKey: String) =
        orsApi.getRoute(body, apiKey)

    suspend fun searchPlaces(query: String) =
        placesDataSource.search(query)
}