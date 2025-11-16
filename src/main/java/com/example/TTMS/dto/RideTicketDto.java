package com.example.TTMS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideTicketDto {

    private String city;
    private String transport;
    private String pickupLocation;
    private String dropLocation;

}
