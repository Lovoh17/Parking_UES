#pragma once
#include <Arduino.h>
#include <DHT.h>
#include "Config.h"

class Emergencias {
public:
    Emergencias(uint8_t pinDHT, uint8_t tipoDHT);
    
    void init();
    void verificar();
    void actualizar();  
    void verificarBotonEmergencia(); 
    
    bool activa() const;
    const char* getMotivo() const;  
    float getTemperatura() const;
    int getNivelGas() const;

private:
    DHT dht;
    
    bool _alarma_activa = false;
    bool _emergencia_manual = false; 
    int _lecturaGas = 0;           
    float _temperatura = 0.0f;
    unsigned long _lastAlarmaUpdate = 0;
    bool _alarmaLedState = false;
    unsigned long _lastDebounceTime = 0; // Para anti-rebote

    float leerTemperatura();
    void activarAlarmaNoBloqueante();
    void controlarVentilacion(bool activar); 
    void debug(const String& mensaje);    
    unsigned long _lastButtonChangeTime = 0;  // Tiempo del último cambio de estado
    bool _lastButtonState = HIGH;            // Estado anterior del botón (pull-up)
    bool _buttonPressed = false;    
};
