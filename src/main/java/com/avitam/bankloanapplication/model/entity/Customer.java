package com.avitam.bankloanapplication.model.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document("customer")

public class Customer extends BaseEntity{

    private String fullName;
    private Double monthlyIncome;
    private String gender;
    private Integer age;
    private String phone;
    private String address;
    private String email;
    private String referenceContacts;
    private String panNumber;
    private String aadhaarNumber;
    private String dateOfBirth;
    private String password;
    private String profileImage;
    private Set<Role> roles;
    private Integer loanScore = 500;
    private List<String> loanApplicationId;
    private List<String> customerList;
    private String resetPasswordToken;
    private String employmentType;
    private String bankAccNumber;
    private String bankIFSCCode;
    private String companyName;
    private String residenceType;
    private String workExperience;
    private String currentEMI;
    private String referralCode;
    private String mobileOTP;
    private String profileVerificationIndex;
    private boolean profileVerificationStatus;


}
