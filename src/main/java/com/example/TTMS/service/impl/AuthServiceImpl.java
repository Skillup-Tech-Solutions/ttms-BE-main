package com.example.TTMS.service.impl;

import com.example.TTMS.config.JwtHelper;
import com.example.TTMS.entity.Transport;
import com.example.TTMS.entity.User;
import com.example.TTMS.entity.UserPasswordForgot;
import com.example.TTMS.entity.Vendor;
import com.example.TTMS.repository.TransportRepo;
import com.example.TTMS.repository.UserRepo;
import com.example.TTMS.repository.VendorRepo;
import com.example.TTMS.service.AuthService;
import com.mongodb.client.result.UpdateResult;
import jakarta.validation.Valid;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtHelper jwtHelper;

    private final UserRepo userRepo;
    private final TransportRepo transportRepo;
    private final VendorRepo vendorRepo;
    private final MongoTemplate mongoTemplate;

    public AuthServiceImpl(PasswordEncoder passwordEncoder, JwtHelper jwtHelper, UserRepo userRepo, TransportRepo transportRepo, VendorRepo vendorRepo,
                           MongoTemplate mongoTemplate){

        this.passwordEncoder = passwordEncoder;
        this.jwtHelper = jwtHelper;
        this.userRepo = userRepo;
        this.transportRepo = transportRepo;
        this.vendorRepo = vendorRepo;
        this.mongoTemplate = mongoTemplate;
    }

    public void resetPassword(@Valid UserPasswordForgot userRequest) {

        Map<String, Object> decodedData = jwtHelper.decodeJWTTokenForResetPassword(userRequest.getToken());
        String email = (String) decodedData.get("email");

        boolean updated = false;

        // 1️⃣ Check in USER
        User user = userRepo.findByEmail(email);
        if (user != null) {
            if (user.getExpiryDate().isAfter(LocalDateTime.now())) {
                UpdateResult result = userRepo.updatePasswordByEmailForgotPassword(
                        email, passwordEncoder.encode(userRequest.getPassword()), mongoTemplate);
                if (result.getMatchedCount() > 0) updated = true;
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Link expired");
            }
        }

        // 2️⃣ Check in VENDOR
        if (!updated) {
            Vendor vendor = vendorRepo.findByEmail(email);
            if (vendor != null) {
                if (vendor.getExpiryDate().isAfter(LocalDateTime.now())) {
                    UpdateResult result = vendorRepo.updatePasswordByEmailForgotPassword(
                            email, passwordEncoder.encode(userRequest.getPassword()), mongoTemplate);
                    if (result.getMatchedCount() > 0) updated = true;
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Link expired");
                }
            }
        }

        // 3️⃣ Check in TRANSPORT
        if (!updated) {
            Transport transport = transportRepo.findByEmail(email);
            if (transport != null) {
                if (transport.getExpiryDate().isAfter(LocalDateTime.now())) {
                    UpdateResult result = transportRepo.updatePasswordByEmailForgotPassword(
                            email, passwordEncoder.encode(userRequest.getPassword()), mongoTemplate);
                    if (result.getMatchedCount() > 0) updated = true;
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Link expired");
                }
            }
        }

        if (!updated) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found or link invalid");
        }
    }
}
