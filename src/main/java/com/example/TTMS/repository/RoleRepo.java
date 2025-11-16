package com.example.TTMS.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.TTMS.entity.Role;

public interface RoleRepo extends MongoRepository<Role, String> {
    
}
