package com.example.TTMS.service;

import java.util.List;

import com.example.TTMS.dto.LocationDto;
import com.example.TTMS.entity.Location;

public interface LocationService {

    Location addLocation(LocationDto location);

    List<Location> getAllLocation();

    Location getLocationById(String id);

    Location updateLocation(String id, LocationDto location);

    void deleteLocation(String id);

}
