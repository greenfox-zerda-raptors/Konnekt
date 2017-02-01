package com.greenfoxacademy.responses;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.net.URI;

/**
 * Created by Lenovo on 2/1/2017.
 */
public abstract class  CustomServerResponse {

    abstract public ResponseEntity generateResponse();

    protected HttpHeaders createResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        URI location = URI.create("https://raptor-konnekt.herokuapp.com");
        responseHeaders.setLocation(location);
        return responseHeaders;
    }
}
