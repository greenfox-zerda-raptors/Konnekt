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
public class LoginController extends CommonTasksHandler {

    private UserService userService;

    @Autowired
    public LoginController(UserService userService,
                           SessionService sessionService) {
        super(sessionService);
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthRequest request) throws IOException {
        return (userService.userLoginIsValid(request))?
                generateSuccessfulLogin(request):
                generateLoginError(request);
    }

    private ResponseEntity generateSuccessfulLogin(AuthRequest request) {
        return showSuccessfulAuthResults(userService.findUserByEmail(request.getEmail()));
    }

    private ResponseEntity generateLoginError(AuthRequest request) {
        return showCustomResults(crateErrorResponse(request), HttpStatus.UNAUTHORIZED);
    }

    private LoginErrorResponse crateErrorResponse(AuthRequest request) {
        LoginErrorResponse errorResponse =
                new LoginErrorResponse(userService);
        errorResponse.addErrorMessages(request);
        return errorResponse;
    }
}