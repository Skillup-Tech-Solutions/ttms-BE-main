package com.example.TTMS.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.TTMS.entity.City;
import com.example.TTMS.entity.Location;
import com.example.TTMS.entity.LocationCost;
import com.example.TTMS.entity.LocationCostDetails;

public interface LocationCostRepo extends MongoRepository<LocationCost, String> {

    default LocationCostDetails findByCityAndPickUpAndDropLocation(String cityId, String pickupLocationId,
            String dropLocationId, MongoTemplate mongoTemplate) {

        Query query = new Query();
        query.addCriteria(
                Criteria.where("city.$id").is(new ObjectId(cityId))
                        .and("locationCostDetails")
                        .elemMatch(Criteria.where("pickupLocation.$id").is(new ObjectId(pickupLocationId))
                                .and("dropLocation.$id").is(new ObjectId(dropLocationId))));

        query.fields().include("locationCostDetails.$");

        LocationCost locationCost = mongoTemplate.findOne(query, LocationCost.class);

        if (locationCost != null && locationCost.getLocationCostDetails() != null
                && !locationCost.getLocationCostDetails().isEmpty()) {
            return locationCost.getLocationCostDetails().get(0);
        }

        return null;
    }

    default LocationCost findByCityAndPickupLocationAndDropLocation(City city, Location pickupLocation,
            Location dropLocation, MongoTemplate mongoTemplate) {

        Query query = new Query();
        query.addCriteria(Criteria.where("city.id").is(city.getId())
                .and("pickupLocation.id").is(pickupLocation.getId())
                .and("dropLocation.id").is(dropLocation.getId()));
        return mongoTemplate.findOne(query, LocationCost.class);
    }

    boolean existsByLocationCostDetailsPickupLocation(Location location);

    boolean existsByLocationCostDetailsDropLocation(Location location);

    boolean existsByCity(City city);

    Optional<LocationCost> findByCity(City city);

}
