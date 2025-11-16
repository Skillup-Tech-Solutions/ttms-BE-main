package com.example.TTMS.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "vendor")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vendor {

    @Id
    private String id;
    private String vendorId;
    private String vendorName;
    private String address;
    private String email;
    private String mobile;
    private String password;
    private String role;
    @DBRef
    private City city;
    // @DBRef
    // private List<Location> locations;
    @DBRef
    private List<Transport> transport;
    private boolean isforgot;
    private LocalDateTime expiryDate;
    
}
