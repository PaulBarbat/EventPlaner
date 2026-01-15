package com.example.eventplanner.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventplanner.data.ServiceEntry
import com.example.eventplanner.data.ServicesConfig
import com.example.eventplanner.data.TuktukEntry
import com.example.eventplanner.data.models.Booking
import com.example.eventplanner.data.remote.aws.ServicesRemoteLoader
import com.example.eventplanner.data.remote.ors.ORSRequest
import com.example.eventplanner.data.repository.BookingRepository
import com.example.eventplanner.data.repository.EventRepository
import com.example.eventplanner.ui.theme.EventPlannerTheme
import com.example.eventplanner.ui.theme.ThemeManager
import com.example.eventplanner.ui.theme.UITheme
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@SuppressLint("DiscouragedApi")
@HiltViewModel
class EventDateViewModel @Inject constructor(
    private val repository: EventRepository,
    private val bookingRepository: BookingRepository,
    @ApplicationContext private val appContext : Context
) : ViewModel() {

    private val tag = "ViewModel"

    val themeManager : ThemeManager = ThemeManager()

    val theme: StateFlow<EventPlannerTheme> = themeManager.theme

    // --- NEW state for event form ---
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    private val _selectedNumber = MutableStateFlow(0)
    val selectedNumber: StateFlow<Int> = _selectedNumber.asStateFlow()

    private val _selectedHours = MutableStateFlow(0)
    val selectedHours: StateFlow<Int> = _selectedHours.asStateFlow()

    private val _formState = MutableStateFlow(1)
    val formState: StateFlow<Int> = _formState.asStateFlow()

    // --- Existing route state ---
    private val _distance = MutableStateFlow<Double?>(null)
    val distance: StateFlow<Double?> = _distance

    private val _suggestions = MutableStateFlow<List<Pair<String, LatLng>>>(emptyList())
    val suggestions: StateFlow<List<Pair<String, LatLng>>> = _suggestions

    private val _startPoint = MutableStateFlow(LatLng(45.642680, 25.617590))

    val startPoint: StateFlow<LatLng> = _startPoint

    private val _endPoint = MutableStateFlow<LatLng?>(value = null)

    val endPoint: StateFlow<LatLng?> = _endPoint

    private val _config = MutableStateFlow<ServicesConfig?>(null)

    private val _imageRes = MutableStateFlow<Map<String, Int>>(emptyMap())

    private val _selectedServiceId = MutableStateFlow<String?>(null)
    val selectedServiceId : StateFlow<String?> = _selectedServiceId

    private val _selectedImage = MutableStateFlow<String?>(null)
    val selectedImage: StateFlow<String?> = _selectedImage

    private val _configOutdated = MutableStateFlow(false)
    val configOutdated: StateFlow<Boolean> = _configOutdated

    private val _selectedServices = mutableStateListOf<Pair<ServiceEntry, TuktukEntry>>()
    val selectedServices: List<Pair<ServiceEntry, TuktukEntry>> = _selectedServices


    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings

    init {
        themeManager.changeTheme(UITheme.BLUE_DARK)
        loadBookings()
        viewModelScope.launch {
            val (config, isLocal) = ServicesRemoteLoader.loadConfig(appContext)
            _configOutdated.value = isLocal
            _config.value = config
            _imageRes.value = config?.allImages?.associate { tuktuk ->
                tuktuk.id to (appContext.resources.getIdentifier(tuktuk.id, "drawable", appContext.packageName)
                    .takeIf { it !=0 } ?: error("Drawable not found for name : $tuktuk.id"))
            } ?: emptyMap()

            if (isLocal) {
                Log.w("Config", "Using local cached services config, data may be uotdated.")
            }
        }
    }

    fun services(): List<ServiceEntry> = _config.value?.services.orEmpty()
    fun allImages(): List<TuktukEntry> = _config.value?.allImages.orEmpty()
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

    fun saveSelectedService() {
        val serviceID = _selectedServiceId.value
        val imageName = _selectedImage.value
        val service = _config.value?.services?.firstOrNull { it.id == serviceID}
        val tuktuk = _config.value?.allImages?.firstOrNull { it.id == imageName}
        if(service!= null && tuktuk != null) {
            _selectedServices.add(service to tuktuk)
            _selectedServiceId.value=null
            _selectedImage.value=null
        }
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
        Log.d(tag,"update to $state")
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
                Log.d(tag,"Try to search $query")
                _suggestions.value = repository.searchPlaces(query)
                Log.d(tag,"Suggestions is ${_suggestions.value.get(0).first} ${_suggestions.value.size} ")
            } catch (e: Exception) {
                Log.e("Places", "Failed to search places", e)
            }
        }
    }


    fun saveBooking() {
        viewModelScope.launch {
            val booking = Booking(
                id = UUID.randomUUID().toString(),
                date = selectedDate.value?.toString() ?: "Unknown",
                numberOfPeople = selectedNumber.value,
                hours = selectedHours.value,
                routeDistance = distance.value,
                selectedServices = selectedServices.map { it.first.id to it.second.displayName }
            )
            bookingRepository.saveBooking(booking)
            invalidate()
            Log.i(tag, "Booking saved: $booking")
        }
    }

    fun loadBookings() {
        viewModelScope.launch {
            _bookings.value = bookingRepository.getAllBookings()
        }
    }

    fun deleteBooking(id: String) {
        viewModelScope.launch {
            bookingRepository.deleteBooking(id)
            loadBookings()
        }
    }

    fun invalidate() {
        _selectedDate.value = null
        _selectedImage.value = null
        _selectedHours.value = 0
        _selectedNumber.value = 0
        _selectedServices.clear()
        _selectedServiceId.value = null
        _distance.value = null
        _endPoint.value = null
    }

    fun calculateSelectedServicePrice(service: ServiceEntry): Int {
        val extraPersons = if (_selectedNumber.value > 100) {
            service.pricePerExtraPerson * (_selectedNumber.value - 100)
        } else {
            0.0
        }

        val extraHours = if (_selectedHours.value > 4) {
            _selectedNumber.value * service.pricePerPersonExtraHours * (_selectedHours.value - 4) / 2.0
        } else {
            0.0
        }

        return if(service.id == "ciggar"){
            if(_selectedNumber.value>100)
                service.pricePerExtraPerson.toInt()
            else
                service.basePrice
        } else{
            (service.basePrice + extraPersons + extraHours).toInt()
        }
    }

    fun changeTheme(theme:UITheme) {
        themeManager.changeTheme(theme)
    }
}
