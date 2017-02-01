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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

/**
 * Created by BSoptei on 1/31/2017.
 */
@BaseController
public class LoginController {

    private final UserService userService;
    private final SessionService sessionService;
    private Gson gson;

    @Autowired
    public LoginController(UserService userService,
                           SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
        gson = new Gson();
    }

    @PostMapping("/login")
    public ResponseEntity loginPost(@RequestBody String loginParameters) throws IOException {
        JsonNode loginJson = new ObjectMapper().readValue(loginParameters, JsonNode.class);
        if (userService.userLoginIsValid(loginJson)) {
            User currentUser = userService.findUserByEmail(loginJson.get("email").textValue());
            Session currentSession = sessionService.createSession(currentUser);
            sessionService.saveSession(currentSession);
            UserResponse userResponse = new UserResponse(currentUser.getId());
            return new ResponseEntity<>(gson.toJson(userResponse),
                    sessionService.generateHeaders("session_token",currentSession.getToken()),
                    HttpStatus.CREATED);

        } else {
            return new ResponseEntity<>("", sessionService.generateHeaders("", ""), HttpStatus.UNAUTHORIZED);
        }
    }
}
