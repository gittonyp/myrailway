package com.devtony.myrailway.model;

public class Train {
    private int trainId;
    private String trainName;
    private String trainNumber;
    private int totalSeats;

    // Getters and setters
    public int getTrainId() { return trainId; }
    public void setTrainId(int trainId) { this.trainId = trainId; }

    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
}