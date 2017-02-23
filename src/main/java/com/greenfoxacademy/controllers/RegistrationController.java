package com.greenfoxacademy.controllers;

import com.greenfoxacademy.constants.Valid;
import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.service.SessionService;
import com.greenfoxacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;

/**
 * Created by JadeTeam on 1/20/2017. Register new user
 */
@BaseController
public class RegistrationController {
    private final UserService userService;
    private SessionService sessionService;

    @Autowired
    public RegistrationController(UserService userService,
                                  SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody AuthRequest request) {
        ArrayList<Valid.issues>[] valid = userService.validateAuthRequest(request, Valid.register);
        return (userService.authRequestIsValid(valid) ?
                sessionService.generateSuccessfulRegistration(request) :
                sessionService.generateRegistrationError(valid));
    }

}