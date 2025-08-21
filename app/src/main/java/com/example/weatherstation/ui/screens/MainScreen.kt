package com.example.weatherstation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherstation.ui.components.*
import com.example.weatherstation.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: WeatherViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showConfigDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Weather Station") 
                },
                actions = {
                    IconButton(onClick = { showConfigDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                    IconButton(onClick = { viewModel.refreshWeatherData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Connection Status
            ConnectionStatusCard(
                esp32Device = uiState.esp32Device,
                onReconnect = { viewModel.testConnection() },
                onChangeIP = { showConfigDialog = true }
            )
            
            // Error Message
            uiState.errorMessage?.let { error ->
                ErrorCard(
                    errorMessage = error,
                    onDismiss = { viewModel.clearError() }
                )
            }
            
            // Location Selector
            if (uiState.availableLocations.isNotEmpty()) {
                LocationSelector(
                    availableLocations = uiState.availableLocations,
                    selectedLocation = uiState.selectedLocation,
                    onLocationSelected = { location ->
                        viewModel.selectLocation(location)
                    }
                )
            }
            
            // Current Weather
            if (uiState.isLoading && uiState.currentWeather == null) {
                LoadingCard()
            } else {
                uiState.currentWeather?.let { weather ->
                    WeatherCard(weatherData = weather)
                } ?: run {
                    NoDataCard(
                        onRetry = { viewModel.refreshWeatherData() }
                    )
                }
            }
            
            // Device Status (if available)
            uiState.deviceStatus?.let { status ->
                DeviceStatusCard(deviceStatus = status)
            }
            
            // Quick Actions
            QuickActionsCard(
                onRefresh = { viewModel.refreshWeatherData() },
                onLoadHistory = { viewModel.loadWeatherHistory() },
                onCheckStatus = { viewModel.getDeviceStatus() }
            )
        }
    }
    
    // Configuration Dialog
    if (showConfigDialog) {
        ESP32ConfigDialog(
            currentIP = uiState.esp32Device?.ipAddress ?: "192.168.1.100",
            onIPChanged = { newIP -> viewModel.updateESP32IP(newIP) },
            onDismiss = { showConfigDialog = false },
            onConnect = { viewModel.testConnection() }
        )
    }
}

@Composable
fun LoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading weather data...")
            }
        }
    }
}

@Composable
fun NoDataCard(onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = "No Data",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No weather data available",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "Make sure your ESP32 is connected and try again",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

@Composable
fun DeviceStatusCard(deviceStatus: com.example.weatherstation.network.DeviceStatus) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Device Status",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusItem("Uptime", formatUptime(deviceStatus.uptime))
                StatusItem("Memory", "${deviceStatus.freeMemory / 1024} KB")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusItem("WiFi Signal", "${deviceStatus.wifiSignalStrength} dBm")
                StatusItem("Firmware", deviceStatus.firmwareVersion)
            }
            
            deviceStatus.batteryLevel?.let { battery ->
                Spacer(modifier = Modifier.height(8.dp))
                StatusItem("Battery", "${battery.toInt()}%")
            }
        }
    }
}

@Composable
fun StatusItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun QuickActionsCard(
    onRefresh: () -> Unit,
    onLoadHistory: () -> Unit,
    onCheckStatus: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ElevatedButton(
                    onClick = onRefresh,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Refresh")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                ElevatedButton(
                    onClick = onLoadHistory,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.History, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("History")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                ElevatedButton(
                    onClick = onCheckStatus,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Status")
                }
            }
        }
    }
}

private fun formatUptime(uptimeMs: Long): String {
    val seconds = uptimeMs / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    
    return when {
        days > 0 -> "${days}d ${hours % 24}h"
        hours > 0 -> "${hours}h ${minutes % 60}m"
        minutes > 0 -> "${minutes}m ${seconds % 60}s"
        else -> "${seconds}s"
    }
}
