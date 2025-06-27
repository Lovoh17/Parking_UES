package com.example.ues_parking.Models;

import java.util.Date;

public class UserMembership {
    private String membershipId;
    private String userId;
    private String planId;
    private Date startDate;
    private Date endDate;
    private boolean isActive;
    private String paymentMethod;
    private String transactionId;

    // Constructor vacío para Firebase
    public UserMembership() {}

    // Constructor completo
    public UserMembership(String userId, String planId, int durationDays,
                          String paymentMethod, String transactionId) {
        this.userId = userId;
        this.planId = planId;
        this.startDate = new Date();
        this.endDate = new Date(startDate.getTime() + durationDays * 24 * 60 * 60 * 1000L);
        this.isActive = true;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
    }

    // Getters y Setters
    public String getMembershipId() { return membershipId; }
    public void setMembershipId(String membershipId) { this.membershipId = membershipId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
}