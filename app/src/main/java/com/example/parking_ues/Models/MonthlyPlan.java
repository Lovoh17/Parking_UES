package com.example.parking_ues.Models;

import java.util.Date;

public class MonthlyPlan {
    private String planId;
    private String userId;
    private String vehicleId;
    private Date startDate;
    private Date endDate;
    private String allowedStartTime;//rango de tiempo disponible
    private String allowedEndTime;
    private double monthlyFee;//precio
    private boolean isActive;
    private int InfractionCount;//conteo de infarcciones
    private Date createdAt;

    // Constructor vacío
    public MonthlyPlan() {}

    public MonthlyPlan(String planId, String userId, String vehicleId, String allowedStartTime, String allowedEndTime, double monthlyFee) {
        this.planId = planId;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.allowedStartTime = allowedStartTime;
        this.allowedEndTime = allowedEndTime;
        this.monthlyFee = monthlyFee;
        this.isActive = true;
        this.InfractionCount = 0;
        this.createdAt = new Date();
    }

    // Getters y Setters
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getAllowedStartTime() { return allowedStartTime; }
    public void setAllowedStartTime(String allowedStartTime) { this.allowedStartTime = allowedStartTime; }

    public String getAllowedEndTime() { return allowedEndTime; }
    public void setAllowedEndTime(String allowedEndTime) { this.allowedEndTime = allowedEndTime; }

    public double getMonthlyFee() { return monthlyFee; }
    public void setMonthlyFee(double monthlyFee) { this.monthlyFee = monthlyFee; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getInfractionCount() { return InfractionCount; }
    public void setInfractionCount(int infractionCount) { this.InfractionCount = infractionCount; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}