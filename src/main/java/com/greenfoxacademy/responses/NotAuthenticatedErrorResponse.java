package com.greenfoxacademy.responses;

import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.service.ForgotPasswordService;

/**
 * Created by BSoptei on 2/2/2017.
 */
public class NotAuthenticatedErrorResponse extends ErrorResponse {
    public NotAuthenticatedErrorResponse(ForgotPasswordService forgotPasswordService) {
        super(forgotPasswordService);
    }

    public NotAuthenticatedErrorResponse(Error error) {
        super(error);
    }

    public void addErrorMessages(int authResult) {
        switch (authResult) {
            case AuthCodes.SESSION_TOKEN_NOT_PRESENT:
                errors.add(new Error("Authentication error", "Session token is not present in request headers."));
                break;
            case AuthCodes.SESSION_TOKEN_NOT_REGISTERED:
                errors.add(new Error("Authentication error", "Session token not registered as active."));
                break;
            case AuthCodes.SESSION_TOKEN_EXPIRED:
                errors.add(new Error("Authentication error", "Session token expired."));
                break;
        }
    }

    @Override
    public void addErrorMessages(AuthRequest request) {
    }
}
