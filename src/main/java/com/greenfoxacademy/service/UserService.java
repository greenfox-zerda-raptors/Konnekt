package com.greenfoxacademy.service;

import com.greenfoxacademy.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.greenfoxacademy.repository.*;

/**
 * Created by JadeTeam on 1/19/2017.
 */

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User user){
        userRepository.save(user);
    }

    public boolean userExists(String userName) {
        return userRepository.findByUserName(userName) != null;
    }
}
