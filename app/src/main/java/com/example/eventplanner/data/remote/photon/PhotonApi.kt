package com.example.eventplanner.data.remote.photon

import retrofit2.http.GET
import retrofit2.http.Query

interface PhotonApi {
    @GET("api/")
    suspend fun search(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("lang") lang: String = "en",
    ): PhotonResponse
}