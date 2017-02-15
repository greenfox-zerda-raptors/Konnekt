package com.greenfoxacademy.responses;

import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.service.UserService;

/**
 * Created by Rita on 2017.02.13..
 */
public class LogoutErrorResponse extends ErrorResponse {

    public LogoutErrorResponse(UserService userService) {
        super(userService);
    }

    @Override
    public void addErrorMessages(AuthRequest request) {

    }
}
