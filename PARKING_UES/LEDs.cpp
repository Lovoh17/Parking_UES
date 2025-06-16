#include "LEDs.h"

LEDController::LEDController() :
    lastUpdateTime(0),
    lastBlinkTime(0),
    simulatedHour(8),
    simulatedMinute(0),
    currentMode(LEDMode::DIURNO),
    previousMode(LEDMode::DIURNO),
    isBlinking(false),
    blinkState(false),
    blinkInterval(500),
    blinkingLED(LEDType::ROJO),
    currentBlinkPriority(BlinkPriority::NONE),
    espaciosLibres(TOTAL_ESPACIOS),
    totalEspacios(TOTAL_ESPACIOS),
    autoModeEnabled(true),
    emergencyActive(false)
{
    // Inicializar estados de LEDs
    for (int i = 0; i < 4; i++) {
        ledStates[i] = false;
        ledBaseStates[i] = false;
    }
}

void LEDController::init() {
    // Configurar pines como salida
    pinMode(PIN_LEDS_BLANCOS, OUTPUT);
    pinMode(PIN_LED_ROJO_2, OUTPUT);
    pinMode(PIN_LED_AZUL, OUTPUT);
    
    // PIN_LED_VERDE es opcional
    if (PIN_LED_VERDE != -1) {
        pinMode(PIN_LED_VERDE, OUTPUT);
    }
    
    // Apagar todos los LEDs inicialmente
    setAllLEDs(false);
    
    // Configuración inicial
    currentMode = LEDMode::DIURNO;
    espaciosLibres = totalEspacios;
    autoModeEnabled = true;
    emergencyActive = false;
    
    // Resetear sistema de parpadeo
    forceStopAllBlinking();
    
    // Aplicar modo inicial
    applyLightingMode();
    
    debug("Sistema LED inicializado - " + formatTime() + 
          " | Espacios: " + String(espaciosLibres) + 
          " | Modo: " + getModeString(currentMode));
          
    // Auto-test opcional
    if (DEBUG_ENABLED) {
        selfTest();
    }
}

void LEDController::update() {
    updateSimulatedTime();
    
    if (autoModeEnabled && !emergencyActive) {
        checkLightSchedule();
    }
    
    updateBlinking();
    indicarEspaciosDisponibles();
}

void LEDController::updateSimulatedTime() {
    if (millis() - lastUpdateTime >= TIEMPO_SIMULACION_MS) {
        lastUpdateTime = millis();
        simulatedMinute++;
        
        if (simulatedMinute >= 60) {
            simulatedMinute = 0;
            simulatedHour = (simulatedHour + 1) % 24;
            
            debug("Cambio de hora: " + formatTime());
            
            if (simulatedHour == 0) {
                debug("¡Nuevo día iniciado!");
            }
        }
    }
}

void LEDController::checkLightSchedule() {
    LEDMode newMode;
    
    // Determinar modo basado en horario (7:00-19:00 = DIURNO)
    if (simulatedHour >= 19 || simulatedHour < 7) {
        newMode = LEDMode::NOCTURNO;
    } else {
        newMode = LEDMode::DIURNO;
    }
    
    if (newMode != currentMode) {
        previousMode = currentMode;
        currentMode = newMode;
        
        debug("Cambio de modo: " + getModeString(previousMode) + 
              " -> " + getModeString(currentMode) + " - " + formatTime());
        
        applyLightingMode();
    }
}

void LEDController::applyLightingMode() {
    // Detener parpadeos de prioridad baja al cambiar modo
    if (currentBlinkPriority == BlinkPriority::ESPACIOS) {
        stopBlinking(BlinkPriority::ESPACIOS);
    }
    
    switch (currentMode) {
        case LEDMode::DIURNO:
            setLED(LEDType::BLANCOS, false);
            setLED(LEDType::AZUL, true);
            setLED(LEDType::ROJO, false);
            break;
            
        case LEDMode::NOCTURNO:
            setLED(LEDType::BLANCOS, true);
            setLED(LEDType::AZUL, false);
            setLED(LEDType::ROJO, false);
            break;
            
        case LEDMode::EMERGENCIA:
            setLED(LEDType::BLANCOS, true);
            setLED(LEDType::AZUL, false);
            startBlinking(LEDType::ROJO, 250, BlinkPriority::EMERGENCIA);
            break;
            
        case LEDMode::MANTENIMIENTO:
            setLED(LEDType::BLANCOS, false);
            setLED(LEDType::AZUL, false);
            setLED(LEDType::ROJO, true);
            forceStopAllBlinking();
            break;
    }
}

void LEDController::updateBlinking() {
    if (isBlinking && millis() - lastBlinkTime >= blinkInterval) {
        lastBlinkTime = millis();
        blinkState = !blinkState;
        
        // Aplicar estado de parpadeo al LED específico
        uint8_t pin = getLEDPin(blinkingLED);
        uint8_t index = getLEDIndex(blinkingLED);
        
        if (pin != 0 && index < 4) {
            digitalWrite(pin, blinkState ? HIGH : LOW);
            ledStates[index] = blinkState;
            
            // No actualizar ledBaseStates durante parpadeo
        }
    }
}

void LEDController::indicarEspaciosDisponibles() {
    // Solo actuar si no hay emergencia o mantenimiento activo
    if (currentMode == LEDMode::EMERGENCIA || currentMode == LEDMode::MANTENIMIENTO) {
        return;
    }
    
    // Solo actuar si no hay parpadeo de mayor prioridad
    if (currentBlinkPriority == BlinkPriority::EMERGENCIA) {
        return;
    }
    
    if (espaciosLibres == 0) {
        // Parqueo lleno - LED rojo fijo
        if (isBlinking && blinkingLED == LEDType::ROJO) {
            stopBlinking(BlinkPriority::ESPACIOS);
        }
        setLED(LEDType::ROJO, true);
        
    } else if (espaciosLibres <= 2) {
        // Pocos espacios - LED rojo parpadeando lento
        if (!isBlinking || blinkingLED != LEDType::ROJO) {
            startBlinking(LEDType::ROJO, 1000, BlinkPriority::ESPACIOS);
        }
        
    } else {
        // Espacios suficientes disponibles
        if (isBlinking && currentBlinkPriority == BlinkPriority::ESPACIOS) {
            stopBlinking(BlinkPriority::ESPACIOS);
        }
        setLED(LEDType::ROJO, false);
    }
}

void LEDController::setLED(LEDType type, bool state) {
    if (type == LEDType::ALL) {
        setAllLEDs(state);
        return;
    }
    
    uint8_t pin = getLEDPin(type);
    uint8_t index = getLEDIndex(type);
    
    if (pin == 0 || index >= 4) {
        return; // PIN inválido o no configurado
    }
    
    // Actualizar estado base
    ledBaseStates[index] = state;
    
    // Si el LED no está parpadeando, actualizar físicamente
    if (!isBlinking || blinkingLED != type) {
        setLEDPhysical(type, state);
    }
}

void LEDController::setLEDPhysical(LEDType type, bool state) {
    uint8_t pin = getLEDPin(type);
    uint8_t index = getLEDIndex(type);
    
    if (pin != 0 && index < 4) {
        digitalWrite(pin, state ? HIGH : LOW);
        ledStates[index] = state;
    }
}

void LEDController::setAllLEDs(bool state) {
    setLED(LEDType::BLANCOS, state);
    setLED(LEDType::ROJO, state);
    setLED(LEDType::AZUL, state);
    if (PIN_LED_VERDE != -1) {
        setLED(LEDType::VERDE, state);
    }
}

void LEDController::toggleLights(bool state) {
    setLED(LEDType::BLANCOS, state);
    debug("LEDs BLANCOS " + String(state ? "ENCENDIDOS" : "APAGADOS") + 
          " - " + formatTime() + " | Modo: " + getModeString(currentMode));
}

void LEDController::setMode(LEDMode mode) {
    if (mode != currentMode) {
        previousMode = currentMode;
        currentMode = mode;
        emergencyActive = (mode == LEDMode::EMERGENCIA);
        applyLightingMode();
        
        debug("Modo cambiado manualmente a: " + getModeString(currentMode));
    }
}

void LEDController::enableAutoMode(bool enable) {
    autoModeEnabled = enable;
    debug("Modo automático " + String(enable ? "ACTIVADO" : "DESACTIVADO"));
}

void LEDController::activateEmergency(bool activate) {
    emergencyActive = activate;
    if (activate) {
        setMode(LEDMode::EMERGENCIA);
    } else {
        // Volver al modo automático apropiado
        LEDMode autoMode = (simulatedHour >= 19 || simulatedHour < 7) ? 
                          LEDMode::NOCTURNO : LEDMode::DIURNO;
        setMode(autoMode);
    }
    
    debug("Emergencia " + String(activate ? "ACTIVADA" : "DESACTIVADA"));
}

void LEDController::startBlinking(LEDType type, uint16_t intervalMs, BlinkPriority priority) {
    // Verificar si se puede iniciar el parpadeo
    if (isBlinking && priority <= currentBlinkPriority) {
        return; // No se puede sobrescribir parpadeo de mayor prioridad
    }
    
    // Detener parpadeo anterior si existe
    if (isBlinking) {
        // Restaurar estado base del LED anterior
        uint8_t prevIndex = getLEDIndex(blinkingLED);
        if (prevIndex < 4) {
            setLEDPhysical(blinkingLED, ledBaseStates[prevIndex]);
        }
    }
    
    // Configurar nuevo parpadeo
    blinkingLED = type;
    isBlinking = true;
    blinkInterval = intervalMs;
    blinkState = false;
    lastBlinkTime = millis();
    currentBlinkPriority = priority;
    
    debug("Parpadeo iniciado - LED: " + String((int)type) + 
          " | Intervalo: " + String(intervalMs) + "ms" +
          " | Prioridad: " + String((int)priority));
}

void LEDController::stopBlinking(BlinkPriority priority) {
    if (!isBlinking) return;
    
    // Solo detener si la prioridad coincide o es NONE (detener cualquiera)
    if (priority != BlinkPriority::NONE && priority != currentBlinkPriority) {
        return;
    }
    
    // Restaurar estado base del LED
    uint8_t index = getLEDIndex(blinkingLED);
    if (index < 4) {
        setLEDPhysical(blinkingLED, ledBaseStates[index]);
    }
    
    isBlinking = false;
    blinkState = false;
    currentBlinkPriority = BlinkPriority::NONE;
    
    debug("Parpadeo detenido");
}

void LEDController::forceStopAllBlinking() {
    if (isBlinking) {
        // Restaurar estado base
        uint8_t index = getLEDIndex(blinkingLED);
        if (index < 4) {
            setLEDPhysical(blinkingLED, ledBaseStates[index]);
        }
    }
    
    isBlinking = false;
    blinkState = false;
    currentBlinkPriority = BlinkPriority::NONE;
    
    debug("Todos los parpadeos detenidos forzosamente");
}

void LEDController::updateEspaciosLibres(uint8_t espacios) {
    if (espacios != espaciosLibres) {
        espaciosLibres = min(espacios, totalEspacios);
        debug("Espacios actualizados: " + String(espaciosLibres) + "/" + String(totalEspacios));
    }
}

void LEDController::setSimulatedTime(uint8_t hour, uint8_t minute) {
    simulatedHour = hour % 24;
    simulatedMinute = minute % 60;
    debug("Tiempo establecido: " + formatTime());
}

void LEDController::resetToDefaultTime() {
    setSimulatedTime(8, 0);
}

// Funciones auxiliares
uint8_t LEDController::getLEDPin(LEDType type) const {
    switch (type) {
        case LEDType::BLANCOS: return PIN_LEDS_BLANCOS;
        case LEDType::ROJO: return PIN_LED_ROJO_2;
        case LEDType::AZUL: return PIN_LED_AZUL;
        case LEDType::VERDE: return (PIN_LED_VERDE != -1) ? PIN_LED_VERDE : 0;
        default: return 0;
    }
}

uint8_t LEDController::getLEDIndex(LEDType type) const {
    switch (type) {
        case LEDType::BLANCOS: return 0;
        case LEDType::ROJO: return 1;
        case LEDType::AZUL: return 2;
        case LEDType::VERDE: return 3;
        default: return 255; // Valor inválido
    }
}

void LEDController::debug(const String& mensaje) {
    if (DEBUG_ENABLED) {
        Serial.print(F("[LEDs] "));
        Serial.println(mensaje);
    }
}

String LEDController::getModeString(LEDMode mode) const {
    switch (mode) {
        case LEDMode::DIURNO: return "DIURNO";
        case LEDMode::NOCTURNO: return "NOCTURNO";
        case LEDMode::EMERGENCIA: return "EMERGENCIA";
        case LEDMode::MANTENIMIENTO: return "MANTENIMIENTO";
        default: return "DESCONOCIDO";
    }
}

String LEDController::formatTime() const {
    char timeStr[6];
    snprintf(timeStr, sizeof(timeStr), "%02d:%02d", simulatedHour, simulatedMinute);
    return String(timeStr);
}

void LEDController::setBrightness(uint8_t level) {
    // Implementar si tienes LEDs con control PWM
    // analogWrite(PIN_LED_PWM, map(level, 0, 100, 0, 255));
}

void LEDController::setSchedule(uint8_t hourOn, uint8_t hourOff) {
    // Para implementar horarios personalizados
    // Guardar en variables de configuración
    debug("Horario personalizado: " + String(hourOn) + ":00 - " + String(hourOff) + ":00");
}

void LEDController::printStatus() const {
    if (!DEBUG_ENABLED) return;
    
    Serial.println(F("=== ESTADO LEDController ==="));
    Serial.println("Tiempo: " + formatTime());
    Serial.println("Modo: " + getModeString(currentMode));
    Serial.println("Espacios libres: " + String(espaciosLibres) + "/" + String(totalEspacios));
    Serial.println("Auto mode: " + String(autoModeEnabled ? "ON" : "OFF"));
    Serial.println("Emergencia: " + String(emergencyActive ? "ACTIVA" : "INACTIVA"));
    Serial.println("Parpadeo: " + String(isBlinking ? "ACTIVO" : "INACTIVO"));
    if (isBlinking) {
        Serial.println("LED parpadeando: " + String((int)blinkingLED));
        Serial.println("Prioridad: " + String((int)currentBlinkPriority));
    }
    
    Serial.print("Estados LEDs: ");
    for (int i = 0; i < 4; i++) {
        Serial.print(ledStates[i] ? "1" : "0");
        if (i < 3) Serial.print(",");
    }
    Serial.println();
    Serial.println(F("==========================="));
}

bool LEDController::selfTest() {
    debug("Iniciando auto-test...");
    
    // Test secuencial de todos los LEDs
    LEDType testLEDs[] = {LEDType::BLANCOS, LEDType::ROJO, LEDType::AZUL};
    int numLEDs = 3;
    if (PIN_LED_VERDE != -1) {
        numLEDs = 4;
    }
    
    for (int i = 0; i < numLEDs; i++) {
        LEDType led = (i < 3) ? testLEDs[i] : LEDType::VERDE;
        setLED(led, true);
        delay(200);
        setLED(led, false);
        delay(100);
    }
    
    debug("Auto-test completado");
    return true;
}
