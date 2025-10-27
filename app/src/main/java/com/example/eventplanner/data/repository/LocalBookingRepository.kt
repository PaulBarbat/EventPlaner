package com.example.eventplanner.data.repository

import android.content.Context
import com.example.eventplanner.data.models.Booking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class LocalBookingRepository(private val context: Context) : BookingRepository {

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true}
    private val fileName = "bookings.json"

    override suspend fun saveBooking(booking: Booking) {
        val bookings = readBookings()
        bookings.add(booking)
        writeBookings(bookings)
    }

    override suspend fun getAllBookings(): List<Booking> = readBookings()

    override suspend fun deleteBooking(id: String) {
        val updated = readBookings().filterNot { it.id == id}
        writeBookings(updated)
    }

    private suspend fun readBookings(): MutableList<Booking> = withContext(Dispatchers.IO) {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return@withContext mutableListOf()
        val text = file.readText()
        if (text.isBlank()) mutableListOf() else json.decodeFromString(text)
    }

    private suspend fun writeBookings(bookings: List<Booking>) = withContext(Dispatchers.IO) {
        val file = File(context.filesDir, fileName)
        file.writeText(json.encodeToString(bookings))
    }
}