package com.example.TTMS.entity;

import lombok.Getter;

@Getter
public enum Status {

    PENDING("Pending"),
    CREATED("created"),
    BOOKED("Booked"),
    RIDE_STARTED("Ride Started"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed");

    private final String label;

    private Status(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
