package com.avitam.bankloanapplication.model.dto;

import com.avitam.bankloanapplication.model.entity.Role;
import lombok.*;

import java.math.BigDecimal;
import java.net.Inet4Address;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CustomerDto extends CommonDto {
    private String fullName;
    private String email;
    private String password;
    private String address;
    private Integer age;
    private String referenceContacts;
    private String panNumber;
    private String aadhaarNumber;
    private String dateOfBirth;
    private Set<Role> roles;
    private String phone;
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
    private String gender;
    private String emailOTP;
    private String token;
    private boolean isNew;
    private String referralCode;
    private String mobileOTP;
    private String profileVerificationIndex;
    private boolean profileVerificationStatus;
}
