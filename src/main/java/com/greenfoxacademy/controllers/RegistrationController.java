package com.greenfoxacademy.controllers;

import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.domain.Session;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.responses.RegistrationErrorResponse;
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
 * Created by JadeTeam on 1/20/2017. Register new user
 */
@BaseController
public class RegistrationController {
    private final UserService userService;
    private SessionService sessionService;

    @Autowired
    public RegistrationController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody AuthRequest request) throws IOException {
        return (userService.registrationIsValid(request)) ?
                generateSuccessfulRegistration(request):
                generateRegistrationError(request);
    }

    private ResponseEntity generateSuccessfulRegistration(AuthRequest request) {
        User newUser = userService.createUser(request);
        Session currentSession = sessionService.createSession(newUser);
        return new ResponseEntity<>(new UserResponse(newUser.getId()),
                                    sessionService.generateHeadersWithToken(currentSession.getToken()),
                                    HttpStatus.CREATED);
    }

    private ResponseEntity generateRegistrationError(AuthRequest request) {
        return new ResponseEntity<>(createErrorResponse(request),
                                    sessionService.generateHeaders(),
                                    HttpStatus.FORBIDDEN);
    }

    private RegistrationErrorResponse createErrorResponse(AuthRequest request) {
        RegistrationErrorResponse errorResponse =
                new RegistrationErrorResponse(userService);
        errorResponse.addErrorMessages(request);
        return errorResponse;
    }

}