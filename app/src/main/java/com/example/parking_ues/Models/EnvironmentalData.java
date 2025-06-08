package com.example.parking_ues.Models;

import java.util.Date;

public class EnvironmentalData {
    private String readingId;
    private double temperature;
    private double gasLevel;
    private double humidity;
    private Date timestamp;
    private String location; // "standard", "vip"
    private EnvironmentalAlerts alerts;

    // Constructor vacío
    public EnvironmentalData() {}

    public EnvironmentalData(String readingId, double temperature, double gasLevel, double humidity, String location) {
        this.readingId = readingId;
        this.temperature = temperature;
        this.gasLevel = gasLevel;
        this.humidity = humidity;
        this.location = location;
        this.timestamp = new Date();
        this.alerts = new EnvironmentalAlerts();
    }

    // Clase interna para alertas
    public static class EnvironmentalAlerts {
        private boolean highTemperature;
        private boolean dangerousGas;
        private boolean ventilationNeeded;

        public EnvironmentalAlerts() {
            this.highTemperature = false;
            this.dangerousGas = false;
            this.ventilationNeeded = false;
        }

        // Getters y Setters
        public boolean isHighTemperature() { return highTemperature; }
        public void setHighTemperature(boolean highTemperature) { this.highTemperature = highTemperature; }

        public boolean isDangerousGas() { return dangerousGas; }
        public void setDangerousGas(boolean dangerousGas) { this.dangerousGas = dangerousGas; }

        public boolean isVentilationNeeded() { return ventilationNeeded; }
        public void setVentilationNeeded(boolean ventilationNeeded) { this.ventilationNeeded = ventilationNeeded; }
    }

    // Getters y Setters
    public String getReadingId() { return readingId; }
    public void setReadingId(String readingId) { this.readingId = readingId; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getGasLevel() { return gasLevel; }
    public void setGasLevel(double gasLevel) { this.gasLevel = gasLevel; }

    public double getHumidity() { return humidity; }
    public void setHumidity(double humidity) { this.humidity = humidity; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public EnvironmentalAlerts getAlerts() { return alerts; }
    public void setAlerts(EnvironmentalAlerts alerts) { this.alerts = alerts; }
}