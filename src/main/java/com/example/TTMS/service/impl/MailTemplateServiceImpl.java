package com.example.TTMS.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.TTMS.service.MailTemplateService;

@Service
public class MailTemplateServiceImpl implements MailTemplateService {

  @Autowired
  private TemplateEngine templateEngine;

  @Value("${com.custom.reset-link}")
  private String resetLink;

  @Override
  public String sendOtpMail(String otp, String name) {

    Context context = new Context();
    context.setVariable("userName", name);
    context.setVariable("otp", otp);
    return templateEngine.process("ride-otp", context);
  }

  @Override
  public String sendForgotPasswordLink(String username, String email, String token) {

    Context context = new Context();
    context.setVariable("recepient", username);
    context.setVariable("resetLink", resetLink+"?"+token);
    return templateEngine.process("reset-password", context);
  }

}

