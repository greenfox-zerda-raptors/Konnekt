package com.greenfoxacademy.controllers;

import com.greenfoxacademy.domain.Session;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.responses.BadRequestErrorResponse;
import com.greenfoxacademy.responses.Error;
import com.greenfoxacademy.responses.NotAuthenticatedErrorResponse;
import com.greenfoxacademy.responses.UserResponse;
import com.greenfoxacademy.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by BSoptei on 2/10/2017.
 */
public class CommonTasksHandler {

    public SessionService sessionService;
    public final BadRequestErrorResponse badFormat =
            new BadRequestErrorResponse(
                    new Error("Data error", "Data did not match required format."));
    public final NotAuthenticatedErrorResponse notAuthResponse =
            new NotAuthenticatedErrorResponse(
                    new Error("Authentication error", "Not authenticated"));

    @Autowired
    public CommonTasksHandler(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public int authIsSuccessful(HttpHeaders headers) {
        return sessionService.sessionIsValid(headers);
    }

    public ResponseEntity respondWithNotAuthenticated(int authResult) {
        notAuthResponse.addErrorMessages(authResult);
        return new ResponseEntity<>(notAuthResponse,
                sessionService.generateHeaders(),
                HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity respondWithBadRequest(Object response) {
        return new ResponseEntity<>(response,
                sessionService.generateHeaders(),
                HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity showCustomResults(Object body, HttpStatus status){
        return new ResponseEntity<>(body,
                sessionService.generateHeaders(),
                status);
    }

    public ResponseEntity showSuccessfulAuthResults(User user){
        Session currentSession = sessionService.createSession(user);
        return new ResponseEntity<>(new UserResponse(user.getId()),
                sessionService.generateHeadersWithToken(currentSession.getToken()),
                HttpStatus.CREATED);
    }

}
