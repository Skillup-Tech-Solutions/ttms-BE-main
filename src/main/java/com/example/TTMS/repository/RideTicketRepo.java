package com.example.TTMS.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.TTMS.entity.RideTicket;

public interface RideTicketRepo extends MongoRepository<RideTicket, String> {

    List<RideTicket> findByPickupDate(LocalDate today);

    default Map<String, Double> getCityCostSummary(String cityId, String locationId, String status,
                                                   LocalDate startDate, LocalDate endDate,
                                                   MongoTemplate mongoTemplate) {

        List<AggregationOperation> operations = new ArrayList<>();
        List<Criteria> criteriaList = new ArrayList<>();

        if (cityId != null && !cityId.isEmpty()) {
            criteriaList.add(Criteria.where("City._id").is(cityId));
        } else if (locationId != null && !locationId.isEmpty()) {
            criteriaList.add(Criteria.where("pickupLocation._id").is(locationId));
        }

        if (status != null && !status.isEmpty()) {
            criteriaList.add(Criteria.where("status").is(status));
        }

        if (startDate != null && endDate != null) {
            criteriaList.add(Criteria.where("pickupDate").gte(startDate).lte(endDate));
        } else if (startDate != null) {
            criteriaList.add(Criteria.where("pickupDate").gte(startDate));
        } else if (endDate != null) {
            criteriaList.add(Criteria.where("pickupDate").lte(endDate));
        }

        if (!criteriaList.isEmpty()) {
            operations.add(Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0]))));
        }

        operations.add(Aggregation.group("City.cityName").sum("cost").as("totalCost"));

        operations.add(Aggregation.project("totalCost").and("_id").as("cityName"));

        Aggregation aggregation = Aggregation.newAggregation(operations);

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, RideTicket.class, Document.class);

        Map<String, Double> cityCostMap = new HashMap<>();
        for (Document doc : results) {
            String cityName = doc.getString("cityName");
            Double totalCost = doc.getDouble("totalCost");
            if (cityName != null) {
                cityCostMap.put(cityName, totalCost != null ? totalCost : 0.0);
            }
        }

        return cityCostMap;
    }


}
