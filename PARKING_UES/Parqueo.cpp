#include "Parqueo.h"
#include "Config.h"

void ParqueoController::init() {  
    // Inicializar sensores y estados  
    for(uint8_t i = 0; i < MAX_SENSORES; i++) {  
        pinMode(pinesSensores[i], INPUT_PULLUP);  
        lastSensorState[i] = digitalRead(pinesSensores[i]);  
        lastDebounceTime[i] = 0;  
        debug("Sensor " + String(i) + " inicializado (Estado: " + (lastSensorState[i] ? "LIBRE" : "OCUPADO") + ")");
    }  
    
    // Iniciar LCD a través del controlador
    lcdController.init();  
    debug("LCD inicializado");
    
    // Inicializar variables de tiempo
    lastUpdateTime = 0;
    tiempoExpiracionCodigo = 0;
    codigoAccesoActual = "";
    
    debug("ParqueoController inicializado correctamente");
}  

bool ParqueoController::leerSensorDebounceado(uint8_t index) {  
    if(index >= MAX_SENSORES) {
        debug("ERROR: Índice de sensor inválido: " + String(index));
        return HIGH; // Retorna estado libre por seguridad
    }
    
    bool currentState = digitalRead(pinesSensores[index]);  
    
    if (currentState != lastSensorState[index]) {  
        lastDebounceTime[index] = millis();  
        debug("Cambio detectado en sensor " + String(index) + 
              " -> Estado: " + (currentState ? "LIBRE" : "OCUPADO"));
    }  
    
    if ((millis() - lastDebounceTime[index]) > DEBOUNCE_DELAY_MS) {  
        if(lastSensorState[index] != currentState) {
            lastSensorState[index] = currentState;
            debug("Estado confirmado sensor " + String(index) + 
                  ": " + (currentState ? "LIBRE" : "OCUPADO"));
        }
        return currentState;  
    }  
    
    return lastSensorState[index];  
}  

bool ParqueoController::estaEnHorarioPermitido(uint8_t hora) {  
    bool permitido = (hora >= HORA_APERTURA && hora < HORA_CIERRE);
    debug("Horario " + String(hora) + ":00 -> " + 
          (permitido ? "PERMITIDO" : "DENEGADO"));
    return permitido;
}  

bool ParqueoController::validarHorarioAcceso(uint8_t hora) {  
    return estaEnHorarioPermitido(hora);  
}  

uint8_t ParqueoController::contarEspaciosDisponibles() {  
    uint8_t libres = 0;  
    for(uint8_t i = 0; i < MAX_SENSORES; i++) {  
        if(leerSensorDebounceado(i) == ESPACIO_LIBRE) {  
            libres++;  
        }  
    }
    debug("Espacios libres: " + String(libres) + "/" + String(MAX_SENSORES));
    return libres;  
}  

void ParqueoController::update(uint8_t hora, uint8_t minuto) {  
    if(millis() - lastUpdateTime >= ACTUALIZACION_PARQUEO_MS) {  
        lastUpdateTime = millis();  
        actualizarLCD(hora, minuto);  
        debug("Actualización LCD (" + String(hora) + ":" + 
              (minuto < 10 ? "0" : "") + String(minuto) + ")");
    }  
}  

uint8_t ParqueoController::getEspaciosDisponibles() {  
    return contarEspaciosDisponibles();  
}  

void ParqueoController::mostrarMensajeEmergencia(const String& motivo) {
    debug("EMERGENCIA: " + motivo);
    lcdController.mostrarMensajeEmergencia(motivo);
}

void ParqueoController::actualizarLCD(uint8_t hora, uint8_t minuto) {
    uint8_t espaciosLibres = contarEspaciosDisponibles();
    lcdController.mostrarEstadoParqueo(hora, minuto, espaciosLibres);
}

void ParqueoController::debug(const String& mensaje) {
    #ifdef DEBUG_ENABLED
    Serial.print(F("[Parqueo] "));
    Serial.println(mensaje);
    #endif
}

void ParqueoController::mostrarMensajeCodigoApp() {
    if(codigoAccesoActual.length() == 0) {
        debug("ERROR: No hay código de acceso generado");
        return;
    }
    
    if(millis() > tiempoExpiracionCodigo) {
        debug("ERROR: Código de acceso expirado");
        return;
    }
    
    lcdController.mostrarMensajeCodigo(codigoAccesoActual);
    debug("Mostrando código de acceso en LCD");
}

void ParqueoController::generarNuevoCodigoAcceso() {
    codigoAccesoActual = generarCodigoAcceso();
    tiempoExpiracionCodigo = millis() + TIEMPO_EXPIRACION_CODIGO_MS;
    debug("Nuevo código generado: " + codigoAccesoActual + 
          " (válido por " + String(TIEMPO_EXPIRACION_CODIGO_MS/60000) + " min)");
}

bool ParqueoController::codigoEsValido(const String& codigo) {
    if(codigo.length() != LONGITUD_CODIGO) {
        debug("Código inválido: longitud incorrecta");
        return false;
    }
    
    if(millis() > tiempoExpiracionCodigo) {
        debug("Código expirado (generado hace " + 
              String((millis() - (tiempoExpiracionCodigo - TIEMPO_EXPIRACION_CODIGO_MS))/1000) + 
              " segundos)");
        return false;
    }
    
    bool esValido = codigo == codigoAccesoActual;
    debug("Validación código: " + codigo + " -> " + 
          (esValido ? "VÁLIDO" : "INVÁLIDO"));
    return esValido;
}

String ParqueoController::generarCodigoAcceso() {
    String codigo = "";
    randomSeed(millis() + analogRead(A0)); 
    
    for(int i = 0; i < LONGITUD_CODIGO; i++) {
        codigo += String(random(0, 10)); // Dígitos 0-9
    }
    
    return codigo;
}

// Método adicional: Obtener estado detallado de sensores
String ParqueoController::getEstadoSensores() {
    String estado = "Sensores: ";
    for(uint8_t i = 0; i < MAX_SENSORES; i++) {
        estado += "[" + String(i) + ":" + 
                 (leerSensorDebounceado(i) ? "L" : "O") + "]";
    }
    return estado;
}

// Método adicional: Verificar si hay espacios críticos
bool ParqueoController::espaciosCriticos() {
    uint8_t libres = contarEspaciosDisponibles();
    return libres <= ESPACIOS_CRITICOS_THRESHOLD;
}

// Método adicional: Obtener tiempo restante del código
uint32_t ParqueoController::getTiempoRestanteCodigo() {
    if(millis() > tiempoExpiracionCodigo) {
        return 0;
    }
    return tiempoExpiracionCodigo - millis();
}
