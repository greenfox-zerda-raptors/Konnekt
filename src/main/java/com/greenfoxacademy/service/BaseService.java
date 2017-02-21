package com.greenfoxacademy.service;

import com.greenfoxacademy.responses.Error;
import com.greenfoxacademy.responses.NotAuthenticatedErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;

/**
 * Created by BSoptei on 2/15/2017.
 */
@Service
public class BaseService {
    private SecureRandom random = new SecureRandom();

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

    public String generateToken() {
        return new BigInteger(130, random).toString(32);
    }
}
