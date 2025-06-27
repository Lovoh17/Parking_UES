package com.example.ues_parking.Models;

import java.io.Serializable;
import java.util.List;

public class Membership implements Serializable {
    private String planId;
    private String name; // "Silver", "Gold"
    private String description;
    private double price;
    private List<String> benefits;
    private boolean isActive;
    private int durationDays; // Duración en días

    // Constructor vacío para Firebase
    public Membership() {}

    // Constructor completo
    public Membership(String planId, String name, String description,
                          double price, List<String> benefits, int durationDays) {
        this.planId = planId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.benefits = benefits;
        this.isActive = true;
        this.durationDays = durationDays;
    }

    // Getters y Setters
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public List<String> getBenefits() { return benefits; }
    public void setBenefits(List<String> benefits) { this.benefits = benefits; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }
}