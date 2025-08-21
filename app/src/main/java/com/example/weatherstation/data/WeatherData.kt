package com.example.weatherstation.data

import com.google.gson.annotations.SerializedName

/**
 * Data class representing weather information from ESP32
 */
data class WeatherData(
    @SerializedName("temperature")
    val temperature: Float = 0.0f,
    
    @SerializedName("humidity")
    val humidity: Float = 0.0f,
    
    @SerializedName("pressure")
    val pressure: Float = 0.0f,
    
    @SerializedName("location")
    val location: String = "",
    
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    
    @SerializedName("weather_condition")
    val weatherCondition: String = "Unknown",
    
    @SerializedName("wind_speed")
    val windSpeed: Float = 0.0f,
    
    @SerializedName("wind_direction")
    val windDirection: String = "N",
    
    @SerializedName("uv_index")
    val uvIndex: Float = 0.0f,
    
    @SerializedName("visibility")
    val visibility: Float = 0.0f
)

/**
 * ESP32 device information
 */
data class ESP32Device(
    val ipAddress: String,
    val name: String,
    val isConnected: Boolean = false,
    val lastSeen: Long = 0L
)

/**
 * Weather history entry for tracking data over time
 */
data class WeatherHistoryEntry(
    val timestamp: Long,
    val temperature: Float,
    val humidity: Float,
    val pressure: Float,
    val location: String
)

/**
 * Response wrapper for ESP32 API calls
 */
data class ESP32Response<T>(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("data")
    val data: T?,
    
    @SerializedName("message")
    val message: String = "",
    
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)
