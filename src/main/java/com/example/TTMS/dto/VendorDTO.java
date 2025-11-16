package com.example.TTMS.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorDTO {

    @NotBlank(message = "Vendor ID is mandatory")
    private String vendorId;
    @NotBlank(message = "Vendor Name is required")
    private String vendorName;
    @NotBlank(message = "City is required")
    private String city;
    // @NotEmpty(message = "locations is required")
    // private List<String> locations;
    @NotBlank(message = "Address is required")
    private String address;
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Mobile is required")
    private String mobile;
    
}
