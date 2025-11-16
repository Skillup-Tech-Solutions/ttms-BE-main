package com.example.TTMS.service;

import java.util.List;

import com.example.TTMS.dto.CityDto;
import com.example.TTMS.entity.City;

public interface CityService{

    City addCity(CityDto cityDto);

    List<City> getAllCity();

    City getCityById(String id);

    City updateCity(String id, CityDto cityDto);

    void deleteCity(String id);
    
}
