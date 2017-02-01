package com.greenfoxacademy.responses;

import com.google.gson.Gson;
import com.greenfoxacademy.domain.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.HashMap;

/**
 * Created by BSoptei on 2/1/2017.
 */
public class SuccessfulLoginAndRegistrationResponse extends CustomServerResponse {
    private String token;
    private User user;

    public SuccessfulLoginAndRegistrationResponse(String token, User user){
        this.token = token;
        this.user = user;
    }

    @Override
    public ResponseEntity generateResponse() {
        HashMap <String, Long> body = new HashMap<>();
        body.put("user_id", user.getId());
        Gson gson = new Gson();
        return new ResponseEntity<>(body, createResponseHeaders(), HttpStatus.CREATED);
    }

    @Override
    protected HttpHeaders createResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        URI location = URI.create("https://raptor-konnekt.herokuapp.com");
        responseHeaders.setLocation(location);
        responseHeaders.set("session_token",token);
        return responseHeaders;
    }
}
