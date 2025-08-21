package com.example.weatherstation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherstation.data.WeatherData
import com.example.weatherstation.data.ESP32Device
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherCard(
    weatherData: WeatherData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Weather Station: ${weatherData.location}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Last updated: ${formatTimestamp(weatherData.timestamp)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Main weather condition display with enhanced icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = WeatherIconProvider.getEnhancedWeatherIcon(
                        condition = weatherData.weatherCondition,
                        isDay = WeatherIconProvider.isDayTime()
                    ),
                    contentDescription = "Weather condition",
                    modifier = Modifier.size(80.dp),
                    tint = WeatherIconProvider.getWeatherColor(weatherData.weatherCondition)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "${weatherData.temperature}°C",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = "Condition: ${weatherData.weatherCondition}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Weather details grid
            WeatherDetailsGrid(weatherData)
        }
    }
}

@Composable
fun WeatherDetailsGrid(weatherData: WeatherData) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailItem(
                icon = WeatherIconProvider.getParameterIcon("humidity"),
                label = "Humidity",
                value = "${weatherData.humidity}%",
                modifier = Modifier.weight(1f)
            )
            
            WeatherDetailItem(
                icon = WeatherIconProvider.getParameterIcon("pressure"),
                label = "Pressure", 
                value = "${weatherData.pressure} hPa",
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailItem(
                icon = WeatherIconProvider.getParameterIcon("wind"),
                label = "Wind",
                value = "${weatherData.windSpeed} km/h ${weatherData.windDirection}",
                modifier = Modifier.weight(1f)
            )
            
            WeatherDetailItem(
                icon = WeatherIconProvider.getParameterIcon("uv"),
                label = "UV Index",
                value = "${weatherData.uvIndex}",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun WeatherDetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ConnectionStatusCard(
    esp32Device: ESP32Device,
    onReconnect: () -> Unit,
    onChangeIP: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (esp32Device.isConnected) 
                MaterialTheme.colorScheme.primaryContainer 
            else MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (esp32Device.isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                    contentDescription = if (esp32Device.isConnected) "Connected" else "Disconnected",
                    tint = if (esp32Device.isConnected) Color.Green else Color.Red,
                    modifier = Modifier.size(32.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "ESP32: ${esp32Device.name}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (esp32Device.isConnected) "Connected" else "Disconnected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Row {
                IconButton(onClick = onReconnect) {
                    Icon(Icons.Default.Refresh, "Reconnect")
                }
                IconButton(onClick = onChangeIP) {
                    Icon(Icons.Default.Settings, "Change IP")
                }
            }
        }
    }
}

@Composable
fun ErrorCard(
    errorMessage: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
fun LocationSelector(
    availableLocations: List<String>,
    selectedLocation: String?,
    onLocationSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Select Location",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableLocations) { location ->
                    FilterChip(
                        onClick = { onLocationSelected(location) },
                        label = { Text(location) },
                        selected = selectedLocation == location,
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(18.dp))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading weather data...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun NoDataCard(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = "No data",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No weather data available",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Check your ESP32 connection and try again",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = onRetry) {
                Icon(Icons.Default.Refresh, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

@Composable
fun DeviceStatusCard(
    deviceStatus: Map<String, Any>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
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
            
            deviceStatus.entries.forEach { (key, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = key.replaceFirstChar { it.uppercaseChar() },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun ESP32DeviceCard(
    device: ESP32Device,
    onRefresh: () -> Unit = {},
    onDisconnect: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (device.isConnected) 
                MaterialTheme.colorScheme.primaryContainer 
            else MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = WeatherIconProvider.getParameterIcon(
                            if (device.isConnected) "esp32_connected" else "esp32_disconnected"
                        ),
                        contentDescription = if (device.isConnected) "Connected ESP32" else "Disconnected ESP32",
                        tint = if (device.isConnected) Color.Green else Color.Red
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Device: ${device.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "IP: ${device.ipAddress}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        if (!device.isConnected) {
                            Text(
                                text = "Last seen: ${formatTimestamp(device.lastSeen)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            Row {
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
                
                IconButton(onClick = onDisconnect) {
                    Icon(
                        imageVector = Icons.Default.PowerOff,
                        contentDescription = "Disconnect"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlPanel(
    esp32Devices: List<ESP32Device>,
    onDeviceSelect: (ESP32Device) -> Unit,
    onRefreshAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onRefreshAll) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh all",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Control Panel",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            IconButton(onClick = { /* Settings */ }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun DeviceFilterBar(
    devices: List<ESP32Device>,
    selectedDevice: ESP32Device?,
    onDeviceSelect: (ESP32Device?) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Connected Devices",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(devices) { device ->
                    FilterChip(
                        onClick = { onDeviceSelect(device) },
                        label = { Text(device.name) },
                        selected = selectedDevice == device,
                        leadingIcon = {
                            Icon(Icons.Default.DeviceHub, null, modifier = Modifier.size(18.dp))
                        }
                    )
                }
            }
        }
    }
}

// Helper function for timestamp formatting
private fun formatTimestamp(timestamp: Long): String {
    val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

// Helper function for temperature color coding
private fun getTemperatureColor(temperature: Double): Color {
    return when {
        temperature < 0 -> Color(0xFF2196F3)  // Blue
        temperature < 10 -> Color(0xFF00BCD4) // Cyan
        temperature < 20 -> Color(0xFF4CAF50) // Green
        temperature < 30 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336)              // Red
    }
}
