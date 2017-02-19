package com.greenfoxacademy.controllers;

import com.greenfoxacademy.bodies.UserAdminResponse;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.responses.AuthCodes;
import com.greenfoxacademy.service.SessionService;
import com.greenfoxacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by posam on 2017-02-14.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */

@BaseController
public class UserController {

    private UserService userService;
    private SessionService sessionService;

    @Autowired
    public UserController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @GetMapping("/users")
    public ResponseEntity showAllUsers(@RequestHeader HttpHeaders headers) {
        int authResult = sessionService.sessionIsValid(headers, true);
        if (authResult == AuthCodes.OK) {
            return userService.respondWithAllUsers();
        } else {
            return userService.respondWithUnauthorized(authResult);
        }


    }

    @GetMapping("/user/{id}")
    public ResponseEntity showSingleUser(@RequestHeader HttpHeaders headers,
                                         @PathVariable("id") long id) {
        int authResult = sessionService.sessionIsValid(headers, true);
        if (authResult == AuthCodes.OK) {
            return userService.respondWithFoundOrNotFound(userService.findUserById(id));
        } else {
            return userService.respondWithUnauthorized(authResult);
        }
    }

    @PutMapping("/user/{id}")
    public ResponseEntity editUser(@RequestHeader HttpHeaders headers,
                                   @RequestBody UserAdminResponse userAdminResponse,
                                   @PathVariable("id") long id) {
        int authResult = sessionService.sessionIsValid(headers, true);
        if (authResult == AuthCodes.OK) {
            User user = userService.findUserById(id);
            return userService.showEditingResults(userAdminResponse, user);
        } else {
            return userService.respondWithUnauthorized(authResult);
        }
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity deleteUser(@RequestHeader HttpHeaders headers,
                                     @PathVariable("id") long id) {
        int authResult = sessionService.sessionIsValid(headers, true);
        if (authResult == AuthCodes.OK) {
            return userService.showDeletingResults(userService.findUserById(id));
        } else {
            return userService.respondWithUnauthorized(authResult);
        }

    }


}
