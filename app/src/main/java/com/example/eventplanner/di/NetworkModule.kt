package com.example.eventplanner.di

import android.content.Context
import com.example.eventplanner.data.remote.ors.ORSApi
import com.example.eventplanner.data.remote.photon.PhotonApi
import com.example.eventplanner.data.remote.places.GooglePlacesDataSource
import com.example.eventplanner.data.repository.EventRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providePlacesDataSource(
        @ApplicationContext context: Context
    ): GooglePlacesDataSource =
        GooglePlacesDataSource(context)

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    @Named("ORS")
    fun provideORSRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Named("Photon")
    fun providePhotonRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://photon.komoot.io/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    @Provides
    @Singleton
    fun provideORSApi(@Named("ORS") retrofit: Retrofit): ORSApi =
        retrofit.create(ORSApi::class.java)

    @Provides
    @Singleton
    fun providePhotonApi(@Named("Photon") retrofit: Retrofit): PhotonApi =
        retrofit.create(PhotonApi::class.java)

    @Provides
    @Singleton
    fun provideRepository(
        orsApi: ORSApi,
        placesDataSource: GooglePlacesDataSource
    ): EventRepository =
        EventRepository(orsApi, placesDataSource)
}
