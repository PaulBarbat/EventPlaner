package com.example.eventplanner.data.remote.places

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.tasks.await

class GooglePlacesDataSource(
    context: Context
) {
    private val tag = "PlacesDataSource"
    private val client = Places.createClient(context)
    private val sessionToken = AutocompleteSessionToken.newInstance()

    suspend fun search(query: String): List<Pair<String, LatLng>> {
        Log.d(tag,"Try to search $query")
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(sessionToken)
            .setQuery(query)
            .build()

        Log.d(tag,"Before response")
        val response = client.findAutocompletePredictions(request).await()
        Log.d(tag,"waiting for response")
        return response.autocompletePredictions.mapNotNull {
            fetchLatLng(it)
        }
    }

    private suspend fun fetchLatLng(
        prediction: AutocompletePrediction
    ): Pair<String, LatLng>? {
        val placeRequest = FetchPlaceRequest.builder(
            prediction.placeId,
            listOf(Place.Field.LAT_LNG, Place.Field.NAME)
        ).build()

        val place = client.fetchPlace(placeRequest).await().place
        val latLng = place.latLng ?: return null

        return place.name.orEmpty() to latLng
    }
}
