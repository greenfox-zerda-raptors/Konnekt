package com.greenfoxacademy.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.domain.AuthenticatedUser;
import com.greenfoxacademy.domain.Session;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.service.HttpServletService;
import com.greenfoxacademy.service.SessionService;
import com.greenfoxacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

/**
 * Created by Lenovo on 1/31/2017.
 */
@BaseController
public class LoginController {

    private final UserService userService;
    private final HttpServletService httpServletService;
    private final SessionService sessionService;

    @Autowired
    public LoginController(UserService userService,
                           HttpServletService httpServletService,
                           SessionService sessionService) {
        this.userService = userService;
        this.httpServletService = httpServletService;
        this.sessionService = sessionService;
    }

    @PostMapping("/login")
    public ResponseEntity loginPost(@RequestBody String loginParameters) throws IOException {
        JsonNode loginJson = new ObjectMapper().readValue(loginParameters, JsonNode.class);
        if (userService.userLoginIsValid(loginJson)) {
            User currentUser = userService.findUserByEmail(loginJson.get("email").textValue());
            Session currentSession = sessionService.createSession(currentUser);
            sessionService.saveSession(currentSession);
            return httpServletService.createResponseEntity(currentUser.getEmail(), currentSession.getToken(), HttpStatus.OK);
        } else {
            return httpServletService.createResponseEntity("", "", HttpStatus.UNAUTHORIZED);
        }
    }
}
