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

    private final UserService userService;
    private final SessionService sessionService;

    @Autowired
    public LoginController(UserService userService,
                           SessionService sessionService) {
          this.userService = userService;
        this.sessionService = sessionService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthRequest request) throws IOException {
        return (userService.userLoginIsValid(request))?
                generateSuccessfulLogin(request):
                generateLoginError(request);
    }

    private ResponseEntity generateSuccessfulLogin(AuthRequest request) {
        User currentUser = userService.findUserByEmail(request.getEmail());
        Session currentSession = sessionService.createSession(currentUser);
        return new ResponseEntity<>(new UserResponse(currentUser.getId()),
                                    sessionService.generateHeadersWithToken(currentSession.getToken()),
                                    HttpStatus.CREATED);
    }

    private ResponseEntity generateLoginError(AuthRequest request) {
        return new ResponseEntity<>(crateErrorResponse(request),
                                    sessionService.generateHeaders(),
                                    HttpStatus.UNAUTHORIZED);
    }

    private LoginErrorResponse crateErrorResponse(AuthRequest request) {
        LoginErrorResponse errorResponse =
                new LoginErrorResponse(userService);
        errorResponse.addErrorMessages(request);
        return errorResponse;
    }
}