package com.avitam.bankloanapplication.web.controllers.admin.customerOtp;


import com.avitam.bankloanapplication.model.dto.CustomerDto;
import com.avitam.bankloanapplication.model.dto.CustomerWsDto;
import com.avitam.bankloanapplication.service.CustomerOTPService;
import jakarta.mail.MessagingException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/otp")
public class CustomerOTPController {

    @Autowired
    private CustomerOTPService customerOTPService;


    @PostMapping("/send-otp")
    public CustomerWsDto sendOtp(@RequestBody CustomerDto customerDto) throws MessagingException {
        if (StringUtils.isNotEmpty(customerDto.getEmail())) {
            return customerOTPService.sendEmailOtp(customerDto);
        } else {
            return customerOTPService.sendMobileOtp(customerDto);
        }
    }

    @PostMapping("/validate-otp")
    public CustomerWsDto validateOtp(@RequestBody CustomerDto customerDto) {
        if (StringUtils.isNotEmpty(customerDto.getEmail())) {
            return customerOTPService.validateEmailOtp(customerDto);
        } else {
            return customerOTPService.validateMobileOtp(customerDto);
        }
    }

    @PostMapping("/save-userName")
    public CustomerWsDto saveUsername(@RequestBody CustomerDto customerDto) {
        return customerOTPService.saveUserName(customerDto);
    }
}
