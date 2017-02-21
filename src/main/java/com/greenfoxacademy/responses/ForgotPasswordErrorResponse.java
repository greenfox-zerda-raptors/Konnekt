package com.greenfoxacademy.responses;

import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.requests.BaseRequest;
import com.greenfoxacademy.requests.ForgotPasswordRequest;

/**
 * Created by posam on 2017-02-20.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */

public class ForgotPasswordErrorResponse extends ErrorResponse {

    public <T extends BaseRequest> void addErrorMessages(T request) {
        if (userService.findUserByEmail(request.getEmail()) == null) {
            errors.add(new Error("User error", "No registered user with the given email found!"));
        }
    }
}
