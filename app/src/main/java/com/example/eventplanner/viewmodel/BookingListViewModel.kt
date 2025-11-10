//package com.example.eventplanner.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.eventplanner.data.models.Booking
//import com.example.eventplanner.data.repository.BookingRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//@HiltViewModel
//class BookingListViewModel @Inject constructor(
//    private val bookingRepository: BookingRepository
//) : ViewModel() {
//
//    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
//    val bookings: StateFlow<List<Booking>> = _bookings
//
//    init {
//        loadBookings()
//    }
//
//    fun loadBookings() {
//        viewModelScope.launch {
//            _bookings.value = bookingRepository.getAllBookings()
//        }
//    }
//
//    fun deleteBooking(id: String) {
//        viewModelScope.launch {
//            bookingRepository.deleteBooking(id)
//            loadBookings()
//        }
//    }
//}