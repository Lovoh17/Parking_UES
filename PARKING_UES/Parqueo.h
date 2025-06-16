#pragma once  
#include <Arduino.h>   
#include "Config.h"  
#include "LCDController.h"

// Constantes del sistema
#define MAX_SENSORES 6
#define LONGITUD_CODIGO 4
#define DEBOUNCE_DELAY_MS 50
#define TIEMPO_EXPIRACION_CODIGO_MS 300000  // 5 minutos
#define ESPACIO_LIBRE HIGH
#define ESPACIO_OCUPADO LOW

class ParqueoController {  
private:  
    // Configuración de pines
    const uint8_t pinesSensores[MAX_SENSORES] = {PIN_SENSOR_1, PIN_SENSOR_2, PIN_SENSOR_3,PIN_SENSOR_4, PIN_SENSOR_5, PIN_SENSOR_6};  
    
    // Variables para debounce  
    uint32_t lastDebounceTime[MAX_SENSORES] = {0};  
    bool lastSensorState[MAX_SENSORES] = {ESPACIO_LIBRE};  
    
    // Controladores
    LCDController lcdController;
    
    // Control de tiempo
    unsigned long lastUpdateTime = 0;  
    
    // Sistema de códigos de acceso
    String codigoAccesoActual;
    unsigned long tiempoExpiracionCodigo = 0;
    
    // Métodos privados
    uint8_t contarEspaciosDisponibles();  
    bool leerSensorDebounceado(uint8_t index);  
    bool estaEnHorarioPermitido(uint8_t hora);  
    void debug(const String& mensaje);    
    String generarCodigoAcceso();
    
public:  
    // Constructor
    ParqueoController() : lcdController(PIN_LCD_RS,PIN_LCD_EN, PIN_LCD_D4, PIN_LCD_D5,PIN_LCD_D6, PIN_LCD_D7) {}  
    
    // Métodos de inicialización y control principal
    void init();  
    void update(uint8_t hora, uint8_t minuto);  
    
    // Métodos de información
    uint8_t getEspaciosDisponibles();  
    bool validarHorarioAcceso(uint8_t hora);
    String getEstadoSensores();
    bool espaciosCriticos();
    
    // Métodos de visualización
    void actualizarLCD(uint8_t hora, uint8_t minuto);  
    void mostrarMensajeEmergencia(const String& motivo);
    void mostrarMensajeCodigoApp();
    
    // Métodos para código de acceso
    void generarNuevoCodigoAcceso();
    bool codigoEsValido(const String& codigo);
    uint32_t getTiempoRestanteCodigo();
};
