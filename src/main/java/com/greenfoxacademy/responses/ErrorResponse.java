package com.greenfoxacademy.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.service.UserService;

import java.util.ArrayList;

/**
 * Created by BSoptei on 2/1/2017.
 */
@JsonSerialize
public abstract class ErrorResponse {
    @JsonProperty
    protected ArrayList<Error> errors = new ArrayList<>();

    protected UserService userService;

    public ErrorResponse(UserService userService){
        this.userService = userService;
    }

    public abstract void addErrorMessages(AuthRequest request);
}
