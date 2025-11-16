package com.example.TTMS.controller;

import java.util.Map;
import java.util.HashMap;

import com.example.TTMS.entity.UserPasswordForgot;
import com.example.TTMS.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.TTMS.config.JwtHelper;
import com.example.TTMS.dto.JwtResponse;
import com.example.TTMS.dto.Login;
import com.example.TTMS.entity.Transport;
import com.example.TTMS.entity.User;
import com.example.TTMS.entity.Vendor;
import com.example.TTMS.service.TransportService;
import com.example.TTMS.service.UserService;
import com.example.TTMS.service.VendorService;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final UserService userService;
    private final JwtHelper jwtHelper;
    private final VendorService vendorService;
    private final TransportService transportService;
    private final AuthService authService;

    public AuthenticationController(UserService userService, JwtHelper jwtHelper, VendorService vendorService,
            TransportService transportService,  AuthService authService) {
        this.userService = userService;
        this.jwtHelper = jwtHelper;
        this.transportService = transportService;
        this.vendorService = vendorService;
        this.authService = authService;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(@Validated @RequestBody Login login) {

        Map<String, Object> details = new HashMap<>();
        Object account = null;
        String role = null;

        User user = userService.validateLoginCredentials(login);
        if (user != null) {
            account = user;
            role = user.getRole();
            details.put("_id", user.getId());
            details.put("userId", user.getUserId());
            details.put("username", user.getUsername());
            details.put("role", role);
        }

        if (account == null) {
            Vendor vendor = vendorService.validateLoginCredentials(login);
            if (vendor != null) {
                account = vendor;
                role = vendor.getRole();
                details.put("_id", vendor.getId());
                details.put("userId", vendor.getVendorId());
                details.put("username", vendor.getVendorName());
                details.put("role", role);
            }
        }

        if (account == null) {
            Transport transport = transportService.validateLoginCredentials(login);
            if (transport != null) {
                account = transport;
                role = transport.getRole();
                details.put("_id", transport.getId());
                details.put("userId", transport.getTransportId());
                details.put("username", transport.getOwnerDetails());
                details.put("role", role);
            }
        }
        if (account == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        JwtResponse jwtResponse = jwtHelper.createJwtForClaims(login.getUserId(), details);
        Map<String, Object> response = new HashMap<>();
        response.put("account", account);
        response.put("token", jwtResponse.getToken());
        response.put("expiry", jwtResponse.getExpiry());
        response.put("message", "Login successful");

        return ResponseEntity.ok(response);

    }

    @GetMapping("/reset-link/{userId}")
    public ResponseEntity<?> sendResetLink(@PathVariable String userId)
            throws MessagingException, Exception {

        boolean linkSent = false;
        try {
            userService.sendForgotPasswordLink(userId);
            linkSent = true;
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) throw ex;
        }

        if (!linkSent) {
            try {
                vendorService.sendForgotPasswordLink(userId);
                linkSent = true;
            } catch (ResponseStatusException ex) {
                if (ex.getStatusCode() != HttpStatus.NOT_FOUND) throw ex;
            }
        }

        if (!linkSent) {
            try {
                transportService.sendForgotPasswordLink(userId);
                linkSent = true;
            } catch (ResponseStatusException ex) {
                if (ex.getStatusCode() != HttpStatus.NOT_FOUND) throw ex;
            }
        }

        if (!linkSent) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Reset link sent to your email address.");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<?> saveResetPassword(@Valid @RequestBody UserPasswordForgot user) throws Exception {
        authService.resetPassword(user);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password changed successfully.");
        return ResponseEntity.ok(response);
    }


}
