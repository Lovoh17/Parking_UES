#include "LCDController.h"

LCDController::LCDController(uint8_t rs, uint8_t en, uint8_t d4, uint8_t d5, uint8_t d6, uint8_t d7) :
    lcd(rs, en, d4, d5, d6, d7), 
    lastUpdateTime(0),
    currentMessage(""),
    isEmergencyActive(false) {}

void LCDController::init() {
    lcd.begin(16, 2);
    lcd.clear();
    
    centerText("SISTEMA", 0);
    centerText("PARQUEO v1.0", 1);
    delay(2000);
    
    centerText("Iniciando...", 0);
    lcd.setCursor(0, 1);
    lcd.print(""); // Línea vacía
    delay(1000);
    
    clear();
    lastUpdateTime = millis();
}

void LCDController::mostrarEstadoSistema() {
    if (currentMessage != "sistema_activo") {
        lcd.clear();
        centerText("SISTEMA ACTIVO", 0);
        centerText("Listo para usar", 1);
        currentMessage = "sistema_activo";
        lastUpdateTime = millis();
    }
}

void LCDController::mostrarEstadoParqueo(uint8_t hora, uint8_t minuto, uint8_t espaciosDisponibles) {
    char timeBuffer[6];
    formatTime(hora, minuto, timeBuffer);
    
    String newMessage = String(timeBuffer) + "_" + String(espaciosDisponibles);
    
    // Solo actualizar si cambió la información
    if (currentMessage != newMessage) {
        lcd.clear();
        
        // Primera línea: Hora centrada
        lcd.setCursor(5, 0); // Centrar "Hora: XX:XX"
        lcd.print("Hora: ");
        lcd.print(timeBuffer);
        
        // Segunda línea: Espacios disponibles
        lcd.setCursor(0, 1);
        lcd.print("Espacios: ");
        lcd.print(espaciosDisponibles);
        lcd.print("/");
        lcd.print(TOTAL_ESPACIOS); // Usar constante del Config.h
        
        // Indicador visual del estado
        if (espaciosDisponibles == 0) {
            lcd.setCursor(15, 1);
            lcd.print("!");
        } else if (espaciosDisponibles <= 2) {
            lcd.setCursor(15, 1);
            lcd.print("*");
        }
        
        currentMessage = newMessage;
        lastUpdateTime = millis();
    }
}

void LCDController::mostrarMensajeEmergencia(const String& motivo) {
    isEmergencyActive = true;
    lcd.clear();
    
    // Centrar mensaje de emergencia
    centerText("!EMERGENCIA!", 0);
    
    // Mostrar motivo truncado si es necesario
    String motivoTruncado = motivo;
    if (motivo.length() > 16) {
        motivoTruncado = motivo.substring(0, 13) + "...";
    }
    centerText(motivoTruncado, 1);
    
    currentMessage = "emergencia_" + motivo;
    lastUpdateTime = millis();
}

void LCDController::mostrarMensajeCodigo(const String& codigo) {
    lcd.clear();
    centerText("CODIGO DE ACCESO", 0);
    
    lcd.setCursor(0, 1);
    lcd.print("App: ");
    
    // Centrar el código en la segunda línea
    String codigoDisplay = codigo;
    if (codigo.length() > 11) { // 16 - 5 ("App: ") = 11
        codigoDisplay = codigo.substring(0, 11);
    }
    
    uint8_t padding = (16 - 5 - codigoDisplay.length()) / 2;
    lcd.setCursor(5 + padding, 1);
    lcd.print(codigoDisplay);
    
    currentMessage = "codigo_" + codigo;
    lastUpdateTime = millis();
}

void LCDController::mostrarMensajePersonalizado(const String& linea1, const String& linea2) {
    lcd.clear();
    
    // Truncar líneas si son muy largas
    String l1 = linea1.length() > 16 ? linea1.substring(0, 16) : linea1;
    String l2 = linea2.length() > 16 ? linea2.substring(0, 16) : linea2;
    
    centerText(l1, 0);
    if (l2.length() > 0) {
        centerText(l2, 1);
    }
    
    currentMessage = "custom_" + linea1 + "_" + linea2;
    lastUpdateTime = millis();
}

void LCDController::clear() {
    lcd.clear();
    currentMessage = "";
    isEmergencyActive = false;
    lastUpdateTime = millis();
}

void LCDController::blinkEmergency() {
    if (isEmergencyActive) {
        static unsigned long lastBlink = 0;
        static bool displayOn = true;
        
        if (millis() - lastBlink >= 500) { // Parpadeo cada 500ms
            if (displayOn) {
                lcd.noDisplay();
            } else {
                lcd.display();
            }
            displayOn = !displayOn;
            lastBlink = millis();
        }
    }
}

bool LCDController::isDisplaying(const String& message) {
    return currentMessage.indexOf(message) != -1;
}

// Funciones auxiliares privadas
void LCDController::centerText(const String& text, uint8_t row) {
    uint8_t padding = (16 - text.length()) / 2;
    lcd.setCursor(padding, row);
    lcd.print(text);
}

void LCDController::formatTime(uint8_t hora, uint8_t minuto, char* buffer) {
    sprintf(buffer, "%02d:%02d", hora, minuto);
}

void LCDController::setBrightness(uint8_t level) {
    // Implementar si tienes control de brillo por PWM
    // analogWrite(BACKLIGHT_PIN, level);
}
