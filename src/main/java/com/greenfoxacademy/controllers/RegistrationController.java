package com.greenfoxacademy.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.service.HttpServletService;
import com.greenfoxacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by JadeTeam on 1/20/2017. Register new user
 */
@Controller
public class RegistrationController {
    private final UserService userService;
    private HttpServletService servletService;

    @Autowired
    public RegistrationController(UserService userService, HttpServletService servletService) {
        this.userService = userService;
        this.servletService = servletService;
    }

    @GetMapping("/register")
    @ResponseBody
    public String registerGet() {
        return "";
    }

    @PostMapping("/register")
    public ResponseEntity registerPost(@RequestBody String regFormData) throws IOException {
        JsonNode registrationJson = new ObjectMapper().readValue(regFormData, JsonNode.class);
        User newUser = userService.createUser(registrationJson);
        String passwordConfirmation = registrationJson.get("passwordConfirmation").textValue();
        if (registrationIsValid(newUser, passwordConfirmation)) {
            userService.save(newUser);
            return servletService.createResponseEntity("user created", "success", HttpStatus.CREATED);
        } else {
            return servletService.createResponseEntity("cannot register user", "error", HttpStatus.BAD_REQUEST);
        }
    }

    private boolean registrationIsValid(User newUser, String passwordConfirmation) {
        return !userService.userExists(newUser.getUserName())
                && passwordConfirmation.equals(newUser.getUserPassword());
    }

}
