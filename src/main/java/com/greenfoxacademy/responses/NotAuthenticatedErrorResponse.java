package com.greenfoxacademy.responses;

import com.greenfoxacademy.requests.AuthRequest;

/**
 * Created by BSoptei on 2/2/2017.
 */
public class NotAuthenticatedErrorResponse extends ErrorResponse {
    public NotAuthenticatedErrorResponse(Error error) {
        super(error);
    }

    @Override
    public void addErrorMessages(AuthRequest request) {
    }
}
