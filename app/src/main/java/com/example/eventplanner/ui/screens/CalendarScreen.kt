package com.example.eventplanner.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.eventplanner.ui.elements.BookingCalendarElement
import com.example.eventplanner.viewmodel.EventDateViewModel

@Composable
fun CalendarScreen(viewModel: EventDateViewModel){
    val selectedDate by viewModel.selectedDate.collectAsState()
    val bookings by viewModel.bookings.collectAsState()

    BookingCalendarElement(
        bookings = bookings,
        selectedDate = selectedDate,
        onDateSelected = { date ->
            viewModel.updateDate(date)
            viewModel.updateFormState(2)
        },
        onSkip = { viewModel.updateFormState(2)}
    )
}