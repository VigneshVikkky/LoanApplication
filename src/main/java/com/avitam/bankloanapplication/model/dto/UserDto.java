package com.avitam.bankloanapplication.model.dto;

import com.avitam.bankloanapplication.model.entity.Role;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDto extends CommonDto {
    private String username;
    private String email;
    private String password;
    private String gender;
    private String address;
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

