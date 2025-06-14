package com.example.parking_ues.Models;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Transaction {
    private String transactionId;
    private String userId;
    private String relatedId; // sessionId, planId, violationId según el tipo
    private double totalAmount;
    private String transactionType; // "parking_payment", "monthly_plan", "violation_fine", "refund"
    private String transactionStatus; // "pending", "processing", "completed", "failed", "cancelled", "refunded"
    private String paymentMethod; // "cash", "card", "bank_transfer", "digital_wallet"
    private Date createdAt;
    private Date completedAt;
    private Date cancelledAt;
    private String description;
    private String referenceNumber; // Número de referencia bancaria o del método de pago
    private TransactionDetails details;
    private List<TransactionItem> items;
    private String processedBy; // userId del operador que procesó la transacción
    private String cancelReason;
    private double refundAmount;
    private String currency;

    // Constructor vacío requerido para Firebase
    public Transaction() {
        this.items = new ArrayList<>();
        this.currency = "USD";
    }

    public Transaction(String transactionId, String userId, String relatedId, double totalAmount,
                       String transactionType, String paymentMethod, String description) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.relatedId = relatedId;
        this.totalAmount = totalAmount;
        this.transactionType = transactionType;
        this.paymentMethod = paymentMethod;
        this.description = description;
        this.transactionStatus = "pending";
        this.createdAt = new Date();
        this.referenceNumber = "TRX-" + System.currentTimeMillis();
        this.items = new ArrayList<>();
        this.details = new TransactionDetails();
        this.currency = "USD";
        this.refundAmount = 0.0;
    }

    // Clase interna para detalles de la transacción
    public static class TransactionDetails {
        private String bankName;
        private String cardLastFourDigits;
        private String authorizationCode;
        private String merchantId;
        private String terminalId;
        private double processingFee;
        private double taxAmount;
        private String batchNumber;

        public TransactionDetails() {
            this.processingFee = 0.0;
            this.taxAmount = 0.0;
        }

        // Getters y Setters
        public String getBankName() { return bankName; }
        public void setBankName(String bankName) { this.bankName = bankName; }

        public String getCardLastFourDigits() { return cardLastFourDigits; }
        public void setCardLastFourDigits(String cardLastFourDigits) { this.cardLastFourDigits = cardLastFourDigits; }

        public String getAuthorizationCode() { return authorizationCode; }
        public void setAuthorizationCode(String authorizationCode) { this.authorizationCode = authorizationCode; }

        public String getMerchantId() { return merchantId; }
        public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

        public String getTerminalId() { return terminalId; }
        public void setTerminalId(String terminalId) { this.terminalId = terminalId; }

        public double getProcessingFee() { return processingFee; }
        public void setProcessingFee(double processingFee) { this.processingFee = processingFee; }

        public double getTaxAmount() { return taxAmount; }
        public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

        public String getBatchNumber() { return batchNumber; }
        public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    }

    // Clase interna para items de la transacción
    public static class TransactionItem {
        private String itemId;
        private String itemType; // "parking_time", "monthly_fee", "violation_fine", "processing_fee"
        private String description;
        private int quantity;
        private double unitPrice;
        private double totalPrice;
        private Date periodStart;
        private Date periodEnd;

        public TransactionItem() {}

        public TransactionItem(String itemId, String itemType, String description,
                               int quantity, double unitPrice) {
            this.itemId = itemId;
            this.itemType = itemType;
            this.description = description;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = quantity * unitPrice;
        }

        // Getters y Setters
        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }

        public String getItemType() { return itemType; }
        public void setItemType(String itemType) { this.itemType = itemType; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) {
            this.quantity = quantity;
            this.totalPrice = quantity * unitPrice;
        }

        public double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(double unitPrice) {
            this.unitPrice = unitPrice;
            this.totalPrice = quantity * unitPrice;
        }

        public double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

        public Date getPeriodStart() { return periodStart; }
        public void setPeriodStart(Date periodStart) { this.periodStart = periodStart; }

        public Date getPeriodEnd() { return periodEnd; }
        public void setPeriodEnd(Date periodEnd) { this.periodEnd = periodEnd; }
    }

    // Métodos de utilidad
    public void addItem(TransactionItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        recalculateTotal();
    }

    private void recalculateTotal() {
        this.totalAmount = 0.0;
        if (this.items != null) {
            for (TransactionItem item : this.items) {
                this.totalAmount += item.getTotalPrice();
            }
        }
        if (this.details != null) {
            this.totalAmount += this.details.getProcessingFee() + this.details.getTaxAmount();
        }
    }

    public void markAsCompleted(String processedBy) {
        this.transactionStatus = "completed";
        this.completedAt = new Date();
        this.processedBy = processedBy;
    }

    public void markAsFailed(String reason) {
        this.transactionStatus = "failed";
        this.cancelReason = reason;
        this.cancelledAt = new Date();
    }

    public void markAsCancelled(String reason, String cancelledBy) {
        this.transactionStatus = "cancelled";
        this.cancelReason = reason;
        this.cancelledAt = new Date();
        this.processedBy = cancelledBy;
    }

    public void processRefund(double refundAmount, String processedBy) {
        this.refundAmount = refundAmount;
        this.transactionStatus = "refunded";
        this.processedBy = processedBy;
        this.completedAt = new Date();
    }

    public boolean isPending() {
        return "pending".equals(this.transactionStatus);
    }

    public boolean isCompleted() {
        return "completed".equals(this.transactionStatus);
    }

    public boolean isFailed() {
        return "failed".equals(this.transactionStatus);
    }

    public boolean isCancelled() {
        return "cancelled".equals(this.transactionStatus);
    }

    public boolean isRefunded() {
        return "refunded".equals(this.transactionStatus);
    }

    // Getters y Setters principales
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRelatedId() { return relatedId; }
    public void setRelatedId(String relatedId) { this.relatedId = relatedId; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getCompletedAt() { return completedAt; }
    public void setCompletedAt(Date completedAt) { this.completedAt = completedAt; }

    public Date getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(Date cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public TransactionDetails getDetails() { return details; }
    public void setDetails(TransactionDetails details) { this.details = details; }

    public List<TransactionItem> getItems() { return items; }
    public void setItems(List<TransactionItem> items) {
        this.items = items;
        recalculateTotal();
    }

    public String getProcessedBy() { return processedBy; }
    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public double getRefundAmount() { return refundAmount; }
    public void setRefundAmount(double refundAmount) { this.refundAmount = refundAmount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", userId='" + userId + '\'' +
                ", totalAmount=" + totalAmount +
                ", transactionType='" + transactionType + '\'' +
                ", transactionStatus='" + transactionStatus + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}