package com.greenfoxacademy.service;

import com.google.gson.Gson;
import com.greenfoxacademy.controllers.CustomResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * Created by Jade Team on 2017.01.24..
 */

@Service
public class HttpServletService {

    private HttpHeaders createResponseHeaders(String headerValue) {
        HttpHeaders responseHeaders = new HttpHeaders();
        URI location = URI.create("localhost:8080");
        responseHeaders.setLocation(location);
        responseHeaders.set("status", headerValue);
        return responseHeaders;
    }

    public ResponseEntity createResponseEntity(String response,
                                               String headerValue,
                                               HttpStatus httpStatus) {
        Gson responseGson = new Gson();
        return new ResponseEntity<>
                (responseGson.toJson(new CustomResponse(response)),
                        createResponseHeaders(headerValue),
                        httpStatus);
    }
}
