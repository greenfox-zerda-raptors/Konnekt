package com.greenfoxacademy.controllers;

import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.SessionRepository;
import com.greenfoxacademy.repository.UserRepository;
import com.greenfoxacademy.responses.*;
import com.greenfoxacademy.responses.Error;
import com.greenfoxacademy.service.SessionService;
import com.greenfoxacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity showAllUsers(@RequestHeader HttpHeaders headers) {
        int authResult = sessionService.sessionIsValid(headers, true);
        if (authResult == AuthCodes.OK) {
            MultipleUserResponse multipleUserResponse = new MultipleUserResponse(userService.obtainAllUsers());
            return new ResponseEntity<>(multipleUserResponse,
                    sessionService.generateHeaders(),
                    HttpStatus.OK);
        } else {
            return respondWithUnauthorized(authResult);
        }


    }

    @GetMapping("users/{id}")
    public ResponseEntity showSingleUser(@RequestHeader HttpHeaders headers,
                                         @PathVariable("id") long id) {
        User user = userService.findUserById(id);
        int authResult = sessionService.sessionIsValid(headers, true);
        if (authResult == AuthCodes.OK) {
            if (user != null) {
                return new ResponseEntity<>(new UserAdminResponse(user),
                        sessionService.generateHeaders(),
                        HttpStatus.OK);
            } else {
                return respondWithItemNotFound();
            }
        } else {
            return respondWithUnauthorized(authResult);
        }
    }


    @PutMapping("users/{id}")
    public ResponseEntity editUser(@RequestHeader HttpHeaders headers,
                                   @RequestBody UserAdminResponse userAdminResponse,
                                   @PathVariable("id") long id) {
        int authResult = sessionService.sessionIsValid(headers, true);
        if (authResult == AuthCodes.OK) {
            return showEditingResults(userAdminResponse, userService.findUserById(id));
        } else {
            return respondWithUnauthorized(authResult);
        }
    }

    private ResponseEntity respondWithUnauthorized(int authResult) {
        NotAuthenticatedErrorResponse response = new NotAuthenticatedErrorResponse(userService);
        response.addErrorMessages(authResult);
        return new ResponseEntity<>(response,
                sessionService.generateHeaders(),
                HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity respondWithItemNotFound() {
        return new ResponseEntity<>(new ItemNotFoundErrorResponse(ItemNotFoundErrorResponse.USER),
                sessionService.generateHeaders(),
                HttpStatus.NOT_FOUND);
    }

    private ResponseEntity respondWithBadRequest() { //TODO this is the same as in contactcontroller
        BadRequestErrorResponse badRequestErrorResponse =
                new BadRequestErrorResponse(
                        new Error("Data error", "Data did not match required format."));
        return new ResponseEntity<>(badRequestErrorResponse,
                sessionService.generateHeaders(),
                HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity showEditingResults(UserAdminResponse userAdminResponse, User user) {
        return (userService.adminEditIsValid(userAdminResponse, user)) ?
                showEditingOKResults(userAdminResponse) :
                respondWithBadRequest();
    }

    private ResponseEntity showEditingOKResults(UserAdminResponse userAdminResponse) {
        try {
            userService.updateUser(userAdminResponse);
        } catch (Exception e) {
            return respondWithItemNotFound();
        }
        return new ResponseEntity<>(userAdminResponse,
                sessionService.generateHeaders(),
                HttpStatus.OK);
    }
}
