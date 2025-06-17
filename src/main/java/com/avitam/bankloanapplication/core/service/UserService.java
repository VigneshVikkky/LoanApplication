package com.avitam.bankloanapplication.core.service;

import com.avitam.bankloanapplication.model.dto.UserDto;
import com.avitam.bankloanapplication.model.entity.User;
import com.avitam.bankloanapplication.model.entity.VerificationToken;

import java.util.List;


public interface UserService {


    UserDto save(UserDto request);

    List<User> getAll();

    void delete(String username);

    User findByUserName(String userName);

    User findByEmail(String email);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    void saveRegisteredUser(User user);

    String validateVerificationToken(String token);

    User getUser(String verificationToken);

    boolean isAdminRole();

    User getCurrentUser();

    boolean updateResetPasswordToken(String token, String email);

    User getByResetPasswordToken(String token);

    boolean updateOtp(String token, String email);

    void updatePassword(User user, String password);


//    List<User> getAll();
//
//    void delete(String username);
}
