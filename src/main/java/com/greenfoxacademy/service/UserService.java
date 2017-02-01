package com.greenfoxacademy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Created by JadeTeam on 1/19/2017. Communicates with UserRepository
 */

@Service
public class UserService {

//    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
    }

    public void save(User user) {
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPassword("12345");
        userRepository.save(user);
    }

    public boolean userExists(String email) {
        return findUserByEmail(email) != null;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findUserByName(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(JsonNode registrationJson) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(registrationJson, User.class);
    }

    public boolean registrationIsValid(User newUser) {
        return !userExists(newUser.getEmail())
                && emailIsValid(newUser.getEmail())
                && passwordsMatch(newUser);
    }

    private boolean emailIsValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean passwordsMatch(User newUser) {
        return newUser.getPasswordConfirmation().equals(newUser.getPassword());
    }

    public boolean userLoginIsValid(JsonNode loginJson) {
        String email = loginJson.get("email").textValue();
        return  userExists(email) &&
                findUserByEmail(email)
                .getPassword()
                .equals(loginJson.get("password").textValue());
    }
}
