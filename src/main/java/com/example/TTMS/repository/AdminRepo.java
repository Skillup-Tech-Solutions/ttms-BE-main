package com.example.TTMS.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.TTMS.entity.Admin;
import com.example.TTMS.entity.Role;

public interface AdminRepo extends MongoRepository<Admin, String> {

    Optional<Role> findByUserId(String userId);

    Optional<Role> findByEmail(String email);
    
}
