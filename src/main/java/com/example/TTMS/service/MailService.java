package com.example.TTMS.service;

import jakarta.mail.MessagingException;

public interface MailService {

  void sendMail(String recepient, String subject, String content) throws MessagingException;

}
