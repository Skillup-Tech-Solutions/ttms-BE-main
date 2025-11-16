package com.example.TTMS.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.TTMS.dto.ApiResponse;
import com.example.TTMS.dto.RideTicketDto;
import com.example.TTMS.entity.RideTicket;
import com.example.TTMS.service.RideTicketService;

@RestController
@RequestMapping("/ride-ticket")
public class RideTicketController {

    private final RideTicketService rideTicketService;

    public RideTicketController(RideTicketService rideTicketService) {
        this.rideTicketService = rideTicketService;
    }

    @PostMapping("/create")
    public ApiResponse<RideTicket> createRideTicket(@RequestBody RideTicketDto rideTicketDto) {
        RideTicket rideTicket = rideTicketService.createRideTicket(rideTicketDto);
        return ApiResponse.success("Ride ticket created successfully", rideTicket);
    }

    @GetMapping("my-tickets")
    public ApiResponse<List<RideTicket>> getMyTickets(@RequestParam(required = false) String search,
            Authentication authentication) {
        return ApiResponse.success(rideTicketService.getMyTickets(search, authentication));
    }

    @GetMapping("/send-otp/{id}")
    public ApiResponse<String> sendOtp(@PathVariable String id) {
        rideTicketService.sendOtp(id);
        return ApiResponse.success("OTP sent successfully");
    }

    @PostMapping("/verify-otp/{id}")
    public ApiResponse<String> verifyOtp(@PathVariable String id, @RequestParam String otp,
            @RequestParam(required = false) String dropLocation) {
        rideTicketService.verifyOtp(id, otp, dropLocation);
        return ApiResponse.success("OTP verified successfully.");
    }

    @PutMapping("/update-remarks/{id}")
    public ApiResponse<RideTicket> updateRemarks(@PathVariable String id,
            @RequestParam(required = false) String remarks, @RequestParam String status) {
        return ApiResponse.success("Remarks updated successfully",
                rideTicketService.updateRemarks(id, remarks, status));
    }

}
