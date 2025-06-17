package com.trackeasy.app.entities;

public class Vehicle {

    private String vehicleID;
    private String brand;
    private String color;
    private String location;
    private boolean hasTracker;
    private boolean running;

    public Vehicle() {}

    public Vehicle(String vehicleID, String brand, String color, String location, boolean hasTracker, boolean running) {
        this.vehicleID = vehicleID;
        this.brand = brand;
        this.color = color;
        this.location = location;
        this.hasTracker = hasTracker;
        this.running = running;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isHasTracker() {
        return hasTracker;
    }

    public void setHasTracker(boolean hasTracker) {
        this.hasTracker = hasTracker;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public String toString() {
        return brand + " (" + color + ")";
    }

}
