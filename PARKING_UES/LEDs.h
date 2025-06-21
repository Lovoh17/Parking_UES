#pragma once
#include <Arduino.h>
#include "Config.h"

enum class LEDMode {
    DIURNO,
    NOCTURNO,
    EMERGENCIA,
    MANTENIMIENTO
};

enum class LEDType {
    BLANCOS,
    ROJO,
    AZUL,
    VERDE,
    ALL
};

enum class BlinkPriority {
    NONE,
    ESPACIOS,      // Prioridad baja
    EMERGENCIA     // Prioridad alta
};

class LEDController {
private:
    // Tiempo y modo
    unsigned long lastUpdateTime;
    unsigned long lastBlinkTime;
    uint8_t simulatedHour;
    uint8_t simulatedMinute;
    LEDMode currentMode;
    LEDMode previousMode;
    
    // Estado de LEDs
    bool ledStates[4]; // [BLANCOS, ROJO, AZUL, VERDE]
    bool ledBaseStates[4]; // Estados base sin parpadeo
    
    // Sistema de parpadeo mejorado
    bool isBlinking;
    bool blinkState;
    uint16_t blinkInterval;
    LEDType blinkingLED;
    BlinkPriority currentBlinkPriority;
    
    // Espacios y configuración
    uint8_t espaciosLibres;
    uint8_t totalEspacios;
    bool autoModeEnabled;
    bool emergencyActive;
    
    // Funciones privadas
    void updateSimulatedTime();
    void checkLightSchedule();
    void updateBlinking();
    void setLEDPhysical(LEDType type, bool state); // Cambio de nombre
    void applyLightingMode();
    void debug(const String& mensaje);
    String getModeString(LEDMode mode) const;
    String formatTime() const;
    uint8_t getLEDPin(LEDType type) const;
    uint8_t getLEDIndex(LEDType type) const;
    
public:
    LEDController();
    void init();
    void update();
    
    // Control básico de LEDs
    void toggleLights(bool state);
    void setLED(LEDType type, bool state);
    void setAllLEDs(bool state);
    
    // Control de modos
    void setMode(LEDMode mode);
    void enableAutoMode(bool enable);
    void activateEmergency(bool activate);
    
    // Control de parpadeo mejorado
    void startBlinking(LEDType type, uint16_t intervalMs = 500, BlinkPriority priority = BlinkPriority::ESPACIOS);
    void stopBlinking(BlinkPriority priority = BlinkPriority::NONE);
    void forceStopAllBlinking();
    
    // Control de espacios
    void updateEspaciosLibres(uint8_t espacios);
    void indicarEspaciosDisponibles();
    
    // Getters
    uint8_t getHour() const { return simulatedHour; }
    uint8_t getMinute() const { return simulatedMinute; }
    LEDMode getCurrentMode() const { return currentMode; }
    bool isNight() const { return currentMode == LEDMode::NOCTURNO; }
    bool isEmergencyActive() const { return emergencyActive; }
    uint8_t getEspaciosLibres() const { return espaciosLibres; }
    bool isCurrentlyBlinking() const { return isBlinking; }
    LEDType getBlinkingLED() const { return blinkingLED; }
    
    // Configuración de tiempo
    void setSimulatedTime(uint8_t hour, uint8_t minute);
    void resetToDefaultTime();
    
    // Configuración avanzada
    void setBrightness(uint8_t level);
    void setSchedule(uint8_t hourOn, uint8_t hourOff);
    
    // Diagnóstico
    void printStatus() const;
    bool selfTest();
};
