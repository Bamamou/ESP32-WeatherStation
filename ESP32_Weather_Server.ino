/*
 * ESP32 Weather Station Server
 * 
 * This code creates a web server on ESP32 that provides weather data
 * from different cities and communicates with the Android app.
 * 
 * Hardware connections:
 * - DHT22 sensor: Pin 4
 * - BMP280 sensor: SDA->21, SCL->22
 * - Status LED: Pin 2
 * 
 * Libraries needed:
 * - WiFi (built-in)
 * - ArduinoJson
 * - DHT sensor library
 * - Adafruit BMP280
 * - HTTPClient
 */

#include <WiFi.h>
#include <WebServer.h>
#include <ArduinoJson.h>
#include <HTTPClient.h>
#include <DHT.h>
#include <Wire.h>
#include <Adafruit_BMP280.h>

// WiFi credentials
const char* ssid = "YOUR_WIFI_SSID";
const char* password = "YOUR_WIFI_PASSWORD";

// Pin definitions
#define DHT_PIN 4
#define DHT_TYPE DHT22
#define STATUS_LED 2
#define SDA_PIN 21
#define SCL_PIN 22

// Sensor objects
DHT dht(DHT_PIN, DHT_TYPE);
Adafruit_BMP280 bmp;

// Web server
WebServer server(80);

// Weather data
struct WeatherData {
  float temperature;
  float humidity;
  float pressure;
  String location;
  unsigned long timestamp;
  String weatherCondition;
  float windSpeed;
  String windDirection;
  float uvIndex;
  float visibility;
};

// Available locations (cities)
String availableLocations[] = {
  "New York",
  "London", 
  "Tokyo",
  "Sydney",
  "Paris",
  "Berlin",
  "Local" // For local sensor data
};

String currentLocation = "Local";
WeatherData currentWeather;

// OpenWeatherMap API (optional - for getting real city weather)
const String WEATHER_API_KEY = "YOUR_OPENWEATHER_API_KEY"; // Get free API key from openweathermap.org
const String WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/weather";

void setup() {
  Serial.begin(115200);
  
  // Initialize pins
  pinMode(STATUS_LED, OUTPUT);
  digitalWrite(STATUS_LED, LOW);
  
  // Initialize sensors
  dht.begin();
  Wire.begin(SDA_PIN, SCL_PIN);
  
  if (!bmp.begin(0x76)) {
    Serial.println("Could not find a valid BMP280 sensor, check wiring!");
  }
  
  // Connect to WiFi
  connectToWiFi();
  
  // Setup web server routes
  setupWebServer();
  
  // Start server
  server.begin();
  Serial.println("ESP32 Weather Station Server started!");
  digitalWrite(STATUS_LED, HIGH);
}

void loop() {
  server.handleClient();
  
  // Update sensor data every 30 seconds
  static unsigned long lastUpdate = 0;
  if (millis() - lastUpdate > 30000) {
    updateLocalWeatherData();
    lastUpdate = millis();
  }
  
  delay(100);
}

void connectToWiFi() {
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  
  Serial.print("Connecting to ");
  Serial.println(ssid);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  
  Serial.println();
  Serial.println("WiFi connected!");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}

void setupWebServer() {
  // Enable CORS for all routes
  server.onNotFound([]() {
    if (server.method() == HTTP_OPTIONS) {
      server.sendHeader("Access-Control-Allow-Origin", "*");
      server.sendHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
      server.sendHeader("Access-Control-Allow-Headers", "Content-Type");
      server.send(200);
    } else {
      server.send(404, "text/plain", "Not found");
    }
  });
  
  // Get current weather
  server.on("/weather", HTTP_GET, []() {
    addCORSHeaders();
    if (currentLocation == "Local") {
      updateLocalWeatherData();
    } else {
      // Get weather from API for selected city
      getWeatherFromAPI(currentLocation);
    }
    sendWeatherResponse();
  });
  
  // Get weather for specific location
  server.on("/weather/", HTTP_GET, []() {
    addCORSHeaders();
    String location = server.pathArg(0);
    if (location == "Local") {
      updateLocalWeatherData();
    } else {
      getWeatherFromAPI(location);
    }
    sendWeatherResponse();
  });
  
  // Get available locations
  server.on("/locations", HTTP_GET, []() {
    addCORSHeaders();
    DynamicJsonDocument doc(1024);
    doc["success"] = true;
    doc["timestamp"] = millis();
    
    JsonArray locations = doc.createNestedArray("data");
    for (int i = 0; i < sizeof(availableLocations)/sizeof(availableLocations[0]); i++) {
      locations.add(availableLocations[i]);
    }
    
    String response;
    serializeJson(doc, response);
    server.send(200, "application/json", response);
  });
  
  // Set location
  server.on("/location", HTTP_POST, []() {
    addCORSHeaders();
    String body = server.arg("plain");
    DynamicJsonDocument requestDoc(512);
    deserializeJson(requestDoc, body);
    
    String newLocation = requestDoc["location"];
    
    // Validate location
    bool isValid = false;
    for (int i = 0; i < sizeof(availableLocations)/sizeof(availableLocations[0]); i++) {
      if (availableLocations[i] == newLocation) {
        isValid = true;
        break;
      }
    }
    
    DynamicJsonDocument doc(512);
    if (isValid) {
      currentLocation = newLocation;
      doc["success"] = true;
      doc["data"] = "Location set to " + newLocation;
    } else {
      doc["success"] = false;
      doc["data"] = "Invalid location";
    }
    doc["timestamp"] = millis();
    
    String response;
    serializeJson(doc, response);
    server.send(200, "application/json", response);
  });
  
  // Ping endpoint
  server.on("/ping", HTTP_GET, []() {
    addCORSHeaders();
    DynamicJsonDocument doc(256);
    doc["success"] = true;
    doc["data"] = "pong";
    doc["timestamp"] = millis();
    
    String response;
    serializeJson(doc, response);
    server.send(200, "application/json", response);
  });
  
  // Device status
  server.on("/status", HTTP_GET, []() {
    addCORSHeaders();
    DynamicJsonDocument doc(1024);
    doc["success"] = true;
    doc["timestamp"] = millis();
    
    JsonObject status = doc.createNestedObject("data");
    status["uptime"] = millis();
    status["freeMemory"] = ESP.getFreeHeap();
    status["wifiSignalStrength"] = WiFi.RSSI();
    status["firmwareVersion"] = "1.0.0";
    
    String response;
    serializeJson(doc, response);
    server.send(200, "application/json", response);
  });
}

void addCORSHeaders() {
  server.sendHeader("Access-Control-Allow-Origin", "*");
  server.sendHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
  server.sendHeader("Access-Control-Allow-Headers", "Content-Type");
}

void updateLocalWeatherData() {
  // Read local sensors
  currentWeather.temperature = dht.readTemperature();
  currentWeather.humidity = dht.readHumidity();
  
  if (bmp.begin()) {
    currentWeather.pressure = bmp.readPressure() / 100.0F; // Convert Pa to hPa
  }
  
  currentWeather.location = "Local Sensor";
  currentWeather.timestamp = millis();
  currentWeather.weatherCondition = getWeatherCondition(currentWeather.temperature, currentWeather.humidity);
  currentWeather.windSpeed = 0; // No wind sensor in basic setup
  currentWeather.windDirection = "N";
  currentWeather.uvIndex = 0; // No UV sensor in basic setup
  currentWeather.visibility = 10; // Default visibility
  
  Serial.println("Local weather updated:");
  Serial.printf("Temperature: %.1f°C, Humidity: %.1f%%, Pressure: %.1f hPa\n", 
                currentWeather.temperature, currentWeather.humidity, currentWeather.pressure);
}

void getWeatherFromAPI(String city) {
  if (WEATHER_API_KEY == "YOUR_OPENWEATHER_API_KEY") {
    // Use dummy data if no API key
    currentWeather.temperature = 20.0 + random(-10, 15);
    currentWeather.humidity = 50.0 + random(-20, 30);
    currentWeather.pressure = 1013.0 + random(-50, 50);
    currentWeather.location = city;
    currentWeather.timestamp = millis();
    currentWeather.weatherCondition = "Simulated";
    currentWeather.windSpeed = random(0, 20);
    currentWeather.windDirection = "SW";
    currentWeather.uvIndex = random(0, 10);
    currentWeather.visibility = 10;
    return;
  }
  
  // Make API call to OpenWeatherMap
  HTTPClient http;
  String url = WEATHER_BASE_URL + "?q=" + city + "&appid=" + WEATHER_API_KEY + "&units=metric";
  
  http.begin(url);
  int httpResponseCode = http.GET();
  
  if (httpResponseCode == 200) {
    String payload = http.getString();
    DynamicJsonDocument doc(2048);
    deserializeJson(doc, payload);
    
    currentWeather.temperature = doc["main"]["temp"];
    currentWeather.humidity = doc["main"]["humidity"];
    currentWeather.pressure = doc["main"]["pressure"];
    currentWeather.location = doc["name"];
    currentWeather.timestamp = millis();
    currentWeather.weatherCondition = doc["weather"][0]["description"];
    currentWeather.windSpeed = doc["wind"]["speed"];
    currentWeather.windDirection = "N"; // Simplified
    currentWeather.uvIndex = 0; // Not in basic API
    currentWeather.visibility = doc["visibility"] | 10000;
    currentWeather.visibility = currentWeather.visibility / 1000; // Convert to km
  } else {
    Serial.printf("Error getting weather data: %d\n", httpResponseCode);
    // Use local sensor data as fallback
    updateLocalWeatherData();
  }
  
  http.end();
}

void sendWeatherResponse() {
  DynamicJsonDocument doc(1024);
  doc["success"] = true;
  doc["timestamp"] = millis();
  
  JsonObject data = doc.createNestedObject("data");
  data["temperature"] = currentWeather.temperature;
  data["humidity"] = currentWeather.humidity;
  data["pressure"] = currentWeather.pressure;
  data["location"] = currentWeather.location;
  data["timestamp"] = currentWeather.timestamp;
  data["weather_condition"] = currentWeather.weatherCondition;
  data["wind_speed"] = currentWeather.windSpeed;
  data["wind_direction"] = currentWeather.windDirection;
  data["uv_index"] = currentWeather.uvIndex;
  data["visibility"] = currentWeather.visibility;
  
  String response;
  serializeJson(doc, response);
  server.send(200, "application/json", response);
}

String getWeatherCondition(float temp, float humidity) {
  if (temp < 0) return "Freezing";
  if (temp > 35) return "Very Hot";
  if (humidity > 80) return "Very Humid";
  if (humidity < 30) return "Dry";
  if (temp > 25 && temp < 30) return "Pleasant";
  return "Moderate";
}
