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
public class RegistrationController extends CommonTasksHandler {
    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService,
                                  SessionService sessionService) {
        super(sessionService);
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody AuthRequest request) throws IOException {
        return (userService.registrationIsValid(request)) ?
                generateSuccessfulRegistration(request):
                generateRegistrationError(request);
    }

    private ResponseEntity generateSuccessfulRegistration(AuthRequest request) {
        return showSuccessfulAuthResults(userService.createUser(request));
    }

    private ResponseEntity generateRegistrationError(AuthRequest request) {
        return showCustomResults(createErrorResponse(request), HttpStatus.FORBIDDEN);
    }

    private RegistrationErrorResponse createErrorResponse(AuthRequest request) {
        RegistrationErrorResponse errorResponse =
                new RegistrationErrorResponse(userService);
        errorResponse.addErrorMessages(request);
        return errorResponse;
    }

}