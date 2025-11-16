package com.example.TTMS.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationCostDto {
    
    @NotBlank(message = "City is required")
    private String city;
    @NotEmpty
    private List<@Valid LocationCostDetailsDto> locationCostDetails;
}
