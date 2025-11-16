package com.example.TTMS.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "location")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    @Id
    private String id;
    private String city;
    private String cityName;
    private String locationId;
    private String locationName;

}
