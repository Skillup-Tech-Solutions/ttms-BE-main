package com.example.TTMS.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.TTMS.entity.City;
import com.example.TTMS.entity.User;

public interface UserRepo extends MongoRepository<User, String> {

    Optional<User> findByUserId(String userId);

    List<User> findByRole(String role);

    User findByEmail(String email);

    default void updateResetValue(String email, MongoTemplate mongoTemplate) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        Update update = new Update();
        update.set("isforgot", true);
        update.set("expiryDate", LocalDateTime.now().plusMinutes(10));
        mongoTemplate.updateFirst(query, update, User.class);
    }

    default UpdateResult updatePasswordByEmailForgotPassword(String email, String password, MongoTemplate mongoTemplate){
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        Update update = new Update();
        update.set("password", password);
        update.set("isforgot", false);
        return mongoTemplate.updateFirst(query, update, User.class);
    }

    long countByCity(City city);
}
