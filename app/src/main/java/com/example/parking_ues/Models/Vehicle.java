package com.example.parking_ues.Models;

import java.util.Date;

public class Vehicle {
    private String vehicleId;
    private String userId;
    private String plateNumber;
    private String brand;
    private String model;
    private String color;
    private boolean isActive;
    private Date registeredAt;

    // Constructor vacío
    public Vehicle() {}

    public Vehicle(String vehicleId, String userId, String plateNumber, String brand, String model, String color) {
        this.vehicleId = vehicleId;
        this.userId = userId;
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.isActive = true;
        this.registeredAt = new Date();
    }

    // Getters y Setters
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }


    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Date getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(Date registeredAt) { this.registeredAt = registeredAt; }
}