package com.example.eventplanner.data.repository

import com.example.eventplanner.data.remote.ors.ORSApi
import com.example.eventplanner.data.remote.ors.ORSRequest
import com.example.eventplanner.data.remote.photon.PhotonApi

class EventRepository(
    private val orsApi: ORSApi,
    private val photonApi: PhotonApi
) {
    suspend fun getRoute(body: ORSRequest, apiKey: String) =
        orsApi.getRoute(body, apiKey)

    suspend fun searchPlaces(query: String) =
        photonApi.search(query)
}