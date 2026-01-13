package com.example.eventplanner.data.remote.aws

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import com.example.eventplanner.data.ServicesConfig

object ServicesRemoteLoader{
    private const val REMOTE_URL = "https://the-tuk-bucket.s3.eu-central-1.amazonaws.com/services.json"
    private const val LOCAL_FILE = "services.json"
    private val json = Json { ignoreUnknownKeys=true}

    suspend fun loadConfig(context: Context): Pair<ServicesConfig?, Boolean>{
        return withContext(Dispatchers.IO) {
            val file = File(context.filesDir, LOCAL_FILE)
            try{
                val connection = URL(REMOTE_URL).openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.requestMethod = "GET"

                if(connection.responseCode == 200) {
                    val data = connection.inputStream.bufferedReader().readText()
                    file.writeText(data)
                    val config = json.decodeFromString<ServicesConfig>(data)
                    Log.i("ServicesRemoteLoader", "HTTP ${connection.responseCode}")
                    return@withContext config to false
                } else {
                    Log.w("ServicesRemoteLoader", "HTTP ${connection.responseCode}, using local file.")
                }
            } catch (e: Exception) {
                Log.e("ServicesRemoteLoader", "Failed to fetch remote config", e)
                }

            if(file.exists()) {
                val localData = file.readText()
                val config = json.decodeFromString<ServicesConfig>(localData)
                return@withContext config to true
            }

            val bundled = context.assets.open("services.json").bufferedReader().use { it.readText() }
            val config = json.decodeFromString<ServicesConfig>(bundled)
            file.writeText(bundled)
            return@withContext config to true
        }
    }
}