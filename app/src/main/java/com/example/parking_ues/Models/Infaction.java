package com.example.parking_ues.Models;

import java.util.Date;

public class Infaction {
    private String InfactionId;
    private String userId;
    private String vehicleId;
    private String sessionId;
    private String InfactionType; // "fuera del rango de horas", "sin pago"
    private String description;
    private Date timestamp;
    private double fine;
    private String status; // "pending", "paid", "disputed"
    private Date resolvedAt;

    // Constructor vacío
    public Infaction() {}

    public Infaction(String InfactionId, String userId, String vehicleId, String sessionId, String InfactionType, String description, double fine) {
        this.InfactionId = InfactionId;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.sessionId = sessionId;
        this.InfactionType = InfactionType;
        this.description = description;
        this.fine = fine;
        this.timestamp = new Date();
        this.status = "pending";
    }

    // Getters y Setters
    public String getInfactionId() { return InfactionId; }
    public void setInfactionId(String InfactionId) { this.InfactionId = InfactionId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getInfactionType() { return InfactionType; }
    public void setInfactionType(String InfactionType) { this.InfactionType = InfactionType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Date resolvedAt) { this.resolvedAt = resolvedAt; }
}
