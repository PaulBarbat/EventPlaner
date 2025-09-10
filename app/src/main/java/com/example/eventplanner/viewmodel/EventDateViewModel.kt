package com.example.eventplanner.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class EventDateViewModel : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    private val _selectedNumber = MutableStateFlow(1)
    val selectedNumber = _selectedNumber.asStateFlow()

    private val _selectedHours = MutableStateFlow(8)

    val selectedHours = _selectedHours.asStateFlow()

    fun updateDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun updateNumber(number: Int) {
        _selectedNumber.value = number
    }

    fun updateHours(hours: Int){
        _selectedHours.value = hours
    }
}
