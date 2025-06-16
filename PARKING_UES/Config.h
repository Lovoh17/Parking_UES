#pragma once
#include <DHT.h>

// =============================================================================
// ARDUINO MEGA 2560 PIN CONFIGURATION
// Optimized pin layout to avoid conflicts
// Total digital pins used: 2-3, 12-13, 26-43, 48-49, 53
// Total analog pins used: A0, A1
// =============================================================================


// =============================================================================
// SYSTEM TIMING CONFIGURATION
// =============================================================================
constexpr uint32_t TIEMPO_ESPERA_CODIGO = 30000;    // 30 seconds
constexpr uint32_t TIEMPO_APERTURA = 3000;          // Door open time (ms)
constexpr uint32_t TIEMPO_SIMULACION_MS = 1000;     // 1s real = 1min simulated
constexpr uint32_t ACTUALIZACION_PARQUEO_MS = 1000; // LCD update interval
constexpr uint32_t TIEMPO_DEBOUNCE = 50;            // Debounce time (ms)


// =============================================================================
// SECURITY CONFIGURATION
// =============================================================================
// Admin RFID UID (replace with your actual RFID card)
const byte UID_ADMIN[] = {0xDE, 0xAD, 0xBE, 0xEF};

// Operating hours (24-hour format)
constexpr uint8_t HORA_APERTURA = 7;    // 7:00 AM
constexpr uint8_t HORA_CIERRE = 20;     // 8:00 PM
constexpr uint8_t TOTAL_ESPACIOS = 6;     //TOTAL_ESPACIOS


// =============================================================================
// SENSOR THRESHOLDS
// =============================================================================
// Ultrasonic distance thresholds (cm)
constexpr uint8_t DISTANCIA_DETECCION = 10;     // Detection threshold
constexpr uint8_t DISTANCIA_COLA_MIN = 20;      // Minimum queue distance
constexpr uint8_t DISTANCIA_COLA_MAX = 25;      // Maximum queue distance
constexpr uint8_t ESPACIOS_CRITICOS_THRESHOLD = 2; // Alerta cuando ≤2 espacios


// Environmental thresholds
constexpr int UMBRAL_GAS_PELIGROSO = 300;       // Dangerous gas level (ADC value)
constexpr float UMBRAL_TEMPERATURA_ALTA = 40.0f; // Critical temperature (°C)


// =============================================================================
// HARDWARE PIN ASSIGNMENTS
// =============================================================================

// -----------------------------------------------------------------------------
// ULTRASONIC SENSORS
// -----------------------------------------------------------------------------
constexpr uint8_t PIN_TRIG_ENTRADA = 32;
constexpr uint8_t PIN_ECHO_ENTRADA = 33;
constexpr uint8_t PIN_TRIG_SALIDA = 34;
constexpr uint8_t PIN_ECHO_SALIDA = 35;


// -----------------------------------------------------------------------------
// SERVO MOTORS
// -----------------------------------------------------------------------------
constexpr uint8_t PIN_SERVO_ENTRADA = 12;
constexpr uint8_t PIN_SERVO_SALIDA = 13;


// Servo angles
constexpr uint8_t PUERTA_ABIERTA = 90;
constexpr uint8_t PUERTA_CERRADA = 0;


// -----------------------------------------------------------------------------
// LIGHTING AND INDICATORS
// -----------------------------------------------------------------------------
constexpr uint8_t PIN_LED_ROJO_1 = 2;        // Emergency LEDs
constexpr uint8_t PIN_LED_ROJO_2 = 3;        // Emergency LEDs
constexpr uint8_t PIN_LEDS_BLANCOS = 36;     // General lighting
constexpr uint8_t PIN_LED_AZUL = 37;         // Status indicator
constexpr uint8_t PIN_BUZZER = 38;           // Audible alarm
#define PIN_LED_VERDE 10  // O -1 si no existe
#define DEBUG_ENABLED true

// -----------------------------------------------------------------------------
// SAFETY AND ENVIRONMENTAL SENSORS
// -----------------------------------------------------------------------------
constexpr uint8_t PIN_GAS = A0;              // Gas sensor (analog)
constexpr uint8_t PIN_TEMPERATURA = A1;      // Additional temperature sensor
constexpr uint8_t PIN_DHT = 39;              // DHT11 sensor
constexpr uint8_t PIN_VENTILADOR = 40;       // Ventilation control
constexpr uint8_t PIN_BOTON_EMERGENCIA = 41; // Emergency button


// DHT Configuration
constexpr uint8_t DHT_TYPE = DHT11;


// -----------------------------------------------------------------------------
// PARKING SENSORS (6 spaces)
// -----------------------------------------------------------------------------
constexpr uint8_t PIN_SENSOR_1 = 26;
constexpr uint8_t PIN_SENSOR_2 = 27;
constexpr uint8_t PIN_SENSOR_3 = 28;
constexpr uint8_t PIN_SENSOR_4 = 29;
constexpr uint8_t PIN_SENSOR_5 = 30;
constexpr uint8_t PIN_SENSOR_6 = 31;


// -----------------------------------------------------------------------------
// LCD DISPLAY (16x2 standard configuration)
// -----------------------------------------------------------------------------
constexpr uint8_t PIN_LCD_RS = 8;
constexpr uint8_t PIN_LCD_EN = 9;
constexpr uint8_t PIN_LCD_D4 = 4;
constexpr uint8_t PIN_LCD_D5 = 5;
constexpr uint8_t PIN_LCD_D6 = 6;
constexpr uint8_t PIN_LCD_D7 = 7;


// -----------------------------------------------------------------------------
// RFID (MFRC522) - SPI Communication
// -----------------------------------------------------------------------------
constexpr uint8_t PIN_RFID_SS = 53;          // Slave Select
constexpr uint8_t PIN_RFID_RST = 49;         // Reset
// Standard SPI pins for Arduino Mega:
// SCK  = 52
// MOSI = 51
// MISO = 50

// =============================================================================
// AVAILABLE PINS FOR FUTURE USE
// =============================================================================
/*
UNUSED DIGITAL PINS:
10, 11, 14-25, 42-48, 50-52

UNUSED ANALOG PINS:
A2-A15
*/
