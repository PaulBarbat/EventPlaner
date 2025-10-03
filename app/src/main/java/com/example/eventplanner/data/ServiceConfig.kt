package com.example.eventplanner.data

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ServiceEntry(
    val id: String,
    val displayName: String,
    val imageResourceNames: List<String>
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ServicesConfig(
    val services: List<ServiceEntry>,
    val allImages: List<String>
)

object ServicesConfigLoader {
    private val json = Json { ignoreUnknownKeys = true}

    fun load(context: Context): Pair<ServicesConfig, Map<String, Int>> {
        val text = context.assets.open("services.json").bufferedReader().use { it.readText()}
        val config = json.decodeFromString<ServicesConfig>(text)

        val nameToResId = config.allImages.associateWith { name ->
            context.resources.getIdentifier(name, "drawable", context.packageName).takeIf { it !=0}
                ?: error("Drawable not found for name: $name")
        }
        return config to nameToResId
    }
}