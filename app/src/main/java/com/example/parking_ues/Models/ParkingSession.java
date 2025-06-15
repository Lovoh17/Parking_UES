package com.example.parking_ues.Models;

import java.util.Date;

public class ParkingSession {
    private String sessionId;
    private String vehicleId;
    private String userId;
    private String spaceId;
    private String plateNumber;
    private Date entryTime;
    private Date exitTime;
    private long duration; // en minutos
    private double totalCost;//contemplar si tiene plan
    private String status; // "activo", "completo", "infraccion"
    private String paymentStatus; // "pendiente", "pagado", "deuda"
    private String planType;
    private boolean isWithinAllowedHours;

    // Constructor vacío
    public ParkingSession() {}

    public ParkingSession(String sessionId, String vehicleId, String userId, String spaceId, String plateNumber, String planType) {
        this.sessionId = sessionId;
        this.vehicleId = vehicleId;
        this.userId = userId;
        this.spaceId = spaceId;
        this.plateNumber = plateNumber;
        this.planType = planType;
        this.entryTime = new Date();
        this.status = "active";
        this.paymentStatus = "pending";
        this.isWithinAllowedHours = true;
        this.duration = 0;
        this.totalCost = 0.0;
    }

    // Getters y Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSpaceId() { return spaceId; }
    public void setSpaceId(String spaceId) { this.spaceId = spaceId; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public Date getEntryTime() { return entryTime; }
    public void setEntryTime(Date entryTime) { this.entryTime = entryTime; }

    public Date getExitTime() { return exitTime; }
    public void setExitTime(Date exitTime) { this.exitTime = exitTime; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }

    public boolean isWithinAllowedHours() { return isWithinAllowedHours; }
    public void setWithinAllowedHours(boolean withinAllowedHours) { isWithinAllowedHours = withinAllowedHours; }
}
