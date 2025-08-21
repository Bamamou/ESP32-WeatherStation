package com.example.weatherstation.network

import com.example.weatherstation.data.ESP32Response
import com.example.weatherstation.data.WeatherData
import retrofit2.Response
import retrofit2.http.*

/**
 * REST API interface for communicating with ESP32 weather station
 */
interface ESP32WeatherService {
    
    /**
     * Get current weather data from ESP32
     */
    @GET("/weather")
    suspend fun getCurrentWeather(): Response<ESP32Response<WeatherData>>
    
    /**
     * Get weather data for a specific location
     */
    @GET("/weather/{location}")
    suspend fun getWeatherForLocation(@Path("location") location: String): Response<ESP32Response<WeatherData>>
    
    /**
     * Get list of available locations
     */
    @GET("/locations")
    suspend fun getAvailableLocations(): Response<ESP32Response<List<String>>>
    
    /**
     * Set the current location for weather monitoring
     */
    @POST("/location")
    suspend fun setLocation(@Body locationRequest: LocationRequest): Response<ESP32Response<String>>
    
    /**
     * Get historical weather data
     */
    @GET("/history")
    suspend fun getWeatherHistory(
        @Query("hours") hours: Int = 24
    ): Response<ESP32Response<List<WeatherData>>>
    
    /**
     * Test ESP32 connection
     */
    @GET("/ping")
    suspend fun ping(): Response<ESP32Response<String>>
    
    /**
     * Get ESP32 device status
     */
    @GET("/status")
    suspend fun getDeviceStatus(): Response<ESP32Response<DeviceStatus>>
}

/**
 * Request body for setting location
 */
data class LocationRequest(
    val location: String
)

/**
 * ESP32 device status information
 */
data class DeviceStatus(
    val uptime: Long,
    val freeMemory: Long,
    val wifiSignalStrength: Int,
    val firmwareVersion: String,
    val batteryLevel: Float? = null
)
