package com.greenfoxacademy.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

/**
 * Created by BSoptei on 2/1/2017.
 */

public class UnauthorizedResponse extends CustomServerResponse {

    public ResponseEntity generateResponse(HttpStatus status) {
        return new ResponseEntity<>("", createResponseHeaders(), status);
    }

    @Override
    public ResponseEntity generateResponse() {
        return null;
    }
}
