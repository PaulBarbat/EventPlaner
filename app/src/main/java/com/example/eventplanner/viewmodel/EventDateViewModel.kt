package com.example.eventplanner.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventplanner.data.ServiceEntry
import com.example.eventplanner.data.ServicesConfig
import com.example.eventplanner.data.ServicesConfigLoader
import com.example.eventplanner.data.remote.ors.ORSRequest
import com.example.eventplanner.data.repository.EventRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EventDateViewModel @Inject constructor(
    private val repository: EventRepository,
    @ApplicationContext private val appContext : Context
) : ViewModel() {

    // --- NEW state for event form ---
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    private val _selectedNumber = MutableStateFlow(0)
    val selectedNumber: StateFlow<Int> = _selectedNumber.asStateFlow()

    private val _selectedHours = MutableStateFlow(0)
    val selectedHours: StateFlow<Int> = _selectedHours.asStateFlow()

    private val _formState = MutableStateFlow(0)
    val formState: StateFlow<Int> = _formState.asStateFlow()

    // --- Existing route state ---
    private val _distance = MutableStateFlow<Double?>(null)
    val distance: StateFlow<Double?> = _distance

    private val _suggestions = MutableStateFlow<List<Pair<String, LatLng>>>(emptyList())
    val suggestions: StateFlow<List<Pair<String, LatLng>>> = _suggestions

    private val _startPoint = MutableStateFlow<LatLng>(LatLng(45.642680, 25.617590))

    val startPoint: StateFlow<LatLng> = _startPoint

    private val _endPoint = MutableStateFlow<LatLng?>(value = null)

    val endPoint: StateFlow<LatLng?> = _endPoint

    private val _config = MutableStateFlow<ServicesConfig?>(null)
    val config: StateFlow<ServicesConfig?> = _config

    private val _imageRes = MutableStateFlow<Map<String, Int>>(emptyMap())
    val imageRes: StateFlow<Map<String, Int>> = _imageRes

    private val _selectedServiceId = MutableStateFlow<String?>(null)
    val selectedServiceId : StateFlow<String?> = _selectedServiceId

    private val _selectedImage = MutableStateFlow<String?>(null)
    val selectedImage: StateFlow<String?> = _selectedImage

    init {
        viewModelScope.launch {
            val (config, resMap) = ServicesConfigLoader.load(context = appContext)
            _config.value = config
            _imageRes.value = resMap
        }
    }

    fun services(): List<ServiceEntry> = _config.value?.services.orEmpty()
    fun allImages(): List<String> = _config.value?.allImages.orEmpty()
    fun resIdOf(name: String): Int = _imageRes.value[name] ?: 0

    fun allowedImageNamesFor(serviceId: String): List<String> =
        _config.value?.services?.firstOrNull { it.id == serviceId }?.imageResourceNames.orEmpty()

    fun selectService(id: String) {
        if (_selectedServiceId.value != id) {
            _selectedServiceId.value = id
            val allowed = allowedImageNamesFor(id).toSet()
            if (_selectedImage.value !in allowed) _selectedImage.value = null
        }
    }

    fun toggleImage(name: String) {
        val svc = _selectedServiceId.value ?: return
        if (name !in allowedImageNamesFor(svc)) return
        _selectedImage.value = if (_selectedImage.value == name) null else name
    }
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

    fun updateFormState(state: Int) {
        _formState.value = state
    }

    fun updateEndPoint(endPoint : LatLng) {
        _endPoint.value = endPoint
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
