package com.example.TTMS.entity;

import org.springframework.data.mongodb.core.mapping.DBRef;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationCostDetails {

    @DBRef
    private Location pickupLocation;
    @DBRef
    private Location dropLocation;
    private double cost;

}
