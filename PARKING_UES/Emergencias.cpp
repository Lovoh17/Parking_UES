#include "Emergencias.h"
#include "Config.h"


Emergencias::Emergencias(uint8_t pinDHT, uint8_t tipoDHT) : dht(pinDHT, tipoDHT) {
    _alarma_activa = false;
    _emergencia_manual = false;
    _lecturaGas = 0;
    _temperatura = 0.0f;
    _lastAlarmaUpdate = 0;
    _alarmaLedState = false;
    _lastDebounceTime = 0;
}

void Emergencias::debug(const String& mensaje) {
    Serial.print(F("[EMERGENCIAS] "));
    Serial.println(mensaje);
}

void Emergencias::init() {
    // Inicialización de pines
    pinMode(PIN_GAS, INPUT);
    pinMode(PIN_LED_ROJO_1, OUTPUT);
    pinMode(PIN_LED_ROJO_2, OUTPUT);
    pinMode(PIN_BUZZER, OUTPUT);
    pinMode(PIN_VENTILADOR, OUTPUT);
    pinMode(PIN_BOTON_EMERGENCIA, INPUT_PULLUP);
    
    // Estado inicial
    digitalWrite(PIN_VENTILADOR, LOW);
    digitalWrite(PIN_LED_ROJO_1, LOW);
    digitalWrite(PIN_LED_ROJO_2, LOW);
    noTone(PIN_BUZZER);
    
    // Inicializar sensor DHT
    dht.begin();
    
    debug("Sistema de emergencias inicializado");
}

void Emergencias::verificarBotonEmergencia() {
    bool currentState = digitalRead(PIN_BOTON_EMERGENCIA);
    
    // Detección de cambio de estado
    if (currentState != _lastButtonState) {
        _lastButtonChangeTime = millis();
    }
    
    // Verificar estado estable después del debounce
    if ((millis() - _lastButtonChangeTime) > TIEMPO_DEBOUNCE) {
        if (currentState != _buttonPressed) {
            _buttonPressed = currentState;
            
            // Solo actuar en flanco descendente (botón presionado)
            if (_buttonPressed == LOW) {
                _emergencia_manual = !_emergencia_manual;
                _alarma_activa = _emergencia_manual;
                
                if (_emergencia_manual) {
                    //registrarEvento("EMERGENCIA MANUAL ACTIVADA");
                    debug("¡EMERGENCIA MANUAL ACTIVADA POR BOTÓN!");
                } else {
                    //registrarEvento("Emergencia manual DESACTIVADA");
                    debug("Emergencia manual DESACTIVADA");
                    // Limpiar estado de alarma
                    digitalWrite(PIN_LED_ROJO_1, LOW);
                    digitalWrite(PIN_LED_ROJO_2, LOW);
                    noTone(PIN_BUZZER);
                }
            }
        }
    }
    _lastButtonState = currentState;
}

void Emergencias::verificar() {
    // Primero verificar botón de emergencia
    verificarBotonEmergencia();
    
    // Si es emergencia manual, ignorar sensores
    if (_emergencia_manual) {
        return;
    }

    // Lectura de sensores con protección
    _lecturaGas = constrain(analogRead(PIN_GAS), 0, 1023);
    _temperatura = leerTemperatura();
    
    // Debug de lecturas cada 5 segundos para no saturar
    static unsigned long lastDebugTime = 0;
    if (millis() - lastDebugTime > 5000) {
        debug("Lecturas - Gas: " + String(_lecturaGas) + " | Temperatura: " + String(_temperatura, 1) + "°C");
        lastDebugTime = millis();
    }
    
    // Detección de condiciones peligrosas
    bool gasPeligroso = _lecturaGas > UMBRAL_GAS_PELIGROSO;
    bool tempAlta = _temperatura > UMBRAL_TEMPERATURA_ALTA;
    
    // Control de ventilación (no activar si hay gas)
    controlarVentilacion(tempAlta && !gasPeligroso);
    
    // Gestión de estado de alarma
    if (gasPeligroso || tempAlta) {
        if (!_alarma_activa) {
            debug("¡EMERGENCIA DETECTADA!");
            if (gasPeligroso) {
                debug("ALERTA: Nivel de gas peligroso (" + String(_lecturaGas) + " > " + String(UMBRAL_GAS_PELIGROSO) + ")");
            }
            if (tempAlta) {
                debug("ALERTA: Temperatura crítica (" + String(_temperatura, 1) + "°C > " + String(UMBRAL_TEMPERATURA_ALTA) + "°C)");
            }
        }
        _alarma_activa = true;
    } else {
        if (_alarma_activa) {
            debug("Condiciones normalizadas - Desactivando alarma");
            // Limpiar estado de alarma
            digitalWrite(PIN_LED_ROJO_1, LOW);
            digitalWrite(PIN_LED_ROJO_2, LOW);
            noTone(PIN_BUZZER);
        }
        _alarma_activa = false;
    }
}

void Emergencias::actualizar() {
    if (_alarma_activa) {
        activarAlarmaNoBloqueante();
    }
}

void Emergencias::controlarVentilacion(bool activar) {
    // No controlar ventilación si es emergencia manual
    if (!_emergencia_manual) {
        static bool estadoAnterior = false;
        if (estadoAnterior != activar) {
            digitalWrite(PIN_VENTILADOR, activar ? HIGH : LOW);
            debug(activar ? "Ventilación ACTIVADA" : "Ventilación DESACTIVADA");
            estadoAnterior = activar;
        }
    }
}

bool Emergencias::activa() const {
    return _alarma_activa;
}

const char* Emergencias::getMotivo() const {
    if (_emergencia_manual) {
        return "Emergencia manual activada";
    }
    if (_lecturaGas > UMBRAL_GAS_PELIGROSO && _temperatura > UMBRAL_TEMPERATURA_ALTA) {
        return "Gas y temperatura peligrosos";
    } else if (_lecturaGas > UMBRAL_GAS_PELIGROSO) {
        return "Fuga de gas detectada";
    } else if (_temperatura > UMBRAL_TEMPERATURA_ALTA) {
        return "Temperatura crítica";
    } else {
        return "Sistema normal";
    }
}

float Emergencias::getTemperatura() const {
    return _temperatura;
}

int Emergencias::getNivelGas() const {
    return _lecturaGas;
}

float Emergencias::leerTemperatura() {
    float t = dht.readTemperature();
    
    if (isnan(t)) {  
        debug("Error leyendo temperatura del DHT11");
        return constrain(_temperatura, -20, 60); // Mantener último valor válido
    }
    
    if (t < -20 || t > 60) {
        debug("Temperatura fuera de rango: " + String(t, 1) + "°C");
        return constrain(_temperatura, -20, 60);
    }
    
    return t;
}

void Emergencias::activarAlarmaNoBloqueante() {
    // Actualización cada 200ms para parpadeo
    if (millis() - _lastAlarmaUpdate > 200) {
        _alarmaLedState = !_alarmaLedState;
        
        digitalWrite(PIN_LED_ROJO_1, _alarmaLedState);
        digitalWrite(PIN_LED_ROJO_2, _alarmaLedState);
        
        if (_alarmaLedState) {
            tone(PIN_BUZZER, 1000, 100); // Beep corto
        }
        
        // CORRECCIÓN 8: Debug menos frecuente
        static unsigned long lastStatusDebug = 0;
        if (millis() - lastStatusDebug > 5000) {
            debug("Alarma activa - Motivo: " + String(getMotivo()));
            lastStatusDebug = millis();
        }
        
        _lastAlarmaUpdate = millis();
    }
    
    // Apagar ventilador si hay gas (excepto en emergencia manual)
    if (!_emergencia_manual && _lecturaGas > UMBRAL_GAS_PELIGROSO) {
        controlarVentilacion(false);
    }
}
