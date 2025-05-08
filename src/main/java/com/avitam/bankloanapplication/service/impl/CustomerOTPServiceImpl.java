package com.avitam.bankloanapplication.service.impl;


import com.avitam.bankloanapplication.model.dto.CustomerDto;
import com.avitam.bankloanapplication.model.dto.CustomerWsDto;
import com.avitam.bankloanapplication.model.entity.Customer;
import com.avitam.bankloanapplication.model.entity.OTP;
import com.avitam.bankloanapplication.repository.CustomerRepository;
import com.avitam.bankloanapplication.service.CustomerOTPService;
import com.avitam.bankloanapplication.service.CustomerService;
import com.avitam.bankloanapplication.tokenGeneration.JWTUtility;
import com.avitam.bankloanapplication.repository.OtpRepository;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomerOTPServiceImpl implements CustomerOTPService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRATION_MINUTES = 5;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 6;
    private static final String NUMBERS = "0123456789";
    private final ConcurrentHashMap<String, String> otpMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LocalDateTime> otpExpirationMap = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private JWTUtility jwtUtility;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private OtpRepository otpRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Value("${msg91.api.key}")
    private String apiKey;
    @Value("${msg91.sender.id}")
    private String senderId;
    @Value("${msg91.otp.expiration.minutes}")
    private int otpExpirationMinutes;

    @Autowired
    private CustomerService customerService;


    @Override
    public CustomerWsDto sendEmailOtp(CustomerDto customerDto) throws MessagingException {
        CustomerWsDto customerWsDto = new CustomerWsDto();
        String email = customerDto.getEmail();
        if (StringUtils.isEmpty(email)) {
            customerWsDto.setSuccess(false);
            customerWsDto.setMessage("Email is required.");
        }
        String otp = generateOtp();
        otpMap.put(email, otp);
        otpExpirationMap.put(email, LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES));

        sendEmail(email, otp);
        customerDto.setEmailOTP(otp);
        OTP otp1 = new OTP();
        otp1.setUserId(email);
        otp1.setEmailOtp(otp);
        otp1.setCreationTime(new Date());
        otpRepository.save(otp1);
        customerWsDto.setCustomerDtoList(List.of(customerDto));
        customerWsDto.setMessage("Otp sent successfully");
        return customerWsDto;
    }

    private void sendEmail(String recipientEmail, String otp) throws MessagingException {

        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");  // Protocol (SMTP)
        properties.put("mail.smtp.auth", "true");           // Enable authentication
        properties.put("mail.smtp.starttls.enable", "true"); // Use TLS
        properties.put("mail.smtp.host", "smtp.gmail.com");  // SMTP Host
        properties.put("mail.smtp.port", "587");             // SMTP Port
        properties.put("mail.smtp.ssl.trust", "*");
        // Create a MimeMessage
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);  // Use your email and app password
            }
        });

        // Create the message
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp + "\nIt will expire in 5 minutes.");

        // Send the email
        Transport.send(message);
    }

    private String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }


    @Override
    public CustomerWsDto validateEmailOtp(CustomerDto customerDto) {
        CustomerWsDto customerWsDto = new CustomerWsDto();
        List<CustomerDto> customerDtoList = new ArrayList<>();
        String email = customerDto.getEmail();
        String otp = customerDto.getOtp();

        boolean isOtpValid = otpMap.containsKey(email) &&
                otpMap.get(email).equals(otp) &&
                otpExpirationMap.containsKey(email) &&
                LocalDateTime.now().isBefore(otpExpirationMap.get(email));

        if (isOtpValid) {
            // Remove OTP after successful validation
            otpMap.remove(email);
            otpExpirationMap.remove(email);

            Customer existingUser = customerRepository.findByEmail(email);
            Customer existingUserByRecordId = customerRepository.findByRecordId(customerDto.getRecordId());

            if (existingUserByRecordId != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(existingUserByRecordId.getEmail());
                CustomerDto customerDto1 = modelMapper.map(existingUserByRecordId, CustomerDto.class);
                customerDto1.setToken(jwtUtility.generateToken(userDetails));
                customerDtoList.add(customerDto1);
            } else if (existingUser == null) {
                // Save the email in the database if not present
                CustomerDto newCustomerDto = new CustomerDto();
                newCustomerDto.setEmail(email);
                newCustomerDto.setStatus(true);
                Customer newCustomer = modelMapper.map(newCustomerDto, Customer.class);
                customerRepository.save(newCustomer);
                if (StringUtils.isEmpty(newCustomer.getRecordId())) {
                    newCustomer.setRecordId(String.valueOf(newCustomer.getId().getTimestamp()));
                }
                customerRepository.save(newCustomer);
                if (StringUtils.isEmpty(newCustomer.getReferralCode())) {
                    newCustomer.setReferralCode(newCustomer.getFullName());
                }
                customerRepository.save(newCustomer);
                newCustomerDto = modelMapper.map(newCustomer, CustomerDto.class);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                newCustomerDto.setToken(jwtUtility.generateToken(userDetails));
                newCustomerDto.setNew(true);
                customerDtoList.add(newCustomerDto);
            } else {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                CustomerDto customerDto1 = modelMapper.map(existingUser,CustomerDto.class);
                customerDto1.setToken(jwtUtility.generateToken(userDetails));
                customerDtoList.add(customerDto1);
            }
            customerWsDto.setSuccess(true);
            customerWsDto.setMessage("OTP validated successfully.");

        } else {
            customerWsDto.setSuccess(false);
            customerWsDto.setMessage("OTP is invalid or has expired.");
        }
        customerWsDto.setCustomerDtoList(customerDtoList);
        return customerWsDto;
    }


    public CustomerWsDto saveUserName(CustomerDto customerDto) {
        CustomerWsDto customerWsDto = new CustomerWsDto();
        String email = customerDto.getEmail();
        String username = customerDto.getName();
        Customer existingUser = StringUtils.isNotEmpty(email) ? customerRepository.findByEmail(email) : customerRepository.findByPhone(customerDto.getPhone());

        if (existingUser == null) {
            customerWsDto.setSuccess(false);
            customerWsDto.setMessage("User not found. Please validate OTP first.");
            return customerWsDto;
        }
        existingUser.setName(customerDto.getName());
        if (StringUtils.isEmpty(existingUser.getReferralCode())) {
            existingUser.setReferralCode(username);
        }
        existingUser.setCreationTime(new Date());
//        if (customerService.findByReferralCode(username) != null) {
//            customerWsDto.setSuccess(false);
//            customerWsDto.setMessage("UserName already present");
//            return customerWsDto;
//        }
        existingUser.setName(username);
//        if (StringUtils.isNotEmpty(customerDto.getReferralCode())) {
//            existingUser.setReferredBy(customerService.checkUserReferral(customerDto));
//            existingUser.setBonus(customerService.checkRefferdBy(customerDto));
//            customerRepository.save(existingUser);
//        }
        customerRepository.save(existingUser);
        customerWsDto.setCustomerDtoList(List.of(modelMapper.map(existingUser, CustomerDto.class)));
        customerWsDto.setSuccess(true);
        customerWsDto.setMessage("UserName saved successfully.");
        return customerWsDto;
    }

    @Override
    public CustomerWsDto sendMobileOtp(CustomerDto customerDto) {
        CustomerWsDto customerWsDto = new CustomerWsDto();
        String mobileNumber = customerDto.getPhone();
        if (StringUtils.isEmpty(mobileNumber)) {
            customerWsDto.setSuccess(false);
            customerWsDto.setMessage("Mobile number is required.");
            return customerWsDto;
        }

        String otp = generateOtp();
        otpMap.put(mobileNumber, otp);
        otpExpirationMap.put(mobileNumber, LocalDateTime.now().plusMinutes(otpExpirationMinutes));

        // Call MSG91 API to send OTP
        String url = "https://api.msg91.com/api/v5/otp?authkey=" + apiKey
                + "&mobile=" + mobileNumber
                + "&sender=" + senderId
                + "&otp=" + otp;

        try {
            restTemplate.getForObject(url, String.class);
            customerWsDto.setSuccess(true);
            customerWsDto.setMessage("OTP sent successfully.");
        } catch (Exception e) {
            customerWsDto.setSuccess(false);
            customerWsDto.setMessage("Failed to send OTP: " + e.getMessage());
        }
        return customerWsDto;
    }

    @Override
    public CustomerWsDto validateMobileOtp(CustomerDto customerDto) {
        List<CustomerDto> customerDtoList = new ArrayList<>();
        CustomerWsDto customerWsDto = new CustomerWsDto();
        String mobileNumber = customerDto.getPhone();
        String otp = customerDto.getOtp();

        boolean isOtpValid = otpMap.containsKey(mobileNumber) &&
                otpMap.get(mobileNumber).equals(otp) &&
                otpExpirationMap.containsKey(mobileNumber) &&
                LocalDateTime.now().isBefore(otpExpirationMap.get(mobileNumber));

        if (isOtpValid) {
            // Clear OTP after successful validation
            otpMap.remove(mobileNumber);
            otpExpirationMap.remove(mobileNumber);
            Customer existingUser = customerRepository.findByPhone(mobileNumber);
            if (existingUser == null) {
                // Save the email in the database if not present
                Customer newUser = new Customer();
                newUser.setPhone(mobileNumber);
                newUser.setStatus(true);
                customerRepository.save(newUser);
                if (newUser.getRecordId() == null) {
                    newUser.setRecordId(String.valueOf(newUser.getId().getTimestamp()));
                }
                customerRepository.save(newUser);
                customerDto = modelMapper.map(newUser, CustomerDto.class);
                UserDetails userDetails = userDetailsService.loadUserByUsername(mobileNumber);
                customerDto.setToken(jwtUtility.generateToken(userDetails));
                customerDto.setNew(true);
                customerDtoList.add(customerDto);
            }
            customerWsDto.setSuccess(true);
            customerWsDto.setMessage("OTP validated successfully.");
        } else {
            customerWsDto.setSuccess(false);
            customerWsDto.setMessage("OTP is invalid or has expired.");
        }
        customerWsDto.setCustomerDtoList(customerDtoList);
        return customerWsDto;
    }
}