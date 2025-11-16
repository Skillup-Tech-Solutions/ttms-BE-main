package com.example.TTMS.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityDto {

    @NotBlank(message = "City ID is required")
    private String cityId;
    @NotBlank(message = "City Name is required")
    private String cityName;

}
