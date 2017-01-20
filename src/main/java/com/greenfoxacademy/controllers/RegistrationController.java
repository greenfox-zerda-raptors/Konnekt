package com.greenfoxacademy.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * Created by JadeTeam on 1/20/2017.
 */
@Controller
public class RegistrationController {
    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    @ResponseBody
    public String registerGet() {
        return "this is the registration page";
    }

    @PostMapping("/register")
    public String registerPost(@RequestBody String profileJson) throws IOException {
        User newUser = new User();
        JsonNode registrationJson = new ObjectMapper().readValue(profileJson, JsonNode.class);
        newUser.setUserName(registrationJson.get("userName").textValue());
        newUser.setUserPassword(registrationJson.get("userPassword").textValue());
        String passwordConfirmation = registrationJson.get("passwordConfirmation").textValue();
        if (!userService.userExists(newUser.getUserName())
                && passwordConfirmation.equals(newUser.getUserPassword())) {
            userService.save(newUser);
            return "redirect:/login";
        } else {
            return "redirect:/register";
        }
    }
}
