package com.example.TTMS.service;

import java.util.List;

import com.example.TTMS.dto.Login;
import com.example.TTMS.dto.TransportDto;
import com.example.TTMS.entity.Transport;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

public interface TransportService {

    Transport addTransport(@Valid TransportDto transport);

    List<Transport> getAllTransports();

    Transport validateLoginCredentials(Login login);

    Transport updateTransport(String id, @Valid TransportDto transportDto);

    void deleteTransport(String id);

    List<Transport> getTransportByCity(String city);

    void sendForgotPasswordLink(String userId) throws MessagingException;
}
