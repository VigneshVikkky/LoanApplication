package com.avitam.bankloanapplication.core.service.impl;


import com.avitam.bankloanapplication.model.entity.Customer;
import com.avitam.bankloanapplication.model.entity.User;
import com.avitam.bankloanapplication.repository.CustomerRepository;
import com.avitam.bankloanapplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String inputString) {
        User user = userRepository.findByEmail(inputString);
        Customer customer1 = customerRepository.findByPhone(inputString);
        Customer customer2 = customerRepository.findByEmail(inputString);

        if ((user == null) && (customer1 == null || !customer1.getStatus()) && (customer2 == null)) {
            throw new UsernameNotFoundException(inputString);
        }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        if (user != null) {
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), grantedAuthorities);
        }
//        if (user2 != null) {
//            return new org.springframework.security.core.userdetails.User(user2.getUsername(), user2.getPassword(), grantedAuthorities);
//        }

        if (customer1 != null) {
            return new CustomUserDetails(
                    customer1.getPhone(),
                    customer1.getStatus(),
                    grantedAuthorities
            );
        }
        return new CustomUserDetails(customer2.getEmail(), customer2.getStatus(), grantedAuthorities);


    }

}

