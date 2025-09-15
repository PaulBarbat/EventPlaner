package com.example.eventplanner.data.remote.ors
import retrofit2.http.*
interface ORSApi {
    @Headers("Content-Type: application/json")
    @POST("v2/directions/driving-car")
    suspend fun getRoute(
        @Body body: ORSRequest,
        @Header("Authorization") apiKey: String
    ): ORSResponse
}