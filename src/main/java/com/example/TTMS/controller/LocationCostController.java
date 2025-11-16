package com.example.TTMS.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.TTMS.dto.ApiResponse;
import com.example.TTMS.dto.LocationCostDto;
import com.example.TTMS.entity.LocationCost;
import com.example.TTMS.service.LocationCostService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/locationcost")
public class LocationCostController {

    private final LocationCostService locationCostService;

    public LocationCostController(LocationCostService locationCostService) {
        this.locationCostService = locationCostService;
    }

    @PostMapping()
    public ApiResponse<LocationCost> addLocationCost(@Valid @RequestBody LocationCostDto locationCostDto) {        
        return ApiResponse.success("LocationCost created successfully",locationCostService.addLocationCost(locationCostDto));
    }

    @GetMapping()
    public ApiResponse<List<LocationCost>> getAllLocationCost() {
        return ApiResponse.success(locationCostService.getAllLocationCost());
    }

    @GetMapping("/{id}")
    public ApiResponse<LocationCost> getLocationCostById(@PathVariable String id) {
        return ApiResponse.success(locationCostService.getLocationCostById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<LocationCost> updateLocationCost(@PathVariable String id, @Valid @RequestBody LocationCostDto locationCostDto) {
        return ApiResponse.success("LocationCost updated successfully", locationCostService.updateLocationCost(id, locationCostDto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteLocationCost(@PathVariable String id) {
        locationCostService.deleteLocationCost(id);
        return ApiResponse.success("LocationCost deleted successfully", null);
    }

    @GetMapping("/city/{cityId}")
    public ApiResponse<LocationCost> getLocationCostByCityId(@PathVariable String cityId){
        return ApiResponse.success(locationCostService.getLocationCostByCityId(cityId));
    }
    
}
