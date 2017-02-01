package com.greenfoxacademy.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.domain.Session;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.responses.SuccessfulLoginAndRegistrationResponse;
import com.greenfoxacademy.responses.UnauthorizedResponse;
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

    @Autowired
    public LoginController(UserService userService,
                           SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @PostMapping("/login")
    public ResponseEntity loginPost(@RequestBody String loginParameters) throws IOException {
        JsonNode loginJson = new ObjectMapper().readValue(loginParameters, JsonNode.class);
        if (userService.userLoginIsValid(loginJson)) {
            User currentUser = userService.findUserByEmail(loginJson.get("email").textValue());
            Session currentSession = sessionService.createSession(currentUser);
            sessionService.saveSession(currentSession);
            SuccessfulLoginAndRegistrationResponse success =
                    new SuccessfulLoginAndRegistrationResponse(currentSession.getToken(), currentUser);
            return success.generateResponse();
        } else {
            return new UnauthorizedResponse().generateResponse(HttpStatus.UNAUTHORIZED);
        }
    }
}
