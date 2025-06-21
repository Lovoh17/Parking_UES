#pragma once
#include <Arduino.h>
#include <LiquidCrystal.h>
#include "Config.h"

class LCDController {
private:
    LiquidCrystal lcd;
    unsigned long lastUpdateTime;
    String currentMessage;
    bool isEmergencyActive;
    
    void centerText(const String& text, uint8_t row);
    void formatTime(uint8_t hora, uint8_t minuto, char* buffer);
    
public:
    LCDController(uint8_t rs, uint8_t en, uint8_t d4, uint8_t d5, uint8_t d6, uint8_t d7);
    void init();
    void mostrarEstadoSistema();
    void mostrarEstadoParqueo(uint8_t hora, uint8_t minuto, uint8_t espaciosDisponibles);
    void mostrarMensajeEmergencia(const String& motivo);
    void mostrarMensajeCodigo(const String& codigo);
    void mostrarMensajePersonalizado(const String& linea1, const String& linea2 = "");
    void clear();
    void setBrightness(uint8_t level); // Si tienes control de brillo
    void blinkEmergency(); // Para parpadeo en emergencias
    bool isDisplaying(const String& message);
};
