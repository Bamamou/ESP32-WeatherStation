package com.example.weatherstation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherstation.data.ESP32Device
import com.example.weatherstation.data.WeatherData
import com.example.weatherstation.network.DeviceStatus
import com.example.weatherstation.network.NetworkClient
import com.example.weatherstation.network.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/**
 * ViewModel for managing weather station data and UI state
 */
class WeatherViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    private var weatherRepository: WeatherRepository? = null
    
    // Default ESP32 IP - can be changed by user
    private var esp32IpAddress = "192.168.1.100"
    
    init {
        // Initialize with default IP
        connectToESP32(esp32IpAddress)
    }
    
    fun connectToESP32(ipAddress: String) {
        esp32IpAddress = ipAddress
        val baseUrl = "http://$ipAddress/"
        val service = NetworkClient.createService(baseUrl)
        weatherRepository = WeatherRepository(service)
        
        _uiState.value = _uiState.value.copy(
            esp32Device = ESP32Device(
                ipAddress = ipAddress,
                name = "ESP32 Weather Station",
                isConnected = false
            ),
            isLoading = true
        )
        
        testConnection()
    }
    
    fun testConnection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            weatherRepository?.testConnection()?.fold(
                onSuccess = { isConnected ->
                    _uiState.value = _uiState.value.copy(
                        esp32Device = _uiState.value.esp32Device?.copy(
                            isConnected = isConnected,
                            lastSeen = System.currentTimeMillis()
                        ),
                        isLoading = false
                    )
                    
                    if (isConnected) {
                        // Start periodic data updates
                        startPeriodicUpdates()
                        // Load initial data
                        refreshWeatherData()
                        loadAvailableLocations()
                    }
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        esp32Device = _uiState.value.esp32Device?.copy(isConnected = false),
                        isLoading = false,
                        errorMessage = "Connection failed: ${error.message}"
                    )
                }
            )
        }
    }
    
    fun refreshWeatherData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, errorMessage = null)
            
            weatherRepository?.getCurrentWeather()?.fold(
                onSuccess = { weatherData ->
                    _uiState.value = _uiState.value.copy(
                        currentWeather = weatherData,
                        isRefreshing = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        errorMessage = "Failed to refresh weather data: ${error.message}"
                    )
                }
            )
        }
    }
    
    fun loadAvailableLocations() {
        viewModelScope.launch {
            weatherRepository?.getAvailableLocations()?.fold(
                onSuccess = { locations ->
                    _uiState.value = _uiState.value.copy(
                        availableLocations = locations
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to load locations: ${error.message}"
                    )
                }
            )
        }
    }
    
    fun selectLocation(location: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            weatherRepository?.setLocation(location)?.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        selectedLocation = location,
                        isLoading = false
                    )
                    // Refresh weather data for new location
                    refreshWeatherData()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to set location: ${error.message}"
                    )
                }
            )
        }
    }
    
    fun loadWeatherHistory(hours: Int = 24) {
        viewModelScope.launch {
            weatherRepository?.getWeatherHistory(hours)?.fold(
                onSuccess = { history ->
                    _uiState.value = _uiState.value.copy(
                        weatherHistory = history
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to load weather history: ${error.message}"
                    )
                }
            )
        }
    }
    
    fun getDeviceStatus() {
        viewModelScope.launch {
            weatherRepository?.getDeviceStatus()?.fold(
                onSuccess = { status ->
                    _uiState.value = _uiState.value.copy(
                        deviceStatus = status
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to get device status: ${error.message}"
                    )
                }
            )
        }
    }
    
    private fun startPeriodicUpdates() {
        viewModelScope.launch {
            while (_uiState.value.esp32Device?.isConnected == true) {
                delay(30000) // Update every 30 seconds
                refreshWeatherData()
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun updateESP32IP(newIP: String) {
        connectToESP32(newIP)
    }
}

/**
 * UI State for the weather app
 */
data class WeatherUiState(
    val currentWeather: WeatherData? = null,
    val weatherHistory: List<WeatherData> = emptyList(),
    val availableLocations: List<String> = emptyList(),
    val selectedLocation: String = "",
    val esp32Device: ESP32Device? = null,
    val deviceStatus: DeviceStatus? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
