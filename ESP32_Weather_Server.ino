/*
 * ESP32 Weather Station Server - OPTIMIZED FOR SPEED
 * 
 * Lightweight version using only DHT sensor for maximum performance
 * 
 * Hardware connections:
 * - DHT22 sensor: Pin 4
 * 
 * Libraries needed:
 * - WiFi (built-in)
 * - ArduinoJson
 * - DHT sensor library
 */

#include <WiFi.h>
#include <WebServer.h>
#include <ArduinoJson.h>
#include <DHT.h>

// WiFi credentials
const char* ssid = "YOUR_WIFI_SSID";
const char* password = "YOUR_WIFI_PASSWORD";

// Pin definitions
#define DHT_PIN 4
#define DHT_TYPE DHT22

// Sensor object
DHT dht(DHT_PIN, DHT_TYPE);

// Web server
WebServer server(80);

// Cached sensor data for fast responses
float cachedTemp = 0;
float cachedHumidity = 0;
unsigned long lastSensorRead = 0;
const unsigned long SENSOR_INTERVAL = 10000; // 10 seconds cache

void setup() {
  Serial.begin(115200);
  
  // Initialize DHT sensor only
  dht.begin();
  
  // Connect to WiFi
  connectToWiFi();
  
  // Setup web server routes (minimal)
  setupWebServer();
  
  // Start server
  server.begin();
  Serial.println("ESP32 Weather Station Server started!");
}

void loop() {
  // Handle client requests immediately
  server.handleClient();
  
  // Update cached sensor data every 10 seconds for speed
  if (millis() - lastSensorRead > SENSOR_INTERVAL) {
    readSensorData();
    lastSensorRead = millis();
  }
  
  // Minimal delay for stability
  delay(10);
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
  
  // Get current weather - OPTIMIZED
  server.on("/weather", HTTP_GET, []() {
    addCORSHeaders();
    sendFastWeatherResponse();
  });
  
  // Ping endpoint - MINIMAL
  server.on("/ping", HTTP_GET, []() {
    addCORSHeaders();
    server.send(200, "application/json", "{\"success\":true,\"data\":\"pong\"}");
  });
  
  // Device status - ESSENTIAL ONLY
  server.on("/status", HTTP_GET, []() {
    addCORSHeaders();
    DynamicJsonDocument doc(512);
    doc["success"] = true;
    doc["timestamp"] = millis();
    
    JsonObject status = doc.createNestedObject("data");
    status["uptime"] = millis();
    status["freeMemory"] = ESP.getFreeHeap();
    
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

void readSensorData() {
  cachedTemp = dht.readTemperature();
  cachedHumidity = dht.readHumidity();
  
  // Check if readings are valid
  if (isnan(cachedTemp) || isnan(cachedHumidity)) {
    Serial.println("Failed to read from DHT sensor!");
    return;
  }
  
  Serial.printf("Sensor updated: %.1f°C, %.1f%%\n", cachedTemp, cachedHumidity);
}

void sendFastWeatherResponse() {
  // Use pre-allocated buffer for speed
  DynamicJsonDocument doc(512);
  doc["success"] = true;
  doc["timestamp"] = millis();
  
  JsonObject data = doc.createNestedObject("data");
  data["temperature"] = cachedTemp;
  data["humidity"] = cachedHumidity;
  data["location"] = "Local Sensor";
  data["weather_condition"] = getSimpleWeatherCondition(cachedTemp, cachedHumidity);
  
  String response;
  response.reserve(256); // Pre-allocate string memory
  serializeJson(doc, response);
  server.send(200, "application/json", response);
}

String getSimpleWeatherCondition(float temp, float humidity) {
  if (temp < 0) return "Cold";
  if (temp > 30) return "Hot";
  if (humidity > 70) return "Humid";
  if (humidity < 40) return "Dry";
  return "Pleasant";
}
