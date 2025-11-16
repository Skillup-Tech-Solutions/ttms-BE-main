package com.example.TTMS.service;

import com.example.TTMS.entity.UserPasswordForgot;

public interface AuthService {
    void resetPassword(UserPasswordForgot user);
}
