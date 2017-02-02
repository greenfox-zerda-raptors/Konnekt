package com.greenfoxacademy.responses;

import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.service.UserService;

/**
 * Created by BSoptei on 2/1/2017.
 */
public class LoginErrorResponse extends ErrorResponse {

    public LoginErrorResponse(UserService userService) {
        super(userService);
    }

    @Override
    public void addErrorMessages(AuthRequest request) {
        if (userService.emailOrPasswordIsNull(request)) {
            errors.add(new Error("Form submission error","All fields must be submitted."));
        } else {
            addErrorMessagesForNonNullCases(request);
        }
    }

    private void addErrorMessagesForNonNullCases(AuthRequest request){
        if (    !userService.userExists(request.getEmail())||
                !userService.passwordAndEmailMatch(request)) {
            errors.add(new Error("Authentication error", "Wrong email or password."));
        }
    }

}
