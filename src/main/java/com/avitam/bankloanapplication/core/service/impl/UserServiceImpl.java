package com.avitam.bankloanapplication.core.service.impl;

import com.avitam.bankloanapplication.core.service.UserService;
import com.avitam.bankloanapplication.model.dto.UserDto;
import com.avitam.bankloanapplication.model.entity.Role;
import com.avitam.bankloanapplication.model.entity.User;
import com.avitam.bankloanapplication.model.entity.VerificationToken;
import com.avitam.bankloanapplication.repository.RoleRepository;
import com.avitam.bankloanapplication.repository.UserRepository;
import com.avitam.bankloanapplication.repository.VerificationTokenRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";
    public static final String ADMIN_USER = "/admin/user";
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Override
    public UserDto save(UserDto request) {
        User user;
        if (request.getRecordId() != null) {
            user = userRepository.findByRecordId(request.getRecordId());
            modelMapper.map(request, user);
            request.setMessage("Data updated successfully");
        } else {
            user = modelMapper.map(request, User.class);
            user.setCreationTime(new Date());
            userRepository.save(user);
        }
        if (StringUtils.isNotEmpty(user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
        if (request.getRecordId() == null) {
            user.setRecordId(String.valueOf(user.getId().getTimestamp()));
        }
        userRepository.save(user);
        request = (modelMapper.map(user, UserDto.class));
        request.setBaseUrl(ADMIN_USER);
        return request;
    }

    @Override
    public List<User> getAll() {
        return List.of();
    }

    @Override
    public void delete(String username) {

    }

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUsername(userName);
    }

    @Override
    public User findByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken();
        myToken.setToken(token);
        myToken.setUser(user);
        tokenRepository.save(myToken);
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    @Override
    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate()
                .getTime() - cal.getTime()
                .getTime()) <= 0) {
            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        // user.setStatus(true);
        tokenRepository.delete(verificationToken);
        userRepository.save(user);
        return TOKEN_VALID;
    }

    @Override
    public User getUser(final String verificationToken) {
        final VerificationToken token = tokenRepository.findByToken(verificationToken);
        if (token != null) {
            return token.getUser();
        }
        return null;
    }

    @Override
    public boolean isAdminRole() {

        Set<Role> roles = getCurrentUser().getRoles();
        if (CollectionUtils.isNotEmpty(roles)) {
            for (Role role : roles) {
                Role role1 = roleRepository.findByRecordId(role.getRecordId());
                if ("ROLE_ADMIN".equals(role1.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principalObject = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        if (userRepository.findByUsername(principalObject.getUsername()) != null) {
            return userRepository.findByUsername(principalObject.getUsername());
        }
        return userRepository.findByEmail(principalObject.getUsername());
    }


    public boolean updateResetPasswordToken(String token, String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            //user.setResetPasswordToken(token);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public User getByResetPasswordToken(String token) {

        return userRepository.findByResetPasswordToken(token);
    }

    @Override
    public boolean updateOtp(String token, String email) {
        return false;
    }


    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        //user.setResetPasswordToken(null);
        userRepository.save(user);
    }
//
//    @Override
//    public List<User> getAll() {
//        return List.of();
//    }
//
//    @Override
//    public void delete(String username) {
//
//    }

}