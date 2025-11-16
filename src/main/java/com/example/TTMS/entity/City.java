package com.example.TTMS.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "city")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class City {

    @Id
    private String id;
    private String cityId;
    private String cityName;
    @DBRef(lazy = true)
    private List<Location> locations;
}
