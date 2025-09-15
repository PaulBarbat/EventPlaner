package com.example.eventplanner.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventplanner.data.remote.ors.ORSRequest
import com.example.eventplanner.data.repository.EventRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EventDateViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {

    // --- NEW state for event form ---
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    private val _selectedNumber = MutableStateFlow(0)
    val selectedNumber: StateFlow<Int> = _selectedNumber.asStateFlow()

    private val _selectedHours = MutableStateFlow(0)
    val selectedHours: StateFlow<Int> = _selectedHours.asStateFlow()

    // --- Existing route state ---
    private val _distance = MutableStateFlow<Double?>(null)
    val distance: StateFlow<Double?> = _distance

    private val _suggestions = MutableStateFlow<List<Pair<String, LatLng>>>(emptyList())
    val suggestions: StateFlow<List<Pair<String, LatLng>>> = _suggestions

    // --- Update functions for the screen ---
    fun updateDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun updateNumber(number: Int) {
        _selectedNumber.value = number
    }

    fun updateHours(hours: Int) {
        _selectedHours.value = hours
    }

    // --- Networking functions ---
    fun fetchRoute(start: LatLng, end: LatLng, apiKey: String) {
        viewModelScope.launch {
            try {
                val body = ORSRequest(
                    coordinates = listOf(
                        listOf(start.longitude, start.latitude),
                        listOf(end.longitude, end.latitude)
                    )
                )
                val response = repository.getRoute(body, apiKey)
                _distance.value = response.routes.first().summary.distance
            } catch (e: Exception) {
                Log.e("ORS", "Failed to fetch route", e)
            }
        }
    }

    fun searchPlaces(query: String) {
        viewModelScope.launch {
            try {
                val response = repository.searchPlaces(query)
                _suggestions.value = response.features.map { f ->
                    val coords = f.geometry.coordinates
                    val name = f.properties.name ?: "Unknown"
                    Pair(name, LatLng(coords[1], coords[0]))
                }
            } catch (e: Exception) {
                Log.e("Photon", "Failed to search places", e)
            }
        }
    }
}
