package com.greenfoxacademy.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.requests.BaseRequest;
import com.greenfoxacademy.service.ForgotPasswordService;
import com.greenfoxacademy.service.UserService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

/**
 * Created by BSoptei on 2/1/2017.
 */
@JsonSerialize
@NoArgsConstructor
public abstract class ErrorResponse {
    @JsonProperty
    protected ArrayList<Error> errors = new ArrayList<>();

    protected UserService userService;

    public ErrorResponse(Error error) {
        this.errors.add(error);
    }

    @Autowired
    public ErrorResponse(UserService userService) {
        this.userService = userService;
    }

    public abstract <T extends BaseRequest> void addErrorMessages(T request);
}
