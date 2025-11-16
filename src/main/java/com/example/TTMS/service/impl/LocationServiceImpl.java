package com.example.TTMS.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.TTMS.dto.LocationDto;
import com.example.TTMS.entity.City;
import com.example.TTMS.entity.Location;
import com.example.TTMS.repository.CityRepo;
import com.example.TTMS.repository.LocationCostRepo;
import com.example.TTMS.repository.LocationRepo;
import com.example.TTMS.repository.TransportRepo;
import com.example.TTMS.service.LocationService;

@Service
public class LocationServiceImpl implements LocationService {

    private final LocationRepo locationRepo;
    private final CityRepo cityRepo;
    private final LocationCostRepo locationCostRepo;

    public LocationServiceImpl(LocationRepo locationRepo, CityRepo cityRepo, LocationCostRepo locationCostRepo,
            TransportRepo transportRepo) {
        this.locationRepo = locationRepo;
        this.cityRepo = cityRepo;
        this.locationCostRepo = locationCostRepo;
    }

    @Override
    public Location addLocation(LocationDto locationDto) {
        City city = cityRepo.findById(locationDto.getCity())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));
        if (city.getLocations() != null &&
                city.getLocations().stream()
                        .anyMatch(loc -> loc.getLocationName().equalsIgnoreCase(locationDto.getLocationName()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Location with name '" + locationDto.getLocationName() + "' already exists in this city");
        }
        Location location = new Location();
        location.setCity(locationDto.getCity());
        location.setCityName(city.getCityName());
        location.setLocationId(locationDto.getLocationId());
        location.setLocationName(locationDto.getLocationName());
        location = locationRepo.save(location);
        if (city.getLocations() == null) {
            city.setLocations(new ArrayList<>());
        }
        city.getLocations().add(location);
        cityRepo.save(city);
        return location;
    }

    @Override
    public List<Location> getAllLocation() {
        return locationRepo.findAll();
    }

    @Override
    public Location getLocationById(String id) {
        return locationRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found"));
    }

    @Override
    public Location updateLocation(String id, LocationDto locationDto) {
        Location existingLocation = locationRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found"));
        City newCity = cityRepo.findById(locationDto.getCity())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));
        boolean duplicate = newCity.getLocations() != null &&
                newCity.getLocations().stream()
                        .filter(loc -> !loc.getId().equals(existingLocation.getId()))
                        .anyMatch(loc -> loc.getLocationName().equalsIgnoreCase(locationDto.getLocationName()));

        if (duplicate) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Location with name '" + locationDto.getLocationName() + "' already exists in this city");
        }
        if (!existingLocation.getCity().equals(newCity.getId())) {
            City oldCity = cityRepo.findById(existingLocation.getCity())
                    .orElse(null);
            if (oldCity != null && oldCity.getLocations() != null) {
                oldCity.getLocations().removeIf(loc -> loc.getId().equals(existingLocation.getId()));
                cityRepo.save(oldCity);
            }
        }
        existingLocation.setCity(locationDto.getCity());
        existingLocation.setCityName(newCity.getCityName());
        existingLocation.setLocationId(locationDto.getLocationId());
        existingLocation.setLocationName(locationDto.getLocationName());

        Location updatedLocation = locationRepo.save(existingLocation);
        List<Location> locations = newCity.getLocations();
        if (locations == null) {
            locations = new ArrayList<>();
        }
        locations.removeIf(loc -> loc.getId().equals(updatedLocation.getId()));
        locations.add(updatedLocation);
        newCity.setLocations(locations);
        cityRepo.save(newCity);
        return updatedLocation;

    }

    @Override
    public void deleteLocation(String id) {
        Location location = locationRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found"));

        // 🔍 Check if used in City
        boolean usedInCity = cityRepo.existsByLocationsContains(location);

        // 🔍 Check if used in LocationCost (pickup/drop)
        boolean usedInLocationCost = locationCostRepo.existsByLocationCostDetailsPickupLocation(location)
                || locationCostRepo.existsByLocationCostDetailsDropLocation(location);

        // boolean usedInTransport = transportRepo.existsByLocationsContains(location);

        if (usedInCity || usedInLocationCost) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete location. It is already mapped in other entities.");
        }
        locationRepo.deleteById(id);
    }

}
