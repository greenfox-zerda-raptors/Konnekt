package com.greenfoxacademy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.greenfoxacademy.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.greenfoxacademy.repository.*;

/**
 * Created by JadeTeam on 1/19/2017. Communicates with UserRepository
 */

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void save(User user){
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        userRepository.save(user);
    }

    public boolean userExists(String userName) {
        return userRepository.findByUserName(userName) != null;
    }

    public User createUser(JsonNode registrationJson){
        User newUser = new User();
        newUser.setUserName(registrationJson.get("username").textValue());
        newUser.setUserPassword(registrationJson.get("password").textValue());
        return newUser;
    }
}
