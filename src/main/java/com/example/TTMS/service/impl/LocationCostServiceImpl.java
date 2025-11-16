package com.example.TTMS.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.TTMS.dto.LocationCostDetailsDto;
import com.example.TTMS.dto.LocationCostDto;
import com.example.TTMS.entity.City;
import com.example.TTMS.entity.Location;
import com.example.TTMS.entity.LocationCost;
import com.example.TTMS.entity.LocationCostDetails;
import com.example.TTMS.repository.CityRepo;
import com.example.TTMS.repository.LocationCostRepo;
import com.example.TTMS.repository.LocationRepo;
import com.example.TTMS.service.LocationCostService;

@Service
public class LocationCostServiceImpl implements LocationCostService {

        private final LocationCostRepo locationCostRepo;
        private final LocationRepo locationRepo;
        private final CityRepo cityRepo;
        private final MongoTemplate mongoTemplate;

        public LocationCostServiceImpl(LocationCostRepo locationCostRepo, LocationRepo locationRepo, CityRepo cityRepo,
                        MongoTemplate mongoTemplate) {
                this.locationCostRepo = locationCostRepo;
                this.locationRepo = locationRepo;
                this.cityRepo = cityRepo;
                this.mongoTemplate = mongoTemplate;
        }

        @Override
        public LocationCost addLocationCost(LocationCostDto locationCostDto) {

                City city = cityRepo.findById(locationCostDto.getCity())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));

                LocationCost locationCost = locationCostRepo.findByCity(city).orElse(null);

                if (locationCost == null) {
                        locationCost = new LocationCost();
                        locationCost.setCity(city);
                        locationCost.setLocationCostDetails(new ArrayList<>());
                }

                for (LocationCostDetailsDto detailsDto : locationCostDto.getLocationCostDetails()) {

                        Location pickupLocation = locationRepo.findById(detailsDto.getPickupLocation())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                        "Pickup location not found"));

                        Location dropLocation = locationRepo.findById(detailsDto.getDropLocation())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                        "Drop location not found"));

                        // Check if same pickup-drop already exists in that city's array
                        boolean exists = locationCost.getLocationCostDetails().stream()
                                        .anyMatch(d -> d.getPickupLocation().getId().equals(pickupLocation.getId())
                                                        && d.getDropLocation().getId().equals(dropLocation.getId()));

                        if (exists) {
                                throw new ResponseStatusException(HttpStatus.CONFLICT,
                                                "Location cost already exists for pickup "
                                                                + pickupLocation.getLocationName()
                                                                + " and drop " + dropLocation.getLocationName()
                                                                + " in city "
                                                                + city.getCityName());
                        }

                        // Add new detail
                        LocationCostDetails newDetail = new LocationCostDetails();
                        newDetail.setPickupLocation(pickupLocation);
                        newDetail.setDropLocation(dropLocation);
                        newDetail.setCost(detailsDto.getCost());

                        locationCost.getLocationCostDetails().add(newDetail);
                }

                return locationCostRepo.save(locationCost);
        }

        @Override
        public List<LocationCost> getAllLocationCost() {
                return locationCostRepo.findAll();
        }

        @Override
        public LocationCost getLocationCostById(String id) {
                return locationCostRepo.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Location Cost not found"));
        }

        @Override
        public LocationCost updateLocationCost(String id, LocationCostDto locationCostDto) {

                LocationCost existing = locationCostRepo.findById(id)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Location cost not found"));

                City city = cityRepo.findById(locationCostDto.getCity())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));

                if (!existing.getCity().getId().equals(city.getId())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "City cannot be changed");
                }

                for (LocationCostDetailsDto detailsDto : locationCostDto.getLocationCostDetails()) {

                        Location pickupLocation = locationRepo.findById(detailsDto.getPickupLocation())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                        "Pickup location not found"));

                        Location dropLocation = locationRepo.findById(detailsDto.getDropLocation())
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                        "Drop location not found"));

                        // Find matching detail in the existing list
                        LocationCostDetails match = existing.getLocationCostDetails().stream()
                                        .filter(d -> d.getPickupLocation().getId().equals(pickupLocation.getId())
                                                        && d.getDropLocation().getId().equals(dropLocation.getId()))
                                        .findFirst()
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                        "No location cost found for pickup "
                                                                        + pickupLocation.getLocationName()
                                                                        + " and drop " + dropLocation.getLocationName()
                                                                        + " in this city"));

                        // Only update the cost
                        match.setCost(detailsDto.getCost());
                }

                return locationCostRepo.save(existing);
        }

        @Override
        public void deleteLocationCost(String id) {
                if (!locationCostRepo.existsById(id)) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location cost not found");
                }
                locationCostRepo.deleteById(id);
        }

        @Override
        public LocationCost getLocationCostByCityId(String cityId) {

                Query query = new Query();
                query.addCriteria(Criteria.where("city.id").is(cityId));

                LocationCost locationCost = mongoTemplate.findOne(query, LocationCost.class);

                if (locationCost == null) {
                        LocationCost emptyCost = new LocationCost();
                        emptyCost.setLocationCostDetails(Collections.emptyList());
                        return emptyCost;
                }

                if (locationCost.getLocationCostDetails() == null) {
                        locationCost.setLocationCostDetails(Collections.emptyList());
                }

                return locationCost;
        }

}
