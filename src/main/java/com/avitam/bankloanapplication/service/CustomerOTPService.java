package com.avitam.bankloanapplication.service;

import com.avitam.bankloanapplication.model.dto.CustomerDto;
import com.avitam.bankloanapplication.model.dto.CustomerWsDto;
import jakarta.mail.MessagingException;

public interface CustomerOTPService {

    CustomerWsDto sendEmailOtp(CustomerDto userDto) throws MessagingException;

    CustomerWsDto validateEmailOtp(CustomerDto userDto);

    CustomerWsDto saveUserName(CustomerDto userDto);

    CustomerWsDto   sendMobileOtp(CustomerDto userDto);

    CustomerWsDto validateMobileOtp(CustomerDto userDto);
}
