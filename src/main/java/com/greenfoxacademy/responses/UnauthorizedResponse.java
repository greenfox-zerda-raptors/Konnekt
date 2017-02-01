package com.greenfoxacademy.responses;

import com.sun.deploy.net.HttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.net.URI;

/**
 * Created by Lenovo on 2/1/2017.
 */
public class UnauthorizedResponse extends CustomServerResponse {

    @Override
    public ResponseEntity generateResponse() {
        return new ResponseEntity<>("", createResponseHeaders(), HttpStatus.UNAUTHORIZED);
    }

}
