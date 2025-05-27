package com.avitam.bankloanapplication.web.controllers;

import com.avitam.bankloanapplication.model.entity.Customer;
import com.avitam.bankloanapplication.model.entity.User;
import com.avitam.bankloanapplication.repository.CustomerRepository;
import com.avitam.bankloanapplication.repository.UserRepository;
import com.avitam.bankloanapplication.tokenGeneration.JWTUtility;
import com.avitam.bankloanapplication.tokenGeneration.JwtRequest;
import com.avitam.bankloanapplication.tokenGeneration.JwtResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
public class TokenGenerationController extends BaseController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTUtility jwtUtility;
    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    UserRepository userRepository;

    @PostMapping("/api/authenticate")
    public JwtResponse authenticate(@RequestBody JwtRequest jwtRequest) {
        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());
        final String token = jwtUtility.generateToken(userDetails);
        Customer customer = customerRepository.findByEmail(jwtRequest.getUsername());
        User user = userRepository.findByUsername(jwtRequest.getUsername());
        return new JwtResponse(token, customer, user);
    }
}
