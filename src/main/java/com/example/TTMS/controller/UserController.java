package com.example.TTMS.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.TTMS.dto.ApiResponse;
import com.example.TTMS.dto.UserDto;
import com.example.TTMS.entity.User;
import com.example.TTMS.service.UserService;

import jakarta.validation.Valid;
import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public ApiResponse<User> addUser(@Valid @RequestBody UserDto userDto) {
        User createdUser = userService.addUser(userDto);
        return ApiResponse.success("User created successfully", createdUser);
    }

    @GetMapping()
    public ApiResponse<List<User>> getAllUser(Authentication authentication) {
        return ApiResponse.success(userService.getAllUser(authentication));
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable String id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable String id, @Valid @RequestBody UserDto userDto) {
        User updatedUser = userService.updateUser(id, userDto);
        return ApiResponse.success("User updated successfully", updatedUser);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ApiResponse.success("User deleted successfully", null);
    }

    @GetMapping("/reset-link/{email}")
    public ResponseEntity<?> sendResetLink(@PathVariable String email) throws MessagingException, Exception { 
        userService.sendForgotPasswordLink(email); 
        return ResponseEntity.ok("Reset link sent to your email address.");
    }

//    @PutMapping("/forgot-password")
//    public ResponseEntity<?> saveResetPassword(@Valid @RequestBody UserPasswordForgot user) throws Exception{
//        userService.resetPassword(user);
//        return ResponseEntity.ok("Password changed successfully.");
//    }

}
