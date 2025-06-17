package com.avitam.bankloanapplication.model.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@NoArgsConstructor
@Document("KYC")
public class KYC extends BaseEntity {
    private String customerId;

    private String panNumber;

    private String aadharNumber;

    private Binary panImage;


}
