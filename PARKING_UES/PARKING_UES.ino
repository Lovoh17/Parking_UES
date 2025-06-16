#include "Config.h"
#include "Emergencias.h"
#include "LEDs.h"
#include "Parqueo.h" 
#include "AccesoRFID.h"
#include "Acceso.h"  

// Instancias globales
Emergencias emergencias(PIN_DHT, DHT_TYPE); 
LEDController leds;
ParqueoController parqueo; 
AccesoRFID accesoRFID;
Acceso acceso(parqueo);  

// Variables de estado
bool sistemaActivo = true;
unsigned long ultimaActualizacionSerial = 0;

void setup() {
    Serial.begin(9600);
    while(!Serial); 
    
    // Inicialización de módulos
    emergencias.init();
    leds.init();
    parqueo.init(); 
    accesoRFID.init();
    acceso.init();
    
    // Mensaje inicial en Serial
    Serial.println(F("\n╔══════════════════════════╗"));
    Serial.println(F("║   SISTEMA DE PARQUEO UES ║"));
    Serial.println(F("║      Versión 1.0        ║"));
    Serial.println(F("╚══════════════════════════╝"));
    Serial.println(F("Inicialización completa"));

    // Mensaje inicial en LCD (a través de ParqueoController)
    parqueo.actualizarLCD(0, 0); 
}

void loop() {
    // Actualización de módulos
    actualizarModulos();
    
    // Lógica de emergencia (máxima prioridad)
    if (emergencias.activa()) {
        manejarEmergencia();
        return;
    }

    // Lógica normal de operación
    if (sistemaActivo) {
        accesoRFID.verificarAcceso();
        accesoRFID.manejarSalida();  
        mostrarEstadoPeriodico();
    }

    delay(100);
}

// ------ Funciones actualizadas ------
void actualizarModulos() {
    emergencias.verificar();
    leds.update();
    parqueo.update(leds.getHour(), leds.getMinute()); 
    accesoRFID.verificarAcceso();
    acceso.actualizar();
}

void manejarEmergencia() {
    // Abrir todas las puertas para evacuación
    acceso.abrirTodasLasPuertas();  
    accesoRFID.abrirPuertaEntrada();
    accesoRFID.abrirPuertaSalida();

    // Activar alarmas 
    leds.toggleLights(true);
    tone(PIN_BUZZER, 1000);  

    if (millis() - ultimaActualizacionSerial >= 500) {
        // Mensaje de emergencia en Serial
        Serial.println(F("¡EMERGENCIA ACTIVADA!"));
        Serial.println(F("Puertas abiertas para evacuación"));
        Serial.print(F("Motivo: "));
        Serial.println(emergencias.getMotivo());

        // Mensaje de emergencia en LCD (a través de ParqueoController)
        parqueo.mostrarMensajeEmergencia(emergencias.getMotivo());
        
        ultimaActualizacionSerial = millis();
    }
}

void mostrarEstadoPeriodico() {
    if (millis() - ultimaActualizacionSerial >= 2000) {
        // Actualizar Serial
        Serial.print(F("[Estado] Hora: "));
        Serial.print(leds.getHour());
        Serial.print(F(":"));
        Serial.print(leds.getMinute());
        Serial.print(F(" | Espacios: "));
        Serial.print(parqueo.getEspaciosDisponibles());
        Serial.print(F("/6 | Modo: "));
        Serial.println(leds.isNight() ? "Nocturno" : "Diurno");
        
        // LCD se actualiza automáticamente en parqueo.update()
        ultimaActualizacionSerial = millis();
    }
}
