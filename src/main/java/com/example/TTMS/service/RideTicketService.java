package com.example.TTMS.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;

import com.example.TTMS.dto.RideTicketDto;
import com.example.TTMS.entity.RideTicket;
import com.example.TTMS.entity.User;

public interface RideTicketService {

    RideTicket createRideTicket(RideTicketDto rideTicket);

    List<RideTicket> getMyTickets(String search, Authentication authentication);

    void createRide(User user, LocalDate pickupDate);

    void sendOtp(String id);

    void verifyOtp(String id, String otp, String dropLocation);

    RideTicket updateRemarks(String id, String remarks, String status);

}
