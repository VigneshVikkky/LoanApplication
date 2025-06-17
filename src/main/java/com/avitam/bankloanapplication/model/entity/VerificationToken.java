package com.avitam.bankloanapplication.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Document("VerificationToken")
public class VerificationToken {

    public User user;
    private int id;
    private String userId;
    private String token;
    private int status;
    private Date ExpiryDate;
}
