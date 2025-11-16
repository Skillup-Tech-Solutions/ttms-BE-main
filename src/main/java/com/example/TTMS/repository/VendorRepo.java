package com.example.TTMS.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.TTMS.entity.City;
import com.example.TTMS.entity.Vendor;

@Repository
public interface VendorRepo extends MongoRepository<Vendor, String> {

    boolean existsByCity(City city);

    Optional<Vendor> findByVendorId(String vendorId);

    default List<Vendor> getVendorsByCityAndLocation(String cityId, List<String> locationIds,
            MongoTemplate mongoTemplate) {

        Query query = new Query();
        query.addCriteria(
                Criteria.where("city.$id").is(new ObjectId(cityId)) // match DBRef city
                        .and("locations.$id").in(locationIds.stream().map(ObjectId::new).toList()) // match DBRef
                                                                                                   // locations
        );
        return mongoTemplate.find(query, Vendor.class);
    }

    default Vendor validateVendorAccess(String vendorId, String cityId, String transportId, String pickupLocationId,
            String dropLocationId, MongoTemplate mongoTemplate) {

        Query query = new Query();

        query.addCriteria(Criteria.where("_id").is(new ObjectId(vendorId))
                .and("city.$id").is(new ObjectId(cityId))
                .and("transport.$id").is(new ObjectId(transportId))
                .and("locations.$id").all(List.of(new ObjectId(pickupLocationId), new ObjectId(dropLocationId))));

        return mongoTemplate.findOne(query, Vendor.class);
    }

    Vendor findByEmail(String email);

    default void updateResetValue(String email, MongoTemplate mongoTemplate) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        Update update = new Update();
        update.set("isforgot", true);
        update.set("expiryDate", LocalDateTime.now().plusMinutes(10));
        mongoTemplate.updateFirst(query, update, Vendor.class);
    }

    default UpdateResult updatePasswordByEmailForgotPassword(String email, String password, MongoTemplate mongoTemplate){
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        Update update = new Update();
        update.set("password", password);
        update.set("isforgot", false);
        return mongoTemplate.updateFirst(query, update, Vendor.class);
    }

    long countByCity(City city);
}
