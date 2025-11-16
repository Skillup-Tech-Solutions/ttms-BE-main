package com.example.TTMS.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.TTMS.dto.ApiResponse;
import com.example.TTMS.dto.CityDto;
import com.example.TTMS.entity.City;
import com.example.TTMS.service.CityService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/city")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @PostMapping()
    public ApiResponse<City> addCity(@Valid @RequestBody CityDto cityDto) {        
        return ApiResponse.success("City created successfully", cityService.addCity(cityDto));
    }

    @GetMapping()
    public ApiResponse<List<City>> getAllCity() {
        return ApiResponse.success(cityService.getAllCity());
    }

    @GetMapping("/{id}")
    public ApiResponse<City> getCityById(@PathVariable("id") String id) {
        return ApiResponse.success(cityService.getCityById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<City> updateCity(@PathVariable String id, @Valid @RequestBody CityDto cityDto) {
        return ApiResponse.success("City updated successfully", cityService.updateCity(id, cityDto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCity(@PathVariable String id) {
        cityService.deleteCity(id);
        return ApiResponse.success("City deleted successfully", null);
    }
    
}
