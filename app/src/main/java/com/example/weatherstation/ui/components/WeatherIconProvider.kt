package com.example.weatherstation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Professional Weather Icon Manager
 * Uses carefully selected Material Design icons for maximum clarity and recognition
 */
object WeatherIconProvider {
    
    /**
     * Get weather icon based on weather condition and time of day
     * Optimized for best visual representation and user recognition
     */
    @Composable
    fun getWeatherIcon(condition: String, isDay: Boolean = true): ImageVector {
        return when (condition.lowercase()) {
            "clear", "sunny" -> {
                if (isDay) Icons.Default.WbSunny else Icons.Default.NightlightRound
            }
            "clouds", "cloudy", "overcast" -> Icons.Default.Cloud
            "partly cloudy", "few clouds", "scattered clouds" -> {
                if (isDay) Icons.Default.WbCloudy else Icons.Default.CloudQueue
            }
            "rain", "drizzle", "shower" -> Icons.Default.Umbrella
            "thunderstorm", "storm" -> Icons.Default.Thunderstorm
            "snow", "sleet" -> Icons.Default.AcUnit
            "mist", "fog", "haze" -> Icons.Default.Cloud
            "windy" -> Icons.Default.Air
            else -> {
                if (isDay) Icons.Default.WbSunny else Icons.Default.NightlightRound
            }
        }
    }
    
    /**
     * Get enhanced weather icons with outlined style for better visibility
     */
    @Composable
    fun getEnhancedWeatherIcon(condition: String, isDay: Boolean = true): ImageVector {
        return when (condition.lowercase()) {
            "clear", "sunny" -> {
                if (isDay) Icons.Outlined.WbSunny else Icons.Outlined.NightlightRound
            }
            "clouds", "cloudy", "overcast" -> Icons.Outlined.Cloud
            "partly cloudy", "few clouds", "scattered clouds" -> Icons.Outlined.WbCloudy
            "rain", "drizzle", "shower" -> Icons.Outlined.Umbrella
            "thunderstorm", "storm" -> Icons.Default.Thunderstorm
            "snow", "sleet" -> Icons.Outlined.AcUnit
            "mist", "fog", "haze" -> Icons.Default.Cloud
            "windy" -> Icons.Outlined.Air
            else -> {
                if (isDay) Icons.Outlined.WbSunny else Icons.Outlined.NightlightRound
            }
        }
    }
    
    /**
     * Get icon for weather parameters - Professional and Clear
     */
    @Composable
    fun getParameterIcon(parameter: String): ImageVector {
        return when (parameter.lowercase()) {
            "temperature", "temp" -> Icons.Default.Thermostat
            "humidity" -> Icons.Default.WaterDrop
            "pressure" -> Icons.Default.Speed
            "wind", "wind_speed" -> Icons.Default.Air
            "uv", "uv_index" -> Icons.Default.WbSunny
            "device", "esp32" -> Icons.Default.Wifi
            "location" -> Icons.Default.LocationOn
            "settings" -> Icons.Default.Settings
            "refresh" -> Icons.Default.Refresh
            "connected" -> Icons.Default.CheckCircle
            "disconnected" -> Icons.Default.Error
            else -> Icons.Default.Info
        }
    }
    
    /**
     * Get weather condition from temperature and humidity
     * This provides intelligent weather detection for ESP32 without external API
     */
    fun getConditionFromSensors(temperature: Double, humidity: Double, pressure: Double?): String {
        return when {
            humidity > 85 && temperature < 10 -> "fog"
            humidity > 80 -> "rain"
            humidity < 30 && temperature > 25 -> "sunny"
            humidity in 30.0..60.0 -> "clear"
            pressure != null && pressure < 1010 -> "cloudy"
            else -> "partly cloudy"
        }
    }
    
    /**
     * Determine if it's day or night based on current time
     */
    fun isDayTime(): Boolean {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return hour in 6..18
    }
    
    /**
     * Get color for weather condition
     */
    @Composable
    fun getWeatherColor(condition: String): androidx.compose.ui.graphics.Color {
        return when (condition.lowercase()) {
            "clear", "sunny" -> androidx.compose.ui.graphics.Color(0xFFFFC107)
            "clouds", "cloudy" -> androidx.compose.ui.graphics.Color(0xFF90A4AE)
            "rain", "drizzle" -> androidx.compose.ui.graphics.Color(0xFF2196F3)
            "thunderstorm", "storm" -> androidx.compose.ui.graphics.Color(0xFF673AB7)
            "snow" -> androidx.compose.ui.graphics.Color(0xFF81D4FA)
            "fog", "mist" -> androidx.compose.ui.graphics.Color(0xFFB0BEC5)
            else -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
        }
    }
}
