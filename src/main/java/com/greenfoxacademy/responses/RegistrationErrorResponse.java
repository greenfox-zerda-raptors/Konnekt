package com.greenfoxacademy.responses;

import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.requests.BaseRequest;
import com.greenfoxacademy.service.UserService;

import java.util.ArrayList;

/**
 * Created by BSoptei on 2/1/2017.
 */

public class RegistrationErrorResponse extends ErrorResponse {

    public RegistrationErrorResponse() {
    }

    public void addErrorMessagesAuth(AuthRequest request) {
        if (userService.oneOfRegistrationFieldsIsNull(request)) {
            errors.add(new Error("Form submission error","All fields must be submitted."));
        } else {
            addErrorMessagesForNonNullCases(request);
        }
    }

    private void addErrorMessagesForNonNullCases(AuthRequest request){
        if (userService.userExists(request.getEmail())) {
            errors.add(new Error("User error", "User already exists."));
        }
        if (!userService.passwordsMatch(request)) {
            errors.add(new Error("Password confirmation error", "Passwords do not match."));
        }
    }

    @Override
    public <T extends BaseRequest> void addErrorMessages(T request) {
        if (request instanceof AuthRequest) {
            addErrorMessagesAuth((AuthRequest) request);
        }
    }

}
