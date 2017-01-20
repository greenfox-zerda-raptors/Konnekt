package com.greenfoxacademy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 * Created by Lenovo on 1/20/2017.
 */
@Controller
public class RegistrationController {
    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

//    @PostMapping("/register")
//    public String register(@RequestParam("userName") String userName,
//                           @RequestParam("userPassword")  String userPassword) throws IOException {
//        User newUser = new User(userName,userPassword);
//        userService.save(newUser);
//        return "redirect:/";
//    }

    @PostMapping("/register")
    public String register(@RequestBody String profileJson) throws IOException {

        User newUser = new ObjectMapper().readValue(profileJson, User.class);
        newUser.setUserRole("USER");
        newUser.setEnabled(true);
        userService.save(newUser);
        return "redirect:/";
    }
}
