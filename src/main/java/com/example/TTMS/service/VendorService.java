package com.example.TTMS.service;

import java.util.List;

import com.example.TTMS.dto.Login;
import com.example.TTMS.dto.VendorDTO;
import com.example.TTMS.entity.Vendor;
import jakarta.mail.MessagingException;

public interface VendorService {

    Vendor addVendor(VendorDTO vendorDto);

    List<Vendor> getAllVendors();

    Vendor getVendorById(String vendorId);

    Vendor updateVendor(String id, VendorDTO vendorDto);

    void deleteVendor(String id);

    Vendor validateLoginCredentials(Login login);

    List<Vendor> getVendorsByCityAndLocation(String cityId, List<String> locationIds);

    void sendForgotPasswordLink(String userId) throws MessagingException;
}
