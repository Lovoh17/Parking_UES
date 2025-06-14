package com.example.parking_ues.Models;

import java.util.Date;

public class User {
    private String userId;
    private String email;
    private String name;
    private String phone;
    private String role; // "cliente", "admin"
    private Date createdAt;
    private boolean isActive;
    private String planType; // "sin plan", "mensual", "vip"

    // Constructor vacío requerido para Firebase
    public User() {}

    public User(String userId, String email, String name, String phone, String role, String planType) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.planType = planType;
        this.createdAt = new Date();
        this.isActive = true;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getPlanType() { return planType; }
    public void setPlanType(String planType) { this.planType = planType; }
}