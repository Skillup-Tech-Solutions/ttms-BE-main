package com.example.TTMS.dto;


import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotBlank(message = "User ID is required")
    private String userId;
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Address is required")
    private String address;
    @NotBlank(message = "city is required")
    private String cityId;
    @NotBlank(message = "Mobile No is required")
    private String mobileNo;
    @NotBlank(message = "Pickup Location is required")
    private String pickupLocation;
    @NotNull(message = "Pickup Date is required")
    private LocalDate pickupDate;
    @NotBlank(message = "Transport is required")
    private String transport;
    @NotNull(message = "No of Person is required")
    private int noOfPerson;
    private String email;
    @NotBlank(message = "Role is required")
    private String role;
    
}
