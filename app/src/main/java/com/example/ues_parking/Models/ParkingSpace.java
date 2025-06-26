package com.example.ues_parking.Models;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ParkingSpace {
    private String spaceId;
    private int spaceNumber;
    private boolean isOccupied;
    private String currentVehicleId;
    private String section; // normal o vip
    private boolean isReserved;
    private Date lastUpdated;
    private Date occupationStartTime; // Nuevo: cuando comenzó la ocupación
    private String occupiedByUserId;  // Nuevo: ID del usuario que ocupa
    private long totalOccupationTime; // Nuevo: tiempo acumulado en minutos

    // Constructor vacío necesario para Firebase
    public ParkingSpace() {}

    public ParkingSpace(String spaceId, int spaceNumber, String section) {
        this.spaceId = spaceId;
        this.spaceNumber = spaceNumber;
        this.section = section;
        this.isOccupied = false;
        this.isReserved = false;
        this.lastUpdated = new Date();
        this.totalOccupationTime = 0;
    }

    // Getters y Setters
    public String getSpaceId() { return spaceId; }
    public void setSpaceId(String spaceId) { this.spaceId = spaceId; }

    public int getSpaceNumber() { return spaceNumber; }
    public void setSpaceNumber(int spaceNumber) { this.spaceNumber = spaceNumber; }

    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) {
        this.isOccupied = occupied;
        if (occupied) {
            this.occupationStartTime = new Date(); // Registrar inicio al ocupar
        } else {
            calculateOccupationTime(); // Calcular tiempo al desocupar
        }
    }

    public String getCurrentVehicleId() { return currentVehicleId; }
    public void setCurrentVehicleId(String currentVehicleId) {
        this.currentVehicleId = currentVehicleId;
    }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public boolean isReserved() { return isReserved; }
    public void setReserved(boolean reserved) { this.isReserved = reserved; }

    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }

    // Nuevos métodos para manejo del tiempo de ocupación
    public Date getOccupationStartTime() { return occupationStartTime; }
    public void setOccupationStartTime(Date occupationStartTime) {
        this.occupationStartTime = occupationStartTime;
    }

    public String getOccupiedByUserId() { return occupiedByUserId; }
    public void setOccupiedByUserId(String occupiedByUserId) {
        this.occupiedByUserId = occupiedByUserId;
    }

    public long getTotalOccupationTime() { return totalOccupationTime; }
    public void setTotalOccupationTime(long totalOccupationTime) {
        this.totalOccupationTime = totalOccupationTime;
    }

    /**
     * Calcula el tiempo transcurrido desde que se ocupó el espacio
     * @return Tiempo en minutos (0 si no está ocupado)
     */
    public long getCurrentOccupationDuration() {
        if (!isOccupied || occupationStartTime == null) {
            return 0;
        }
        long diffInMillis = new Date().getTime() - occupationStartTime.getTime();
        return TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
    }

    /**
     * Calcula y acumula el tiempo de ocupación cuando se libera el espacio
     */
    private void calculateOccupationTime() {
        if (occupationStartTime != null) {
            long duration = getCurrentOccupationDuration();
            this.totalOccupationTime += duration;
            this.occupationStartTime = null;
        }
    }

    /**
     * Obtiene el tiempo total de ocupación (acumulado + actual)
     */
    public long getCombinedOccupationTime() {
        return totalOccupationTime + getCurrentOccupationDuration();
    }

    /**
     * Formatea el tiempo de ocupación para mostrar al usuario
     */
    public String getFormattedOccupationTime() {
        long totalMinutes = getCombinedOccupationTime();
        if (totalMinutes <= 0) return "0m";

        long hours = TimeUnit.MINUTES.toHours(totalMinutes);
        long minutes = totalMinutes % 60;

        if (hours > 0) {
            return String.format("%dh %02dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }
}