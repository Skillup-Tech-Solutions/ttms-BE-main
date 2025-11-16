package com.example.TTMS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardCount {

    private long cityCount;
    private long locationCount;
    private long userCount;
    private long vendorCount;
    private long transportCount;
    private long rideTicketCount;

    // Revenue & rides
    private double totalRevenue;
    private long todayRideCount;
    private double todayRevenue;
    private long monthlyRideCount;
    private double monthlyRevenue;

    // Rides grouped by location
    private List<Map<String, Object>> ridesByLocation;

    // Optional cost aggregation per city (if needed for charts)
    private Map<String, Double> cityCostMap;
    private List<Map<String, Object>> ridesByCities;

}
