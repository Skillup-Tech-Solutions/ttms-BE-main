package com.example.TTMS.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.TTMS.config.JwtHelper;
import com.example.TTMS.service.MailService;
import com.example.TTMS.service.MailTemplateService;
import jakarta.mail.MessagingException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.TTMS.dto.Login;
import com.example.TTMS.dto.TransportDto;
import com.example.TTMS.entity.City;
import com.example.TTMS.entity.Transport;
import com.example.TTMS.entity.TransportStatus;
import com.example.TTMS.entity.Vendor;
import com.example.TTMS.repository.CityRepo;
import com.example.TTMS.repository.TransportRepo;
import com.example.TTMS.repository.VendorRepo;
import com.example.TTMS.service.TransportService;

import jakarta.validation.Valid;

@Service
public class TransportServiceImpl implements TransportService {

    private final TransportRepo transportRepo;
    private final VendorRepo vendorRepo;
    private final CityRepo cityRepo;
    private final PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;
    private final JwtHelper jwtHelper;
    private final MailTemplateService mailTemplateService;
    private final MailService mailService;

    public TransportServiceImpl(TransportRepo transportRepo, VendorRepo vendorRepo, CityRepo cityRepo,
            PasswordEncoder passwordEncoder, MongoTemplate mongoTemplate, JwtHelper jwtHelper,
                                MailTemplateService mailTemplateService, MailService mailService ) {
        this.transportRepo = transportRepo;
        this.vendorRepo = vendorRepo;
        this.cityRepo = cityRepo;
        this.passwordEncoder = passwordEncoder;
        this.mongoTemplate = mongoTemplate;
        this.jwtHelper = jwtHelper;
        this.mailTemplateService = mailTemplateService;
        this.mailService = mailService;
    }

    @Override
    public Transport addTransport(@Valid TransportDto transportDto) {

        Transport transport = new Transport();
        transport.setTransportId(transportDto.getTransportId());
        transport.setVehicleNo(transportDto.getVehicleNo());
        transport.setOwnerDetails(transportDto.getOwnerDetails());
        transport.setContact(transportDto.getContact());
        transport.setType(transportDto.getType());
        transport.setSeater(transportDto.getSeater());
        Vendor vendor = vendorRepo.findById(transportDto.getVendor())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "vendor not found"));
        transport.setVendorId(transportDto.getVendor());
        transport.setEmail(transportDto.getEmail());
        transport.setPassword(passwordEncoder.encode("12345678"));
        transport.setRole("Transport");
        City city = cityRepo.findById(transportDto.getCity())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));
        transport.setCity(city);
        transport.setStatus(TransportStatus.AVAILABLE.getLabel());
        transport = transportRepo.save(transport);
        if (vendor.getTransport() == null) {
            vendor.setTransport(new ArrayList<>());
        }
        vendor.getTransport().add(transport);
        vendorRepo.save(vendor);
        return transport;
    }

    @Override
    public List<Transport> getAllTransports() {
        return transportRepo.findAll();
    }

    @Override
    public Transport validateLoginCredentials(Login login) {
        return transportRepo.findByTransportId(login.getUserId())
                .filter(transport -> passwordEncoder.matches(login.getPassword(), transport.getPassword()))
                .orElse(null);
    }

    @Override
    public Transport updateTransport(String id, @Valid TransportDto transportDto) {

        Transport existingTransport = transportRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transport not found"));

        existingTransport.setTransportId(transportDto.getTransportId());
        existingTransport.setVehicleNo(transportDto.getVehicleNo());
        existingTransport.setOwnerDetails(transportDto.getOwnerDetails());
        existingTransport.setContact(transportDto.getContact());
        existingTransport.setType(transportDto.getType());
        existingTransport.setSeater(transportDto.getSeater());
        existingTransport.setEmail(transportDto.getEmail());
        existingTransport.setVendorId(transportDto.getVendor());
        City city = cityRepo.findById(transportDto.getCity())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));
        existingTransport.setCity(city);
        return transportRepo.save(existingTransport);
    }

    @Override
    public void deleteTransport(String id) {

        Transport transport = transportRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transport not found"));
        transportRepo.delete(transport);
    }

    @Override
    public List<Transport> getTransportByCity(String city) {
        return transportRepo.getTransportByCity(city, mongoTemplate);
    }

    @Override
    public void sendForgotPasswordLink(String transportId) throws MessagingException {
        Optional<Transport> optionalTransport = transportRepo.findByTransportId(transportId);
        Transport transport = optionalTransport.get();
        if (transport == null){
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found with email");
        }

        String token = jwtHelper.generateJWTTokenForResetPassword(transport.getEmail());
        String content = mailTemplateService.sendForgotPasswordLink(null, transport.getEmail(), token);
        mailService.sendMail(transport.getEmail(), "Reset Password", content);
        transportRepo.updateResetValue(transport.getEmail(), mongoTemplate);
    }


}
