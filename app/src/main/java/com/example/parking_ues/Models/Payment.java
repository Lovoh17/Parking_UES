package com.example.parking_ues.Models;

import android.icu.text.SimpleDateFormat;

import java.util.Date;

public class Payment {
    private String paymentId;
    private String userId;
    private String sessionId;
    private String planId;
    private String violationId;
    private double amount;
    private String paymentType; // "hourly", "monthly", "violation"
    private String paymentMethod; // "cash", "card", "transfer"
    private Date timestamp;
    private String status; // "completed", "pending", "failed"
    private String receiptNumber;

    // Constructor vacío
    public Payment() {}

    public Payment(String paymentId, String userId, double amount, String paymentType, String paymentMethod) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.amount = amount;
        this.paymentType = paymentType;
        this.paymentMethod = paymentMethod;
        this.timestamp = new Date();
        this.status = "completed";
        this.receiptNumber = "REC-" + System.currentTimeMillis();
    }

    private String generateReceiptNumber(String paymentType) {
        // Formatear fecha como YYYYMMDD
//        String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
//
//        // Obtener las 3 primeras letras del tipo de pago en mayúsculas
//        String typePart = paymentType != null && paymentType.length() >= 3 ?
//                paymentType.substring(0, 3).toUpperCase() : "GEN";
//
//        // Obtener número secuencial con relleno de ceros
//        int seq = sequence.incrementAndGet();
//        if (seq > MAX_SEQUENCE) {
//            sequence.set(0);
//            seq = sequence.incrementAndGet();
//        }
//        String sequencePart = String.format("%04d", seq);

        return "REC-";// + datePart + "-" + typePart + "-" + sequencePart;
    }


    // Getters y Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public String getViolationId() { return violationId; }
    public void setViolationId(String violationId) { this.violationId = violationId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
}