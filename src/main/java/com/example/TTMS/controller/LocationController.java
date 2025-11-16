package com.example.TTMS.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.TTMS.service.LocationService;

import jakarta.validation.Valid;

import com.example.TTMS.dto.ApiResponse;
import com.example.TTMS.dto.LocationDto;
import com.example.TTMS.entity.Location;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequestMapping("/location")
@RestController
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping()
    public ApiResponse<Location> addLocation(@Valid @RequestBody LocationDto location) {        
        return ApiResponse.success("Location created successfully", locationService.addLocation(location));
    }

    @GetMapping()
    public ApiResponse<List<Location>> getAllLocation() {
        return ApiResponse.success(locationService.getAllLocation());
    }

    @GetMapping("/{id}")
    public ApiResponse<Location> getLocationById(@PathVariable("id") String id) {
        return ApiResponse.success(locationService.getLocationById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<Location> updateLocation(@PathVariable String id, @Valid @RequestBody LocationDto location) {
        return ApiResponse.success("Location updated successfully", locationService.updateLocation(id, location));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteLocation(@PathVariable String id) {
        locationService.deleteLocation(id);
        return ApiResponse.success("Location deleted successfully", null);
    }
}
