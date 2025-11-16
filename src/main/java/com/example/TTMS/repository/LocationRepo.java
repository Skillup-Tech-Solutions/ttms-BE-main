package com.example.TTMS.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.TTMS.entity.City;
import com.example.TTMS.entity.Location;

public interface LocationRepo extends MongoRepository<Location, String> {

    boolean existsByCity(String id);

    long countByCity(City city);
    
}
