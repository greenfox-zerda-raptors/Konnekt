package com.greenfoxacademy.responses;

import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.requests.BaseRequest;
import com.greenfoxacademy.requests.ForgotPasswordRequest;
import com.greenfoxacademy.service.UserService;

/**
 * Created by Viktor on 2017.02.06..
 */

public class PasswordResetErrorResponse extends ErrorResponse {

    public PasswordResetErrorResponse() {
    }

    public void addErrorMessagesAuth(AuthRequest request) {
        if (userService.findUserByEmail(request.getEmail()) == null) {
            errors.add(new Error("User error", "No registered user with the given email found!"));
        }
        if (userService.oneOfPasswordsIsNull(request)) {
            errors.add(new Error("Form submission error", "All fields must be submitted."));
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
