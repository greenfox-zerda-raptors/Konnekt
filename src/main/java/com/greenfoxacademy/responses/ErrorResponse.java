package com.greenfoxacademy.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greenfoxacademy.constants.Valid;
import com.greenfoxacademy.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by BSoptei on 2/1/2017.
 */
@JsonSerialize
public class ErrorResponse {
    @JsonProperty
    ArrayList<Error> errors = new ArrayList<>();
    private static HashMap<Valid.issues, Error> registration = new HashMap<>();
    private static HashMap<Valid.issues, Error> forgotReset = new HashMap<>();
    private static HashMap<Valid.issues, Error> login = new HashMap<>();

    public enum AuthType {
        REGISTRATION (registration), RESET (forgotReset), LOGIN (login);

    private HashMap<Valid.issues, Error> messages = new HashMap<>();
    AuthType(HashMap<Valid.issues, Error> messages) {
        this.messages = messages;
    }
    }

    ErrorResponse(Error error) {
        this.errors.add(error);
    }

    public ErrorResponse() {
        registration.put(Valid.issues.NULL, new Error("Form submission error", "All fields must be submitted"));
        registration.put(Valid.issues.INVALID_EMAIL, new Error("Invalid email", "Please enter a valid email"));
        registration.put(Valid.issues.INVALID_PASS, new Error("Form submission error","Password must be at least 8 characters long."));
        registration.put(Valid.issues.MISMATCH, new Error("Password confirmation error", "Passwords do not match"));
        registration.put(Valid.issues.NOT_FOUND, new Error("not found placeholder", "not found"));
        registration.put(Valid.issues.NOT_UNIQUE, new Error("Email error", "User already registered with given email"));
        registration.put(Valid.issues.UNAUTHORIZED, new Error("unauthorized placeholder", "unauthorized"));
        forgotReset.put(Valid.issues.NULL, new Error("Form submission error", "All fields must be submitted"));
        forgotReset.put(Valid.issues.MISMATCH, new Error("Password confirmation error", "Passwords do not match"));
        forgotReset.put(Valid.issues.NOT_FOUND, new Error("User error", "No registered user with the given email found!"));
        login.put(Valid.issues.NULL, new Error("Form submission error","All fields must be submitted."));
        login.put(Valid.issues.UNAUTHORIZED, new Error("Authentication error", "Wrong email or password."));
    }

    public ErrorResponse addErrorMessages(ArrayList<Valid.issues>[] issues, AuthType authType) {
        for (int i = 0; i < issues.length; i++) {
            ArrayList a = issues[i];
            for (Valid.issues iss : Valid.issues.values()) {
                if (a.contains(iss)) {
                    errors.add(authType.messages.get(iss));
                }
            }
        }
        return this;
    }
}
