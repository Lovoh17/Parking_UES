package com.example.parking_ues.Models;

import java.util.Date;

public class ParkingSpace {
    private String spaceId;
    private int spaceNumber;
    private boolean isOccupied;
    private String currentVehicleId;
    private String section;//normal o vip
    private boolean isReserved;//espacios vip pueden ser reservados
    private Date lastUpdated;

    // Constructor vacío
    public ParkingSpace() {}

    public ParkingSpace(String spaceId, int spaceNumber, String section) {
        this.spaceId = spaceId;
        this.spaceNumber = spaceNumber;
        this.section = section;
        this.isOccupied = false;
        this.isReserved = false;
        this.lastUpdated = new Date();
    }

    // Getters y Setters
    public String getSpaceId() { return spaceId; }
    public void setSpaceId(String spaceId) { this.spaceId = spaceId; }

    public int getSpaceNumber() { return spaceNumber; }
    public void setSpaceNumber(int spaceNumber) { this.spaceNumber = spaceNumber; }

    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { isOccupied = occupied; }

    public String getCurrentVehicleId() { return currentVehicleId; }
    public void setCurrentVehicleId(String currentVehicleId) { this.currentVehicleId = currentVehicleId; }


    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public boolean isReserved() { return isReserved; }
    public void setReserved(boolean reserved) { isReserved = reserved; }

    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
}
