package com.greenfoxacademy.controllers;

import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.domain.Session;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.responses.LoginErrorResponse;
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
 * Created by BSoptei on 1/31/2017. Login endpoint
 */
@BaseController
public class LoginController {

    private SessionService sessionService;
    private UserService userService;

    @Autowired
    public LoginController(UserService userService,
                           SessionService sessionService) {
        this.sessionService = sessionService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthRequest request) throws IOException {
        return (userService.userLoginIsValid(request))?
                sessionService.generateSuccessfulLogin(request):
                sessionService.generateLoginError(request);
    }

}