package com.greenfoxacademy.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by Lenovo on 2/1/2017.
 */
public class SuccessfulLoginResponse extends CustomServerResponse {
    private String token;

    public SuccessfulLoginResponse(String token){
        this.token = token;
    }

    @Override
    public ResponseEntity generateResponse() {
        ResponseEntity successfulLogin = new ResponseEntity<>("", createResponseHeaders(), HttpStatus.OK);
        successfulLogin.getHeaders().set("token",token);
        return successfulLogin;
    }



}
