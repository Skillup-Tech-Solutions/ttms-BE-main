package com.example.TTMS.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.TTMS.dto.ApiResponse;
import com.example.TTMS.dto.VendorDTO;
import com.example.TTMS.entity.Vendor;
import com.example.TTMS.service.VendorService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/vendor")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService){
        this.vendorService = vendorService;
    }

    @PostMapping()
    public ApiResponse<Vendor> addVendor(@Valid @RequestBody VendorDTO vendorDto){
        return ApiResponse.success("Vendor added successfully",vendorService.addVendor(vendorDto));
    }
    
    @GetMapping()
    public ApiResponse<List<Vendor>> getAllVendors() {
        return ApiResponse.success(vendorService.getAllVendors());
    }

    @GetMapping("/{id}")
    public ApiResponse<Vendor> getVendorById(@PathVariable String id) {
        return ApiResponse.success(vendorService.getVendorById(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<Vendor> updateVendor(@PathVariable String id, @Valid@RequestBody VendorDTO vendorDto) {
        return ApiResponse.success("Vendor updated successfully", vendorService.updateVendor(id, vendorDto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteVendor(@PathVariable String id) {
        vendorService.deleteVendor(id);
        return ApiResponse.success("Vendor deleted successfully", null);
    }

    @GetMapping("/city/location")
    public ApiResponse<List<Vendor>> getVendorsByCityAndLocation(@RequestParam String cityId, @RequestParam List<String> locationIds) {
        return ApiResponse.success(vendorService.getVendorsByCityAndLocation(cityId, locationIds));
    }
    
}
