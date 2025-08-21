# ESP32 Weather Station Android App

A modern Android application that connects to an ESP32 weather station via WiFi to display real-time weather data from different cities and local sensors.

## Features

### Android App Features:
- **Real-time Weather Display**: Temperature, humidity, pressure, wind speed, UV index
- **Multi-location Support**: Switch between different cities and local sensor data
- **ESP32 Connection Management**: Easy IP configuration and connection status monitoring
- **Modern Material 3 UI**: Clean, intuitive interface with dynamic color theming
- **Pull-to-refresh**: Quick data updates
- **Error Handling**: Comprehensive error messages and retry mechanisms
- **Device Status Monitoring**: ESP32 uptime, memory usage, WiFi signal strength

### ESP32 Features:
- **WiFi Web Server**: RESTful API for Android app communication
- **Multiple Data Sources**: Local sensors + weather API for different cities
- **Local Sensors Support**: DHT22 (temperature/humidity) + BMP280 (pressure)
- **Real-time Updates**: Continuous sensor monitoring
- **CORS Support**: Cross-origin requests for web and mobile access

## Hardware Requirements

### ESP32 Setup:
- ESP32 Development Board (ESP32-WROOM-32 or similar)
- DHT22 Temperature/Humidity sensor (optional)
- BMP280 Pressure sensor (optional)
- Status LED
- Breadboard and jumper wires

### Connections:
```
ESP32 Pin  ->  Component
GPIO 4     ->  DHT22 Data pin
GPIO 21    ->  BMP280 SDA
GPIO 22    ->  BMP280 SCL
GPIO 2     ->  Status LED (+ 330Ω resistor)
3.3V       ->  Sensor VCC
GND        ->  Sensor GND
```

## Software Setup

### Android App:
1. Open the project in Android Studio
2. Sync Gradle dependencies
3. Build and install the app on your Android device
4. Make sure your Android device is on the same WiFi network as ESP32

### ESP32 Setup:
1. Install required Arduino libraries:
   - ArduinoJson
   - DHT sensor library by Adafruit
   - Adafruit BMP280 library
   
2. Update WiFi credentials in `ESP32_Weather_Server.ino`:
   ```cpp
   const char* ssid = "YOUR_WIFI_SSID";
   const char* password = "YOUR_WIFI_PASSWORD";
   ```

3. (Optional) Get OpenWeatherMap API key for real city weather:
   - Sign up at [openweathermap.org](https://openweathermap.org/api)
   - Replace `YOUR_OPENWEATHER_API_KEY` in the code

4. Upload the code to your ESP32

## Usage

1. **Power on ESP32**: Connect to power and wait for WiFi connection
2. **Find ESP32 IP**: Check Serial Monitor for the assigned IP address
3. **Open Android App**: Launch the Weather Station app
4. **Configure Connection**: Tap the settings icon and enter ESP32 IP address
5. **Select Location**: Choose from available cities or use local sensor data
6. **Monitor Weather**: View real-time weather data with automatic updates

## API Endpoints

The ESP32 exposes the following REST API endpoints:

- `GET /weather` - Get current weather data
- `GET /weather/{location}` - Get weather for specific location  
- `GET /locations` - Get list of available locations
- `POST /location` - Set current location
- `GET /ping` - Test connection
- `GET /status` - Get device status

## Architecture

### Android App:
- **MVVM Architecture**: Clean separation of concerns
- **Jetpack Compose**: Modern declarative UI
- **Retrofit**: HTTP client for ESP32 communication
- **StateFlow**: Reactive state management
- **Material 3**: Modern design system

### ESP32:
- **WebServer Library**: HTTP server implementation
- **ArduinoJson**: JSON serialization/deserialization
- **Sensor Libraries**: Hardware abstraction for sensors
- **WiFi Management**: Connection handling and monitoring

## Customization

### Adding New Locations:
Edit the `availableLocations` array in the ESP32 code:
```cpp
String availableLocations[] = {
  "New York",
  "London", 
  "Your City",
  "Local"
};
```

### Adding New Sensors:
1. Update `WeatherData` struct in ESP32 code
2. Update `WeatherData.kt` in Android app
3. Modify `updateLocalWeatherData()` function
4. Update UI components to display new data

### Styling:
- Modify `WeatherStationTheme` for app-wide styling
- Update `WeatherCard` and other components for custom layouts
- Change colors in `MaterialTheme.colorScheme`

## Troubleshooting

### ESP32 Won't Connect to WiFi:
- Check WiFi credentials
- Ensure ESP32 is within range
- Try different WiFi network (avoid enterprise networks)
- Check Serial Monitor for error messages

### Android App Can't Connect:
- Verify ESP32 IP address (check Serial Monitor)
- Ensure both devices are on same network
- Check firewall settings
- Try ping from computer: `ping [ESP32_IP]`

### No Sensor Data:
- Check sensor wiring
- Verify sensor power (3.3V)
- Check I2C connections for BMP280
- Monitor Serial output for sensor errors

## Next Steps

This is a foundation that you can expand with:
- **Historical Data Storage**: Add SD card or cloud storage
- **Weather Alerts**: Threshold-based notifications
- **Multiple ESP32 Stations**: Network of weather stations
- **Data Visualization**: Charts and graphs
- **Automation**: Control devices based on weather conditions

## Contributing

Feel free to fork this project and submit pull requests for improvements!

## License

This project is open source and available under the MIT License.
