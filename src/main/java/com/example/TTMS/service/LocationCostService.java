package com.example.TTMS.service;

import java.util.List;

import com.example.TTMS.dto.LocationCostDto;
import com.example.TTMS.entity.LocationCost;

public interface LocationCostService {

    LocationCost addLocationCost(LocationCostDto locationCostDto);

    List<LocationCost> getAllLocationCost();

    LocationCost getLocationCostById(String id);

    LocationCost updateLocationCost(String id, LocationCostDto locationCostDto);

    void deleteLocationCost(String id);

    LocationCost getLocationCostByCityId(String cityId);

}
