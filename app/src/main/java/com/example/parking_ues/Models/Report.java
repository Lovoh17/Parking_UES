package com.example.parking_ues.Models;

import java.util.Date;

public class Report {
    private String reportId;
    private String date;
    private int totalSessions;
    private double totalRevenue;
    private double avgDuration;
    private String peakHours;
    private double occupancyRate;
    private int violationsCount;
    private EnvironmentalAverages environmentalAverages;
    private Date createdAt;

    // Constructor vacío
    public Report() {}

    public Report(String reportId, String date) {
        this.reportId = reportId;
        this.date = date;
        this.createdAt = new Date();
        this.environmentalAverages = new EnvironmentalAverages();
    }

    // Clase interna para promedios ambientales
    public static class EnvironmentalAverages {
        private double temperature;
        private double gasLevel;

        public EnvironmentalAverages() {}

        public EnvironmentalAverages(double temperature, double gasLevel) {
            this.temperature = temperature;
            this.gasLevel = gasLevel;
        }

        // Getters y Setters
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }

        public double getGasLevel() { return gasLevel; }
        public void setGasLevel(double gasLevel) { this.gasLevel = gasLevel; }
    }

    // Getters y Setters
    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getTotalSessions() { return totalSessions; }
    public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public double getAvgDuration() { return avgDuration; }
    public void setAvgDuration(double avgDuration) { this.avgDuration = avgDuration; }

    public String getPeakHours() { return peakHours; }
    public void setPeakHours(String peakHours) { this.peakHours = peakHours; }

    public double getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(double occupancyRate) { this.occupancyRate = occupancyRate; }

    public int getViolationsCount() { return violationsCount; }
    public void setViolationsCount(int violationsCount) { this.violationsCount = violationsCount; }

    public EnvironmentalAverages getEnvironmentalAverages() { return environmentalAverages; }
    public void setEnvironmentalAverages(EnvironmentalAverages environmentalAverages) { this.environmentalAverages = environmentalAverages; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}