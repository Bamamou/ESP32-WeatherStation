package com.example.weatherstation.network

import com.example.weatherstation.data.WeatherData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Network client for ESP32 communication
 */
object NetworkClient {
    
    private const val DEFAULT_TIMEOUT = 10L
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
        .build()
    
    fun createService(baseUrl: String): ESP32WeatherService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        return retrofit.create(ESP32WeatherService::class.java)
    }
}

/**
 * Repository for managing ESP32 weather data
 */
class WeatherRepository(private val esp32Service: ESP32WeatherService) {
    
    suspend fun getCurrentWeather(): Result<WeatherData> {
        return try {
            val response = esp32Service.getCurrentWeather()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("No data received"))
            } else {
                Result.failure(Exception("Failed to get weather data: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getWeatherForLocation(location: String): Result<WeatherData> {
        return try {
            val response = esp32Service.getWeatherForLocation(location)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("No data received"))
            } else {
                Result.failure(Exception("Failed to get weather data for $location"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAvailableLocations(): Result<List<String>> {
        return try {
            val response = esp32Service.getAvailableLocations()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { locations ->
                    Result.success(locations)
                } ?: Result.failure(Exception("No locations received"))
            } else {
                Result.failure(Exception("Failed to get available locations"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun setLocation(location: String): Result<String> {
        return try {
            val response = esp32Service.setLocation(LocationRequest(location))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data ?: "Location set successfully")
            } else {
                Result.failure(Exception("Failed to set location"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getWeatherHistory(hours: Int = 24): Result<List<WeatherData>> {
        return try {
            val response = esp32Service.getWeatherHistory(hours)
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { history ->
                    Result.success(history)
                } ?: Result.failure(Exception("No history data received"))
            } else {
                Result.failure(Exception("Failed to get weather history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun testConnection(): Result<Boolean> {
        return try {
            val response = esp32Service.ping()
            Result.success(response.isSuccessful && response.body()?.success == true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getDeviceStatus(): Result<DeviceStatus> {
        return try {
            val response = esp32Service.getDeviceStatus()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { status ->
                    Result.success(status)
                } ?: Result.failure(Exception("No device status received"))
            } else {
                Result.failure(Exception("Failed to get device status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
