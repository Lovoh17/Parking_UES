#pragma once
#include <Arduino.h>

class SerialComm {
private:
    const unsigned long BAUD_RATE = 9600;
    String inputBuffer;
    bool messageReady = false;

public:
    void init() {
        Serial.begin(BAUD_RATE);
        while(!Serial); // Esperar conexión en algunas placas
        inputBuffer.reserve(256); // Reservar memoria para el buffer
    }

    void update() {
        while(Serial.available() > 0 && !messageReady) {
            char inChar = (char)Serial.read();
            if(inChar == '\n') {
                messageReady = true;
            } else {
                inputBuffer += inChar;
            }
        }
    }

    bool messageAvailable() {
        return messageReady;
    }

    String getMessage() {
        messageReady = false;
        String message = inputBuffer;
        inputBuffer = "";
        return message;
    }

    void send(const String& message) {
        Serial.println(message);
    }

    void debug(const String& message) {
        Serial.print("[DEBUG] ");
        Serial.println(message);
    }
};
