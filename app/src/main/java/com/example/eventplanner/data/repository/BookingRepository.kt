package com.example.eventplanner.data.repository

import com.example.eventplanner.data.models.Booking

interface BookingRepository {
    suspend fun saveBooking(booking: Booking)
    suspend fun getAllBookings(): List<Booking>
    suspend fun deleteBooking(id: String)
}