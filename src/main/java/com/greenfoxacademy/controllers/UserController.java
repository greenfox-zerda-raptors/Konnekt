package com.greenfoxacademy.controllers;

import com.greenfoxacademy.repository.SessionRepository;
import com.greenfoxacademy.repository.UserRepository;
import com.greenfoxacademy.responses.AuthCodes;
import com.greenfoxacademy.responses.MultipleUserResponse;
import com.greenfoxacademy.responses.NotAuthenticatedErrorResponse;
import com.greenfoxacademy.service.SessionService;
import com.greenfoxacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Created by posam on 2017-02-14.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */

@BaseController
public class UserController {

    private UserService userService;
    private UserRepository userRepository;
    private SessionRepository sessionRepository;
    private SessionService sessionService;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository, SessionRepository sessionRepository, SessionService sessionService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.sessionService = sessionService;
    }

    @GetMapping("/users")
    public ResponseEntity showallusers(@RequestHeader HttpHeaders headers) {
        int authResult = sessionService.sessionIsValid(headers, true);
        if (authResult == AuthCodes.OK) {
            MultipleUserResponse multipleUserResponse = new MultipleUserResponse(userService.obtainAllUsers());
            return new ResponseEntity<>(multipleUserResponse,
                    sessionService.generateHeaders(),
                    HttpStatus.OK);
        } else {
            NotAuthenticatedErrorResponse response = new NotAuthenticatedErrorResponse(userService);
            response.addErrorMessages(authResult);
            return new ResponseEntity<>(response,
                    sessionService.generateHeaders(),
                    HttpStatus.UNAUTHORIZED);
        }


    }

}
