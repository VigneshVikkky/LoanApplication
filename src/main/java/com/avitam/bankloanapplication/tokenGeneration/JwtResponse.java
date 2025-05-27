package com.avitam.bankloanapplication.tokenGeneration;
import com.avitam.bankloanapplication.model.entity.Customer;

import com.avitam.bankloanapplication.model.entity.User;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String jwtToken;
    private Customer customer;
    private User user;
}
