package com.example.TTMS.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.TTMS.dto.ApiResponse;
import com.example.TTMS.dto.DashboardCount;
import com.example.TTMS.service.DashboardService;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping()
    public ApiResponse<DashboardCount> getDashboardCounts(
            @RequestParam(required = false) String cityId,
            @RequestParam(required = false) String locationId,
            @RequestParam(required = false) String vendorId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
                
        return ApiResponse
                .success(dashboardService.getDashboardCounts(cityId, locationId, vendorId, status, startDate, endDate));
    }

}
