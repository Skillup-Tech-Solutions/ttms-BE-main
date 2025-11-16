package com.example.TTMS.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.TTMS.dto.ApiResponse;
import com.example.TTMS.dto.TransportDto;
import com.example.TTMS.entity.Transport;
import com.example.TTMS.service.TransportService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transport")
public class TransportController {

    private final TransportService transportService;

    public TransportController(TransportService transportService){
        this.transportService = transportService;
    }

    @PostMapping()
    public ApiResponse<Transport> addTransport(@Valid @RequestBody TransportDto transport){
        return ApiResponse.success("Transport added successfully",transportService.addTransport(transport));
    }

    @GetMapping()
    public ApiResponse<List<Transport>> getAllTransports(){
        return ApiResponse.success(transportService.getAllTransports());
    }

    @PutMapping("/{id}")
    public ApiResponse<Transport> updateTransport(@PathVariable String id, @Valid @RequestBody TransportDto transportDto) {
        return ApiResponse.success("Transport updated successfully", transportService.updateTransport(id, transportDto));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteTransport(@PathVariable String id) {
        transportService.deleteTransport(id);
        return ApiResponse.success("Transport deleted successfully");
    }

    @GetMapping("city/{city}")
    public ApiResponse<List<Transport>> getTransportByCity(@PathVariable String city) {
        return ApiResponse.success(transportService.getTransportByCity(city));
    }
    
}
