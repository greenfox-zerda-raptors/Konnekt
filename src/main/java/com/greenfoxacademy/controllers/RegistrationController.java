package com.greenfoxacademy.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.greenfoxacademy.domain.Session;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.responses.UserResponse;
import com.greenfoxacademy.service.SessionService;
import com.greenfoxacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by JadeTeam on 1/20/2017. Register new user
 */
@BaseController
public class RegistrationController {
    private final UserService userService;
    private SessionService sessionService;
    private Gson gson;

    @Autowired
    public RegistrationController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.gson = new Gson();
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
        if (userService.registrationIsValid(newUser)) {
            userService.save(newUser);
            Session currentSession = sessionService.createSession(newUser);
            sessionService.saveSession(currentSession);
            UserResponse userResponse = new UserResponse(newUser.getId());
            return new ResponseEntity<>(gson.toJson(userResponse),
                    sessionService.generateHeaders("session_token", currentSession.getToken()),
                    HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("", sessionService.generateHeaders("",""), HttpStatus.FORBIDDEN);
        }
    }

}
