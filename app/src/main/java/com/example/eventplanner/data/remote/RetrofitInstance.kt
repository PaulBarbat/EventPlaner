package com.example.eventplanner.data.remote

import com.example.eventplanner.data.remote.ors.ORSApi
import com.example.eventplanner.data.remote.photon.PhotonApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val orsApi: ORSApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ORSApi::class.java)
    }

    val photonApi: PhotonApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://photon.komoot.io/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PhotonApi::class.java)
    }
}