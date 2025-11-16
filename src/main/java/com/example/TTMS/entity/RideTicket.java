package com.example.TTMS.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideTicket {

    @Id
    private String id;
    private String userId;
    private String userName;
    private String mobileNo;
    private City City;
    private Vendor vendor;
    private Transport transport;
    private LocationCost locationCost;
    private Location pickupLocation;
    private Location dropLocation;
    private LocalDate pickupDate;
    private double cost;
    private String status;
    private LocalDateTime rideStartTime;
    private LocalDateTime rideEndTime;
    private String otp;
    private LocalDateTime otpExpiryTime;
    private boolean isOtpSent;
    private String remarks;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdRole;
    
}
