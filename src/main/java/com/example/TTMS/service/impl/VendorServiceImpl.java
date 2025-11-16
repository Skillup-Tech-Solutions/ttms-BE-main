package com.example.TTMS.service.impl;

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
import com.example.TTMS.dto.VendorDTO;
import com.example.TTMS.entity.City;
import com.example.TTMS.entity.Vendor;
import com.example.TTMS.repository.CityRepo;
import com.example.TTMS.repository.VendorRepo;
import com.example.TTMS.service.VendorService;

@Service
public class VendorServiceImpl implements VendorService {

    private final VendorRepo vendorRepo;
    private final CityRepo cityRepo;
    private final PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;
    private final JwtHelper jwtHelper;
    private final MailTemplateService mailTemplateService;
    private final MailService mailService;

    public VendorServiceImpl(VendorRepo vendorRepo, CityRepo cityRepo, PasswordEncoder passwordEncoder, MongoTemplate mongoTemplate, JwtHelper jwtHelper,
    MailTemplateService mailTemplateService, MailService mailService) {
        this.vendorRepo = vendorRepo;
        this.cityRepo = cityRepo;
        this.passwordEncoder = passwordEncoder;
        this.mongoTemplate = mongoTemplate;
        this.jwtHelper = jwtHelper;
        this.mailTemplateService = mailTemplateService;
        this.mailService = mailService;
    }

    @Override
    public Vendor addVendor(VendorDTO vendorDto) {

        Vendor vendor = new Vendor();
        vendor.setVendorId(vendorDto.getVendorId());
        vendor.setVendorName(vendorDto.getVendorName());
        vendor.setAddress(vendorDto.getAddress());
        vendor.setEmail(vendorDto.getEmail());
        vendor.setMobile(vendorDto.getMobile());
        vendor.setPassword(passwordEncoder.encode("12345678"));
        vendor.setRole("Vendor");
        City city = cityRepo.findById(vendorDto.getCity())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));
        vendor.setCity(city);
        return vendorRepo.save(vendor);
    }

    @Override
    public List<Vendor> getAllVendors() {
        return vendorRepo.findAll();
    }

    @Override
    public Vendor getVendorById(String vendorId) {

        return vendorRepo.findById(vendorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found"));
    }

    @Override
    public Vendor updateVendor(String id, VendorDTO vendorDto) {
        Vendor existing = vendorRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found"));
        City city = cityRepo.findById(vendorDto.getCity())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));
        existing.setVendorId(vendorDto.getVendorId());
        existing.setVendorName(vendorDto.getVendorName());
        existing.setAddress(vendorDto.getAddress());
        existing.setEmail(vendorDto.getEmail());
        existing.setMobile(vendorDto.getMobile());
        existing.setPassword(existing.getPassword());
        existing.setRole("Vendor");
        existing.setCity(city);

        return vendorRepo.save(existing);
    }

    @Override
    public void deleteVendor(String id) {
        if (!vendorRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found");
        }
        vendorRepo.deleteById(id);
    }

    @Override
    public Vendor validateLoginCredentials(Login login) {

        return vendorRepo.findByVendorId(login.getUserId())
                .filter(vendor -> passwordEncoder.matches(login.getPassword(), vendor.getPassword()))
                .orElse(null);
    }

    @Override
    public List<Vendor> getVendorsByCityAndLocation(String cityId, List<String> locationIds) {
        
        List<Vendor> vendors = vendorRepo.getVendorsByCityAndLocation(cityId, locationIds, mongoTemplate);
        if(vendors.isEmpty() || vendors == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transport not found for the locations");
        }
        return vendors;

        // return vendors.stream()
        //           .flatMap(v -> v.getTransport().stream())
        //           .toList();
    }

    @Override
    public void sendForgotPasswordLink(String vendorId) throws MessagingException {
        Optional<Vendor> vendorOptional = vendorRepo.findByVendorId(vendorId);
        Vendor vendor = vendorOptional.get();
        if(vendor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found");
        }
        String token = jwtHelper.generateJWTTokenForResetPassword(vendor.getEmail());
        String content = mailTemplateService.sendForgotPasswordLink(vendor.getVendorName(), vendor.getEmail(), token);
        mailService.sendMail(vendor.getEmail(), "Reset Password", content);
        vendorRepo.updateResetValue(vendor.getEmail(), mongoTemplate);
    }
}
