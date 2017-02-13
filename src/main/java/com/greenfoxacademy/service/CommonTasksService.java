package com.greenfoxacademy.service;

import com.greenfoxacademy.responses.Error;
import com.greenfoxacademy.responses.NotAuthenticatedErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * Created by BSoptei on 2/13/2017.
 */
@Service
public class CommonTasksService {


    private final NotAuthenticatedErrorResponse notAuthResponse =
            new NotAuthenticatedErrorResponse(
                    new Error("Authentication error", "Not authenticated"));


    public ResponseEntity respondWithNotAuthenticated(int authResult) {
        notAuthResponse.addErrorMessages(authResult);
        return new ResponseEntity<>(notAuthResponse,
                generateHeaders(),
                HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity respondWithBadRequest(Object response) {
        return new ResponseEntity<>(response,
                generateHeaders(),
                HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity showCustomResults(Object body, HttpStatus status){
        return new ResponseEntity<>(body,
                generateHeaders(),
                status);
    }


    public HttpHeaders generateHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        URI location = URI.create("https://raptor-konnekt.herokuapp.com");
        responseHeaders.setLocation(location);
        return responseHeaders;
    }

    public HttpHeaders generateHeadersWithToken(String token) {
        HttpHeaders responseHeadersWithToken = generateHeaders();
        responseHeadersWithToken.set("session_token", token);
        return responseHeadersWithToken;
    }
}
