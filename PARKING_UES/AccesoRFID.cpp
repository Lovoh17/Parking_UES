#include "AccesoRFID.h"
#include "LEDs.h"

extern LEDController leds;

void AccesoRFID::debug(const String& mensaje) {
    Serial.print(F("[RFID] "));
    Serial.println(mensaje);
}

void AccesoRFID::init() {
    SPI.begin();
    rfid.PCD_Init();
    servoEntrada.attach(PIN_SERVO_ENTRADA);
    servoSalida.attach(PIN_SERVO_SALIDA);
    cerrarPuertaEntrada();
    cerrarPuertaSalida();
    debug("RFID y servos inicializados");
}

void AccesoRFID::verificarAcceso() {
    if (millis() < tiempoBloqueo) {
        debug("Sistema bloqueado. Tiempo restante: " + String((tiempoBloqueo - millis()) / 1000) + "s");
        return;
    }
    
    if (!rfid.PICC_IsNewCardPresent() || !rfid.PICC_ReadCardSerial()) return;
    
    // Debug del UID leído
    String uidStr = "";
    for (byte i = 0; i < rfid.uid.size; i++) {
        uidStr += String(rfid.uid.uidByte[i], HEX);
        if (i < rfid.uid.size - 1) uidStr += ":";
    }
    debug("Tarjeta detectada - UID: " + uidStr);
    
    _adminAutenticado = compararUID(rfid.uid.uidByte, (byte*)UID_ADMIN);
    
    if (_adminAutenticado) {
        debug("Acceso ADMIN concedido - UID autorizado");
        abrirPuertaEntrada();
        //registrarAcceso(rfid.uid.uidByte, true);
    } else {
        uint8_t horaActual = leds.getHour();
        debug("Verificando horario - Hora actual: " + String(horaActual) + ":00");
        
        if (horaActual >= 7 && horaActual < 19) {
            debug("Acceso concedido - Dentro del horario permitido (7:00-19:00)");
            abrirPuertaEntrada();
            //registrarAcceso(rfid.uid.uidByte, true);
        } else {
            debug("Acceso denegado - Fuera de horario permitido");
            debug("Intentos fallidos: " + String(intentosFallidos + 1));
            intentosFallidos++;
            
            if (intentosFallidos >= 3) {
                tiempoBloqueo = millis() + 30000;  // Bloqueo por 30s
                debug("¡SISTEMA BLOQUEADO! - Demasiados intentos fallidos (30 segundos)");
            }
        }
    }
    
    if (_adminAutenticado || (leds.getHour() >= 7 && leds.getHour() < 19)) {
        debug("Esperando paso del vehículo (3 segundos)");
        delay(3000);
        cerrarPuertaEntrada();
        intentosFallidos = 0;
    }
    
    rfid.PICC_HaltA();
}

void AccesoRFID::manejarSalida() {
    if (!rfid.PICC_IsNewCardPresent() || !rfid.PICC_ReadCardSerial()) return;
    
    // Debug del UID en salida
    String uidStr = "";
    for (byte i = 0; i < rfid.uid.size; i++) {
        uidStr += String(rfid.uid.uidByte[i], HEX);
        if (i < rfid.uid.size - 1) uidStr += ":";
    }
    debug("Tarjeta detectada en SALIDA - UID: " + uidStr);
    
    abrirPuertaSalida();
    //registrarAcceso(rfid.uid.uidByte, false);------con py
    debug("Salida registrada - Puerta abierta");
    
    debug("Esperando paso del vehículo (3 segundos)");
    delay(3000);
    cerrarPuertaSalida();
    rfid.PICC_HaltA();
}

bool AccesoRFID::compararUID(byte* uid, byte* uidAdmin) {
    for (byte i = 0; i < 4; i++) {
        if (uid[i] != uidAdmin[i]) {
            debug("UID no coincide con ADMIN en posición " + String(i));
            return false;
        }
    }
    debug("UID coincide con ADMIN - Autenticación exitosa");
    return true;
}

// Métodos de servos con debug
void AccesoRFID::abrirPuertaEntrada() { 
    servoEntrada.write(90); 
    debug("Puerta ENTRADA abierta (90°)");
}

void AccesoRFID::cerrarPuertaEntrada() { 
    servoEntrada.write(0); 
    debug("Puerta ENTRADA cerrada (0°)");
}

void AccesoRFID::abrirPuertaSalida() { 
    servoSalida.write(90); 
    debug("Puerta SALIDA abierta (90°)");
}

void AccesoRFID::cerrarPuertaSalida() { 
    servoSalida.write(0); 
    debug("Puerta SALIDA cerrada (0°)");
}

bool AccesoRFID::adminAutenticado() const { 
    return _adminAutenticado; 
}
