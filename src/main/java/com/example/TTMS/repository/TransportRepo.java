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

import com.example.TTMS.entity.Transport;

public interface TransportRepo extends MongoRepository<Transport, String> {

    // boolean existsByLocationsContains(Location location);

    Optional<Transport> findByTransportId(String transportId);

    default List<Transport> getTransportByCity(String city, MongoTemplate mongoTemplate){

        Query query = new Query();
        query.addCriteria(Criteria.where("city.$id").is(new ObjectId(city)));
        return mongoTemplate.find(query, Transport.class);
    }

    default void updateTransportStatus(String transport, String status, MongoTemplate mongoTemplate){

        Query query = new Query(Criteria.where("id").is(transport));
        Update update = new Update();
        update.set("status", status);
        mongoTemplate.updateFirst(query, update, Transport.class);
    }

    Transport findByEmail(String email);

    default void updateResetValue(String email, MongoTemplate mongoTemplate) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        Update update = new Update();
        update.set("isforgot", true);
        update.set("expiryDate", LocalDateTime.now().plusMinutes(10));
        mongoTemplate.updateFirst(query, update, Transport.class);
    }

    default UpdateResult updatePasswordByEmailForgotPassword(String email, String password, MongoTemplate mongoTemplate){
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        Update update = new Update();
        update.set("password", password);
        update.set("isforgot", false);
        return mongoTemplate.updateFirst(query, update, Transport.class);
    }
}
