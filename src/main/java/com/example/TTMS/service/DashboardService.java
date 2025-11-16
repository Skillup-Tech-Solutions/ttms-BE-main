package com.example.TTMS.service;

import java.time.LocalDate;

import com.example.TTMS.dto.DashboardCount;

public interface DashboardService {

    DashboardCount getDashboardCounts(String cityId, String locationId, String vendorId, String status,
            LocalDate startDate, LocalDate endDate);

}
