package com.avitam.bankloanapplication.model.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Document("User")
public class User extends BaseEntity {
    private String username;
    private String email;
    private String password;
    private String address;
    private String gender;
    private String referenceContacts;
    private String panNumber;
    private String aadhaarNumber;
    private String dateOfBirth;
    private Set<Role> roles;
    private String mobileNumber;
    private String profileImage;
    private String resetPasswordToken;
    private String employmentType;
    private BigDecimal monthlyIncome;
    private String bankAccNumber;
    private String bankIFSCCode;
    private String companyName;
    private String residenceType;
    private String workExperience;
    private String currentEMI;

}
