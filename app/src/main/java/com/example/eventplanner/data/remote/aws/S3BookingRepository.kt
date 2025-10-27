package com.example.eventplanner.data.remote.aws

import android.util.Log
import com.example.eventplanner.data.models.Booking
import com.example.eventplanner.data.repository.BookingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.net.HttpURLConnection
import java.net.URL

class S3BookingRepository(
    private val s3Url: String
) : BookingRepository {

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true}
    private val TAG = "S3BookingRepository"

    override suspend fun saveBooking(booking: Booking) {
        val all = getAllBookings() + booking
        uploadToS3(json.encodeToString(all))
        Log.i(TAG, "saveBooking")
    }

    override suspend fun getAllBookings(): List<Booking> = try {
        val remoteData = downloadFromS3()
        Log.i(TAG, "getAllBookings")
        json.decodeFromString(remoteData)
    } catch(e: Exception) {
        Log.e("S3BookingRepository", "Failed to read S3 file", e)
        emptyList()
    }

    override suspend fun deleteBooking(id: String) {
        val updated = getAllBookings().filterNot { it.id == id}
        uploadToS3(json.encodeToString(updated))
        Log.i(TAG, "Delete booking ${id}")
    }

    private suspend fun downloadFromS3(): String = withContext(Dispatchers.IO) {
        val connection = URL(s3Url).openConnection() as HttpURLConnection
        Log.i(TAG, "Download from S3")
        connection.requestMethod = "GET"
        connection.inputStream.bufferedReader().readText()
    }

    private suspend fun uploadToS3(jsonText: String) = withContext(Dispatchers.IO) {
        val connection = URL(s3Url).openConnection() as HttpURLConnection
        connection.doOutput = true
        connection.requestMethod = "PUT"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.outputStream.use { it.write(jsonText.toByteArray())}
        Log.i(TAG, "Upload to S3: ${connection.responseCode}")
    }

}