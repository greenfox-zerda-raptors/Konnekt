package com.greenfoxacademy.controllers;

import com.greenfoxacademy.constants.Valid;
import com.greenfoxacademy.domain.Session;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.responses.ErrorResponse;
import com.greenfoxacademy.responses.UserResponse;
import com.greenfoxacademy.service.SessionService;
import com.greenfoxacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by BSoptei on 1/31/2017. Login endpoint
 */
@BaseController
public class AuthenticationController {

    private UserService userService;
    private SessionService sessionService;

    @Autowired
    public AuthenticationController(UserService userService,
                                    SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthRequest request) throws IOException {
        ArrayList<Valid.issues>[] valid = userService.validateAuthRequest(request, Valid.login);
        return (userService.authRequestIsValid(valid)) ?
                generateSuccessfulLogin(request) :
                generateLoginError(valid);
    }


    @PostMapping("/logout")
    public ResponseEntity logout(@RequestHeader HttpHeaders headers) {
        sessionService.deleteSession(headers);
        return new ResponseEntity(HttpStatus.OK);
    }

    private ResponseEntity generateSuccessfulLogin(AuthRequest request) {
        User currentUser = userService.findUserByEmail(request.getEmail());
        Session currentSession = sessionService.createSession(currentUser);
        return new ResponseEntity<>(new UserResponse(currentUser.getId()),
                sessionService.generateHeadersWithToken(currentSession.getToken()),
                HttpStatus.CREATED);
    }

    private ResponseEntity generateLoginError(ArrayList<Valid.issues>[] issues) {
        return sessionService.showCustomResults(
                crateErrorResponse(issues),
                HttpStatus.UNAUTHORIZED);
    }

    private ErrorResponse crateErrorResponse(ArrayList<Valid.issues>[] issues) {
        ErrorResponse errorResponse = new ErrorResponse();
        return errorResponse.addErrorMessages(issues, ErrorResponse.AuthType.LOGIN);
    }
}