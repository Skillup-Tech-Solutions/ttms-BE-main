package com.example.TTMS.config.db;

import java.util.Locale;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Collation;

import com.example.TTMS.entity.City;
import com.example.TTMS.entity.Location;
import com.example.TTMS.entity.Transport;
import com.example.TTMS.entity.User;
import com.example.TTMS.entity.Vendor;

import jakarta.annotation.PostConstruct;

@Configuration
@DependsOn("mongoTemplate")
public class Indexing {

        private final MongoTemplate mongoTemplate;

        public Indexing(MongoTemplate mongoTemplate) {
                this.mongoTemplate = mongoTemplate;
        }

        @PostConstruct
        void ensureIndexes() {
                IndexDefinition userIndex = new Index("userId", Sort.Direction.ASC).unique()
                                .collation(Collation.of(Locale.US).strength(2));
                mongoTemplate.indexOps(User.class).createIndex(userIndex);

                IndexDefinition userEmailIndex = new Index("email", Sort.Direction.ASC).unique()
                                .collation(Collation.of(Locale.US).strength(2));
                mongoTemplate.indexOps(User.class).createIndex(userEmailIndex);

                IndexDefinition userMobileIndex = new Index("mobileNo", Sort.Direction.ASC).unique()
                                .collation(Collation.of(Locale.US).strength(2));
                mongoTemplate.indexOps(User.class).createIndex(userMobileIndex);

                IndexDefinition cityIdIndex = new Index("cityId", Sort.Direction.ASC).unique()
                                .collation(Collation.of(Locale.US).strength(2));
                mongoTemplate.indexOps(City.class).createIndex(cityIdIndex);

                IndexDefinition cityNameIndex = new Index("cityName", Sort.Direction.ASC).unique()
                                .collation(Collation.of(Locale.US).strength(2));
                mongoTemplate.indexOps(City.class).createIndex(cityNameIndex);

                IndexDefinition locationIdIndex = new Index("locationId", Sort.Direction.ASC).unique()
                                .collation(Collation.of(Locale.US).strength(2));
                mongoTemplate.indexOps(Location.class).createIndex(locationIdIndex);

                IndexDefinition vendorIdIndex = new Index("vendorId", Sort.Direction.ASC).unique()
                                .collation(Collation.of(Locale.US).strength(2));
                mongoTemplate.indexOps(Vendor.class).createIndex(vendorIdIndex);

                IndexDefinition vendorAddressIndex = new Index("address", Sort.Direction.ASC).unique()
                                .collation(Collation.of(Locale.US).strength(2));
                mongoTemplate.indexOps(Vendor.class).createIndex(vendorAddressIndex);

                IndexDefinition vendorEmailIndex = new Index("email", Sort.Direction.ASC).unique()
                                .collation(Collation.of(Locale.US).strength(2));
                mongoTemplate.indexOps(Vendor.class).createIndex(vendorEmailIndex);

                IndexDefinition vendorMobileIndex = new Index("mobile", Sort.Direction.ASC).unique()
                                .collation(Collation.of(Locale.US).strength(2));
                mongoTemplate.indexOps(Vendor.class).createIndex(vendorMobileIndex);

                IndexDefinition vehicleIndex = new Index("vehicleNo", Sort.Direction.ASC).unique()
                                .collation(Collation.of(Locale.US).strength(2));
                mongoTemplate.indexOps(Transport.class).createIndex(vehicleIndex);

                IndexDefinition transportIdIndex = new Index("transportId", Sort.Direction.ASC).unique()
                                .collation(Collation.of(Locale.US).strength(2));
                mongoTemplate.indexOps(Transport.class).createIndex(transportIdIndex);

        }
}
