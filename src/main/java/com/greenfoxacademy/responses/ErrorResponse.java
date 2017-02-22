package com.greenfoxacademy.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greenfoxacademy.constants.Valid;
import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.service.ForgotPasswordService;
import com.greenfoxacademy.service.UserService;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by BSoptei on 2/1/2017.
 */
@JsonSerialize
public abstract class ErrorResponse {
    @JsonProperty
    ArrayList<Error> errors = new ArrayList<>();
    
    private HashMap<Valid.issues, Error> messages = new HashMap<>();

    protected UserService userService;

    public ErrorResponse(UserService userService) {
        this.userService = userService;
    }

    public ErrorResponse(Error error) {
        this.errors.add(error);
    }

    public ErrorResponse() {
        messages.put(Valid.issues.INVALID, new Error("invalid placeholder", "invalid"));
        messages.put(Valid.issues.MISMATCH, new Error("mismatch placeholder", "mismatch"));
        messages.put(Valid.issues.NOT_FOUND, new Error("not found placeholder", "not found"));
        messages.put(Valid.issues.NOT_UNIQUE, new Error("not unique placeholder", "not unique"));
        messages.put(Valid.issues.UNAUTHORIZED, new Error("unauthorized placeholder", "unauthorized"));
    }

    public abstract void addErrorMessages(AuthRequest request);

    public void addErrorMessages(ArrayList<Valid.issues>[] issues) {
        for (ArrayList a : issues) {
            for (Valid.issues i : Valid.issues.values()) {
                if (a.contains(i)) {
                    errors.add(messages.get(i));
                }
            }
        }
    }
}
