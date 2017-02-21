package com.greenfoxacademy.responses;

import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.requests.BaseRequest;

/**
 * Created by BSoptei on 2/2/2017.
 */
public class BadRequestErrorResponse extends ErrorResponse {
    public BadRequestErrorResponse(Error error) {
        super(error);
    }

    public void addErrorMessagesAuth(AuthRequest request) {

    }

    @Override
    public <T extends BaseRequest> void addErrorMessages(T request) {
        if (request instanceof AuthRequest) {
            addErrorMessagesAuth((AuthRequest) request);
        }
    }
}
