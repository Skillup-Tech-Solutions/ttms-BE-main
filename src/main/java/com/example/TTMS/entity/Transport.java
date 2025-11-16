package com.example.TTMS.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Document(collection = "transports")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transport {

    @Id
    private String id;
    private String transportId;
    private String vehicleNo;
    private String ownerDetails;
    private String contact;
    private String email;
    private String type;
    private int seater;
    private String vendorId;
    private String password;
    private String role;
    @DBRef
    private City city;
    private String status;
    private boolean isforgot;
    private LocalDateTime expiryDate;

}
