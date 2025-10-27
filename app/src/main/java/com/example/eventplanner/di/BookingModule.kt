package com.example.eventplanner.di

import android.content.Context
import com.example.eventplanner.data.repository.BookingRepository
import com.example.eventplanner.data.repository.LocalBookingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object BookingModule {

    @Provides
    fun provideBookingRepository(
        @ApplicationContext context: Context
    ): BookingRepository = LocalBookingRepository(context)
    //BookingRepository = S3BookingRepository("https://the-tuk-bucket.s3.eu-central-1.amazonaws.com/bookings.json")
}