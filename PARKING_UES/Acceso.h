#pragma once
#include <Arduino.h>
#include <Servo.h>
#include "Parqueo.h"
#include "Config.h"

class Acceso {
public:
    enum EstadoPuerta { CERRADA, ABIERTA };
    
    Acceso(ParqueoController& parqueoRef);
    void init();
    void actualizar();
    bool detectarVehiculo(uint8_t trigPin, uint8_t echoPin);
    bool detectarCola(uint8_t trigPin, uint8_t echoPin);
    bool hayEspacioDisponible();
    void abrirPuerta(Servo& servo);
    void cerrarPuerta(Servo& servo);
    void cerrarTodasLasPuertas();
    void abrirTodasLasPuertas();
    bool validarCodigoAcceso(const String& codigo);
    void debug(const String& mensaje);

private:
    ParqueoController& parqueo;
    Servo servoEntrada;
    Servo servoSalida;
    
    // Variables de estado
    unsigned long _ultimaDeteccionEntrada;
    unsigned long _ultimaDeteccionSalida;
    EstadoPuerta _estadoPuertaEntrada;
    EstadoPuerta _estadoPuertaSalida;
    unsigned long _tiempoAperturaEntrada;
    unsigned long _tiempoAperturaSalida;
    
    void manejarEntrada();
    void manejarSalida();
};
