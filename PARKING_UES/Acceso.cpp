#include "Acceso.h"
#include "Parqueo.h"
#include "Config.h"

Acceso::Acceso(ParqueoController& parqueoRef) : parqueo(parqueoRef) {
    _ultimaDeteccionEntrada = 0;
    _ultimaDeteccionSalida = 0;
    _estadoPuertaEntrada = CERRADA;
    _estadoPuertaSalida = CERRADA;
    _tiempoAperturaEntrada = 0;
    _tiempoAperturaSalida = 0;
}

void Acceso::debug(const String& mensaje) {
    Serial.print(F("[ACCESO] "));
    Serial.println(mensaje);
}

void Acceso::init() {
    pinMode(PIN_TRIG_ENTRADA, OUTPUT);
    pinMode(PIN_ECHO_ENTRADA, INPUT);
    pinMode(PIN_TRIG_SALIDA, OUTPUT);
    pinMode(PIN_ECHO_SALIDA, INPUT);
    
    servoEntrada.attach(PIN_SERVO_ENTRADA);
    servoSalida.attach(PIN_SERVO_SALIDA);
    
    cerrarPuerta(servoEntrada);
    cerrarPuerta(servoSalida);
    
    debug("Módulo de Acceso inicializado");
}

void Acceso::actualizar() {
    unsigned long ahora = millis();
    
    // Corrección 1: Verificar estado antes de cerrar automáticamente
    if (_estadoPuertaEntrada == ABIERTA && (ahora - _tiempoAperturaEntrada) > TIEMPO_APERTURA) {
        cerrarPuerta(servoEntrada);
        debug("Cerrando puerta entrada por timeout");
    }
    
    if (_estadoPuertaSalida == ABIERTA && (ahora - _tiempoAperturaSalida) > TIEMPO_APERTURA) {
        cerrarPuerta(servoSalida);
        debug("Cerrando puerta salida por timeout");
    }
    
    // Corrección 2: Mejorar la lógica de detección
    if (ahora - _ultimaDeteccionEntrada > TIEMPO_DEBOUNCE) {
        if (detectarVehiculo(PIN_TRIG_ENTRADA, PIN_ECHO_ENTRADA)) {
            _ultimaDeteccionEntrada = ahora;
            manejarEntrada();
        }
    }
    
    if (ahora - _ultimaDeteccionSalida > TIEMPO_DEBOUNCE) {
        if (detectarVehiculo(PIN_TRIG_SALIDA, PIN_ECHO_SALIDA)) {
            _ultimaDeteccionSalida = ahora;
            manejarSalida();
        }
    }
}

void Acceso::manejarEntrada() {
    if (_estadoPuertaEntrada == ABIERTA) {
        debug("Puerta entrada ya está abierta - Ignorando detección");
        return;
    }
    
    if (hayEspacioDisponible()) {
        parqueo.mostrarMensajeCodigoApp();
        debug("Mostrando código de acceso en LCD");
        
        unsigned long tiempoEspera = millis();
        bool codigoIngresado = false;
        
        // Corrección 3: Lógica más realista para validación de código
        while ((millis() - tiempoEspera) < TIEMPO_ESPERA_CODIGO && !codigoIngresado) {
            // Aquí deberías implementar la lógica real de recepción del código
            // Por ahora, simulo que se recibe después de 2 segundos
            if ((millis() - tiempoEspera) > 2000) {
                // En implementación real, aquí validarías el código recibido
                // codigoIngresado = validarCodigoAcceso(codigoRecibido);
                codigoIngresado = true; // Simulación temporal
            }
            
            // Corrección 4: Verificar cola correctamente
            if (detectarCola(PIN_TRIG_ENTRADA, PIN_ECHO_ENTRADA)) {
                debug("Cola detectada en entrada - Manteniendo espera");
                // No cancelar, sino continuar esperando
            }
            
            delay(100);
        }
        
        if (codigoIngresado) {
            abrirPuerta(servoEntrada);
            debug("Acceso concedido - Puerta abierta");
        } else {
            debug("Tiempo agotado - Código no recibido");
            parqueo.mostrarMensajeEmergencia("Codigo requerido");
        }
    } else {
        debug("No hay espacios disponibles");
        parqueo.mostrarMensajeEmergencia("Parqueo lleno");
    }
}

void Acceso::manejarSalida() {
    if (_estadoPuertaSalida == ABIERTA) {
        debug("Puerta salida ya está abierta - Ignorando detección");
        return;
    }
    
    // Corrección 5: Lógica más sensata para salidas
    // No debería importar si hay cola en entrada para permitir salidas
    debug("Vehículo detectado en salida - Abriendo puerta");
    abrirPuerta(servoSalida);
    
    // Opcional: Decrementar contador de vehículos
    // parqueo.decrementarVehiculos();
}

bool Acceso::detectarVehiculo(uint8_t trigPin, uint8_t echoPin) {
    digitalWrite(trigPin, LOW);
    delayMicroseconds(2);
    digitalWrite(trigPin, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigPin, LOW);
    
    long duracion = pulseIn(echoPin, HIGH, 30000); // Corrección 6: Timeout para pulseIn
    
    if (duracion == 0) {
        debug("Timeout en sensor ultrasónico");
        return false;
    }
    
    float distancia = duracion * 0.034 / 2;
    
    String sensor = (trigPin == PIN_TRIG_ENTRADA) ? "ENTRADA" : "SALIDA";
    debug("Sensor " + sensor + " - Distancia: " + String(distancia, 1) + " cm");
    
    return (distancia > 0 && distancia < DISTANCIA_DETECCION);
}

bool Acceso::detectarCola(uint8_t trigPin, uint8_t echoPin) {
    // Corrección 7: Implementación más eficiente
    digitalWrite(trigPin, LOW);
    delayMicroseconds(2);
    digitalWrite(trigPin, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigPin, LOW);
    
    long duracion = pulseIn(echoPin, HIGH, 30000);
    
    if (duracion == 0) {
        return false; // No hay lectura válida
    }
    
    float distancia = duracion * 0.034 / 2;
    
    bool hayCola = (distancia >= DISTANCIA_COLA_MIN && distancia <= DISTANCIA_COLA_MAX);
    
    if (hayCola) {
        String sensor = (trigPin == PIN_TRIG_ENTRADA) ? "ENTRADA" : "SALIDA";
        debug("Cola detectada en " + sensor + ": " + String(distancia, 1) + " cm");
    }
    
    return hayCola;
}

bool Acceso::hayEspacioDisponible() {
    uint8_t espacios = parqueo.getEspaciosDisponibles();
    debug("Espacios disponibles: " + String(espacios));
    return espacios > 0;
}

void Acceso::abrirPuerta(Servo& servo) {
    if (&servo == &servoEntrada) {
        servo.write(PUERTA_ABIERTA);
        _estadoPuertaEntrada = ABIERTA;
        _tiempoAperturaEntrada = millis();
        debug("Puerta ENTRADA abierta");
    } else if (&servo == &servoSalida) {
        servo.write(PUERTA_ABIERTA);
        _estadoPuertaSalida = ABIERTA;
        _tiempoAperturaSalida = millis();
        debug("Puerta SALIDA abierta");
    }
}

void Acceso::cerrarPuerta(Servo& servo) {
    if (&servo == &servoEntrada) {
        servo.write(PUERTA_CERRADA);
        _estadoPuertaEntrada = CERRADA;
        debug("Puerta ENTRADA cerrada");
    } else if (&servo == &servoSalida) { 
        servo.write(PUERTA_CERRADA);
        _estadoPuertaSalida = CERRADA;
        debug("Puerta SALIDA cerrada");
    }
}

void Acceso::cerrarTodasLasPuertas() {
    cerrarPuerta(servoEntrada);
    cerrarPuerta(servoSalida);
    debug("Todas las puertas cerradas");
}

void Acceso::abrirTodasLasPuertas() {
    abrirPuerta(servoEntrada);
    abrirPuerta(servoSalida);
    debug("Todas las puertas abiertas por emergencia");
}

bool Acceso::validarCodigoAcceso(const String& codigo) {
    bool valido = parqueo.codigoEsValido(codigo);
    debug("Validación código " + codigo + ": " + (valido ? "VÁLIDO" : "INVÁLIDO"));
    return valido;
}
