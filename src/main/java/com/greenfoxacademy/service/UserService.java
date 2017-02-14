package com.greenfoxacademy.service;

import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.UserRepository;
import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.responses.UserRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by JadeTeam on 1/19/2017. Communicates with UserRepository
 */

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User user) {
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

    public User createUser(AuthRequest registrationRequest) {
        User newUser = new User();
        String rawPassword = registrationRequest.getPassword();
        newUser.setEmail(registrationRequest.getEmail());
        setUserPassword(newUser, encryptPassword(rawPassword));
        return newUser;
    }

    public boolean registrationIsValid(AuthRequest request) {
        return !oneOfRegistrationFieldsIsNull(request) &&
                !userExists(request.getEmail()) &&
                emailIsValid(request.getEmail()) &&
                passwordsMatch(request);
    }

    private boolean emailIsValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    public boolean passwordsMatch(AuthRequest request) {
        return request
                .getPassword()
                .equals(request.getPassword_confirmation());
    }

    public boolean userLoginIsValid(AuthRequest request) {
        return !emailOrPasswordIsNull(request) &&
                userExists(request.getEmail()) &&
                passwordAndEmailMatch(request);
    }

    public boolean passwordAndEmailMatch(AuthRequest request) {
        return findUserByEmail(request
                .getEmail())
                .getPassword()
                .equals(encryptPassword(request.getPassword())); // here you should hash the received pw
    }

    public User findUserById(Long userId) {
        return userRepository.findOne(userId);
    }

    public boolean emailOrPasswordIsNull(AuthRequest request) {
        return request.getEmail() == null ||
                request.getPassword() == null;
    }

    public boolean oneOfPasswordsIsNull(AuthRequest request) {
        return request.getPassword() == null ||
                request.getPassword_confirmation() == null;
    }

    public boolean oneOfRegistrationFieldsIsNull(AuthRequest request) {
        return emailOrPasswordIsNull(request) ||
                request.getPassword_confirmation() == null;
    }

    public String encryptPassword(String rawPassword) {
        try {
            byte[] bytesOfMessage = rawPassword.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(bytesOfMessage);
            BigInteger bigInt = new BigInteger(1, thedigest);
            return bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setUserPassword(User user, String password) {
        user.setPassword(password);
        save(user);
    }

    public boolean userIsAdmin(Long userId) {
        return userRepository.findOne(userId).getUserRole().equals(UserRoles.ADMIN);
    }
}