// package com.example.TTMS.service.impl;

// import java.time.LocalDate;
// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.data.mongodb.core.query.Criteria;
// import org.springframework.data.mongodb.core.query.Query;
// import org.springframework.stereotype.Service;

// import com.example.TTMS.dto.DashboardCount;
// import com.example.TTMS.entity.City;
// import com.example.TTMS.entity.RideTicket;
// import com.example.TTMS.repository.CityRepo;
// import com.example.TTMS.repository.LocationRepo;
// import com.example.TTMS.repository.RideTicketRepo;
// import com.example.TTMS.repository.TransportRepo;
// import com.example.TTMS.repository.UserRepo;
// import com.example.TTMS.repository.VendorRepo;
// import com.example.TTMS.service.DashboardService;
// import org.springframework.data.mongodb.core.MongoTemplate;

// @Service
// public class DashboardServiceImpl implements DashboardService {

//         private final CityRepo cityRepo;
//         private final LocationRepo locationRepo;
//         private final UserRepo userRepo;
//         private final VendorRepo vendorRepo;
//         private final TransportRepo transportRepo;
//         private final RideTicketRepo rideTicketRepo;
//         private final MongoTemplate mongoTemplate;

//         public DashboardServiceImpl(CityRepo cityRepo, LocationRepo locationRepo, UserRepo userRepo,
//                         VendorRepo vendorRepo, TransportRepo transportRepo, RideTicketRepo rideTicketRepo,
//                         MongoTemplate mongoTemplate) {
//                 this.cityRepo = cityRepo;
//                 this.locationRepo = locationRepo;
//                 this.userRepo = userRepo;
//                 this.vendorRepo = vendorRepo;
//                 this.transportRepo = transportRepo;
//                 this.rideTicketRepo = rideTicketRepo;
//                 this.mongoTemplate = mongoTemplate;
//         }

//         @Override
//         public DashboardCount getDashboardCounts(String cityId, String locationId, String vendorId, String status,
//                         LocalDate startDate, LocalDate endDate) {

//                 long cityCount = 0;
//                 long locationCount = 0;
//                 long userCount = 0;
//                 long vendorCount = 0;
//                 long transportCount = 0;
//                 long ticketCount = 0;
//                 Map<String, Double> cityCostMap = new HashMap<>();

//                 Query ticketQuery = new Query();

//                 // 🔹 1️⃣ City filter
//                 if (cityId != null && !cityId.isEmpty()) {
//                         City city = cityRepo.findById(cityId).orElse(null);
//                         if (city != null) {
//                                 cityCount = 1;
//                                 locationCount = locationRepo.countByCity(city);
//                                 userCount = userRepo.countByCity(city);
//                                 vendorCount = vendorRepo.countByCity(city);

//                                 ticketQuery.addCriteria(Criteria.where("City._id").is(cityId));
//                         }
//                 } else {
//                         cityCount = cityRepo.count();
//                         locationCount = locationRepo.count();
//                         userCount = userRepo.count();
//                         vendorCount = vendorRepo.count();
//                         transportCount = transportRepo.count();
//                 }

//                 // 🔹 2️⃣ Location filter (if cityId is empty)
//                 if ((cityId == null || cityId.isEmpty()) && locationId != null && !locationId.isEmpty()) {
//                         ticketQuery.addCriteria(Criteria.where("pickupLocation._id").is(locationId));
//                 }

//                 // 🔹 3️⃣ Status filter
//                 if (status != null && !status.isEmpty()) {
//                         ticketQuery.addCriteria(Criteria.where("status").is(status));
//                 }

//                 // 🔹 4️⃣ Date range filter
//                 if (startDate != null && endDate != null) {
//                         ticketQuery.addCriteria(Criteria.where("pickupDate").gte(startDate).lte(endDate));
//                 }

//                 // 🔹 5️⃣ Ticket count
//                 ticketCount = mongoTemplate.count(ticketQuery, RideTicket.class);

//                 // 🔹 6️⃣ Cost aggregation via repo
//                 cityCostMap = rideTicketRepo.getCityCostSummary(cityId, locationId, status, startDate, endDate,
//                                 mongoTemplate);

//                 return new DashboardCount(
//                                 cityCount,
//                                 locationCount,
//                                 userCount,
//                                 vendorCount,
//                                 transportCount,
//                                 ticketCount,
//                                 cityCostMap);
//         }

// }




















package com.example.TTMS.service.impl;

import com.example.TTMS.dto.DashboardCount;
import com.example.TTMS.entity.City;
import com.example.TTMS.entity.Location;
import com.example.TTMS.entity.RideTicket;
import com.example.TTMS.entity.Transport;
import com.example.TTMS.entity.User;
import com.example.TTMS.entity.Vendor;
import com.example.TTMS.repository.CityRepo;
import com.example.TTMS.repository.LocationRepo;
import com.example.TTMS.repository.RideTicketRepo;
import com.example.TTMS.repository.TransportRepo;
import com.example.TTMS.repository.UserRepo;
import com.example.TTMS.repository.VendorRepo;
import com.example.TTMS.service.DashboardService;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final MongoTemplate mongoTemplate;
    private final RideTicketRepo rideTicketRepo;

    public DashboardServiceImpl(MongoTemplate mongoTemplate, CityRepo cityRepo, LocationRepo locationRepo,
            UserRepo userRepo, VendorRepo vendorRepo, TransportRepo transportRepo,
            RideTicketRepo rideTicketRepo) {
        this.mongoTemplate = mongoTemplate;
        this.rideTicketRepo = rideTicketRepo;
    }




    
    @Override
    public DashboardCount getDashboardCounts(String cityId, String locationId, String vendorId, String status,
            LocalDate startDate, LocalDate endDate) {

        // Count basic entities
        long cityCount = mongoTemplate.count(new Query(), City.class);
        long locationCount = mongoTemplate.count(new Query(), Location.class);
        long userCount = mongoTemplate.count(new Query(), User.class);
        long vendorCount = mongoTemplate.count(new Query(), Vendor.class);
        long transportCount = mongoTemplate.count(new Query(), Transport.class);

        // Build dynamic criteria for RideTicket
        Criteria rideCriteria = new Criteria();
        if (cityId != null && !cityId.isEmpty())
            rideCriteria.and("City._id").is(cityId);
        if (locationId != null && !locationId.isEmpty())
            rideCriteria.and("pickupLocation._id").is(locationId);
        if (vendorId != null && !vendorId.isEmpty())
            rideCriteria.and("transport.vendorId").is(vendorId);
        if (status != null && !status.isEmpty())
            rideCriteria.and("status").is(status);
        if (startDate != null && endDate != null)
            rideCriteria.and("pickupDate").gte(startDate).lte(endDate);

        Query rideQuery = new Query(rideCriteria);

        // Total ride tickets
        long rideTicketCount = mongoTemplate.count(rideQuery, RideTicket.class);

        // Total revenue
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(rideCriteria),
                Aggregation.group().sum("cost").as("totalRevenue"));
        AggregationResults<Document> totalResult = mongoTemplate.aggregate(totalAgg, "rideTicket", Document.class);
        double totalRevenue = totalResult.getMappedResults().isEmpty() ? 0
                : totalResult.getUniqueMappedResult().getDouble("totalRevenue");

        // Today's rides
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();

        Criteria todayCriteria = new Criteria().and("pickupDate").gte(todayStart).lt(todayEnd);
        Query todayQuery = new Query(todayCriteria);
        long todayRideCount = mongoTemplate.count(todayQuery, RideTicket.class);

        Aggregation todayAgg = Aggregation.newAggregation(
                Aggregation.match(todayCriteria),
                Aggregation.group().sum("cost").as("todayRevenue"));

        Aggregation ridesByCityAgg = Aggregation.newAggregation(
                Aggregation.group("City.cityName").count().as("count"),
                Aggregation.project("count").and("_id").as("cityName"));

        AggregationResults<Document> ridesByCityResult = mongoTemplate.aggregate(
                ridesByCityAgg, "rideTicket", Document.class);

        List<Map<String, Object>> ridesByCities = ridesByCityResult.getMappedResults().stream()
                .map(doc -> Map.of(
                        "cityName", doc.getString("cityName"),
                        "count", doc.get("count")))
                .collect(Collectors.toList());
        AggregationResults<Document> todayResult = mongoTemplate.aggregate(todayAgg, "rideTicket", Document.class);
        double todayRevenue = todayResult.getMappedResults().isEmpty() ? 0
                : todayResult.getUniqueMappedResult().getDouble("todayRevenue");

        // Monthly rides
        LocalDate firstOfMonth = today.withDayOfMonth(1);
        LocalDateTime monthStart = firstOfMonth.atStartOfDay();
        LocalDateTime monthEnd = today.plusDays(1).atStartOfDay();

        Criteria monthCriteria = new Criteria().and("pickupDate").gte(monthStart).lt(monthEnd);
        Query monthQuery = new Query(monthCriteria);
        long monthlyRideCount = mongoTemplate.count(monthQuery, RideTicket.class);

        Aggregation monthAgg = Aggregation.newAggregation(
                Aggregation.match(monthCriteria),
                Aggregation.group().sum("cost").as("monthlyRevenue"));
        AggregationResults<Document> monthResult = mongoTemplate.aggregate(monthAgg, "rideTicket", Document.class);
        double monthlyRevenue = monthResult.getMappedResults().isEmpty() ? 0
                : monthResult.getUniqueMappedResult().getDouble("monthlyRevenue");

        // Rides by location
        Aggregation ridesByLocationAgg = Aggregation.newAggregation(
                Aggregation.group("pickupLocation.locationName").count().as("count"),
                Aggregation.project("count").and("_id").as("locationName"));
        AggregationResults<Document> ridesResult = mongoTemplate.aggregate(ridesByLocationAgg, "rideTicket",
                Document.class);

        List<Map<String, Object>> ridesByLocation = ridesResult.getMappedResults().stream()
                .map(doc -> Map.of(
                        "locationName", doc.getString("locationName"),
                        "count", doc.get("count")))
                .collect(Collectors.toList());

        // City cost map using old repo method
        Map<String, Double> cityCostMap = rideTicketRepo.getCityCostSummary(cityId, locationId, status, startDate,
                endDate, mongoTemplate);

        // Return dashboard DTO
        return new DashboardCount(
                cityCount,
                locationCount,
                userCount,
                vendorCount,
                transportCount,
                rideTicketCount,
                totalRevenue, 
                todayRideCount,
                todayRevenue,
                monthlyRideCount,
                monthlyRevenue,
                ridesByLocation,
                cityCostMap ,
                ridesByCities
        );
    }
}
