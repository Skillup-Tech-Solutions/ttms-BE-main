package com.example.TTMS.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.TTMS.dto.CityDto;
import com.example.TTMS.entity.City;
import com.example.TTMS.repository.CityRepo;
import com.example.TTMS.repository.LocationCostRepo;
import com.example.TTMS.repository.LocationRepo;
import com.example.TTMS.repository.VendorRepo;
import com.example.TTMS.service.CityService;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepo cityRepo;
    private final LocationRepo locationRepo;
    private final LocationCostRepo locationCostRepo;
    private final VendorRepo vendorRepo;

    public CityServiceImpl(CityRepo cityRepo, LocationRepo locationRepo, LocationCostRepo locationCostRepo,
            VendorRepo vendorRepo) {
        this.cityRepo = cityRepo;
        this.locationRepo = locationRepo;
        this.locationCostRepo = locationCostRepo;
        this.vendorRepo = vendorRepo;
    }

    @Override
    public City addCity(CityDto cityDto) {

        City city = new City();
        city.setCityId(cityDto.getCityId());
        city.setCityName(cityDto.getCityName());
        return cityRepo.save(city);
    }

    @Override
    public List<City> getAllCity() {
        return cityRepo.findAll();
    }

    @Override
    public City getCityById(String id) {
        return cityRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));
    }

    @Override
    public City updateCity(String id, CityDto cityDto) {
        City existingCity = cityRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));

        existingCity.setCityId(cityDto.getCityId());
        existingCity.setCityName(cityDto.getCityName());

        return cityRepo.save(existingCity);
    }

    @Override
    public void deleteCity(String id) {

        City city = cityRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));

        boolean usedInVendor = vendorRepo.existsByCity(city);
        boolean usedInLocation = locationRepo.existsByCity(city.getId());
        boolean usedInLocationCost = locationCostRepo.existsByCity(city);
        if (usedInVendor || usedInLocation || usedInLocationCost) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete city. It is already mapped in other entities.");
        }
        cityRepo.deleteById(id);
    }

}
