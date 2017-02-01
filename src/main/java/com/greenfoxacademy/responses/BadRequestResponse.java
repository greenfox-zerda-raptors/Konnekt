package com.greenfoxacademy.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by Lenovo on 2/1/2017.
 */
public class BadRequestResponse extends CustomServerResponse {

    @Override
    public ResponseEntity generateResponse() {
        return new ResponseEntity<>("", createResponseHeaders(), HttpStatus.BAD_REQUEST);
    }
}
