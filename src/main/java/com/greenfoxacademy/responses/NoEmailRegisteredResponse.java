package com.greenfoxacademy.responses;

import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.service.UserService;

/**
 * Created by Viktor on 2017.02.06..
 */

public class NoEmailRegisteredResponse extends ErrorResponse {

    public NoEmailRegisteredResponse(UserService userService) {
        super(userService);
        errors.add(new Error("User error","This email address does not exist!"));
    }

    @Override
    public void addErrorMessages(AuthRequest request) {

    }
}
