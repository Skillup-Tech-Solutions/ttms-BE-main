package com.example.TTMS.service;
public interface MailTemplateService {

  String sendOtpMail(String otp, String name);

    String sendForgotPasswordLink(String username, String email, String token);
}
