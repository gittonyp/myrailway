// File Path: /home/tony/intellih/myrailway/src/main/java/com/devtony/myrailway/model/SeatConfiguration.java
package com.devtony.myrailway.model;

public class SeatConfiguration {
    private int configId;
    private int trainId;
    private String className;
    private int totalSeats;
    private double price;

    // Getters and setters
    public int getConfigId() { return configId; }
    public void setConfigId(int configId) { this.configId = configId; }
    public int getTrainId() { return trainId; }
    public void setTrainId(int trainId) { this.trainId = trainId; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
