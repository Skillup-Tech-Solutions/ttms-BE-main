package com.example.TTMS.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.TTMS.entity.City;
import com.example.TTMS.entity.Location;

public interface CityRepo extends MongoRepository<City, String> {

    boolean existsByCityId(String cityId);

    boolean existsByCityName(String cityName);

    default void updateLocation(String cityId, String locationId, MongoTemplate mongoTemplate) {

        Query locationQuery = new Query().addCriteria(Criteria.where("id").is(locationId));
        Location location = mongoTemplate.findOne(locationQuery, Location.class);
        if (location == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found");
        }
        Query query = new Query(Criteria.where("id").is(cityId));
        Update update = new Update();
        update.addToSet("locations", location);
        mongoTemplate.updateFirst(query, update, City.class);
    }

    boolean existsByLocationsContains(Location location);

}
