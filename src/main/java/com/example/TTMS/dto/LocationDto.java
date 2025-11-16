package com.example.TTMS.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {

    @NotBlank(message = "City is required")
    private String city;
    @NotBlank(message = "Location ID is mandatory")
    private String locationId;
    @NotBlank(message = "Location Name is required")
    private String locationName;
    
}
