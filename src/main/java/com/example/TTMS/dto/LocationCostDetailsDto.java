package com.example.TTMS.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationCostDetailsDto {

    @NotBlank(message = "Pickup location is required")
    private String pickupLocation;
    @NotBlank(message = "Drop location is required")
    private String dropLocation;
    @NotNull(message = "Cost is required")
    private double cost;

}
