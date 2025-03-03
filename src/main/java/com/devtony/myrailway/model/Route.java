// File Path: /home/tony/intellih/myrailway/src/main/java/com/devtony/myrailway/model/Route.java
package com.devtony.myrailway.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Route {
    private int routeId;
    private Train train;
    private List<Station> stations;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int stationOrder;

    // Getters and setters
    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }
    public Train getTrain() { return train; }
    public void setTrain(Train train) { this.train = train; }
    public List<Station> getStations() { return stations; }
    public void setStations(List<Station> stations) { this.stations = stations; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public int getStationOrder() { return stationOrder; }
    public void setStationOrder(int stationOrder) { this.stationOrder = stationOrder; }
    public float durationHours;
    public void setDurationHours(int durationHours) {
        LocalDateTime departureTime = LocalDateTime.from(getDepartureTime().toLocalTime());
        LocalDateTime arrivalTime = LocalDateTime.from(getArrivalTime().toLocalTime());

        // Calculate duration in hours
         durationHours = (int) ChronoUnit.HOURS.between(departureTime, arrivalTime);
    }

    public long getDurationHours() {
        return 0;
    }
}
