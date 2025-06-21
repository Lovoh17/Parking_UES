#pragma once
#include <Arduino.h>
#include <Servo.h>
#include <SPI.h>
#include <MFRC522.h>
#include "Config.h"

class AccesoRFID {
public:
    void init();
    void verificarAcceso();
    void manejarSalida();  
    bool adminAutenticado() const;
    void cerrarPuertaEntrada();
    void cerrarPuertaSalida();
    void abrirPuertaEntrada();
    void abrirPuertaSalida();
    
private:
    MFRC522 rfid{PIN_RFID_SS, PIN_RFID_RST};
    Servo servoEntrada;
    Servo servoSalida;
    bool _adminAutenticado = false;
    uint8_t intentosFallidos = 0;
    unsigned long tiempoBloqueo = 0;
    bool compararUID(byte* uid, byte* uidAdmin);
    void debug(const String& mensaje);
};
