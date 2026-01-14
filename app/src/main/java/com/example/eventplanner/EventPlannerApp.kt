package com.example.eventplanner

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EventPlannerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (!com.google.android.libraries.places.api.Places.isInitialized()) {
            com.google.android.libraries.places.api.Places.initialize(
                applicationContext,
                BuildConfig.PLACES_KEY
            )
        }
    }
}
