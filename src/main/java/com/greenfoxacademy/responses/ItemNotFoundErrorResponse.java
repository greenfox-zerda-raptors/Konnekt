package com.greenfoxacademy.responses;

import com.greenfoxacademy.requests.AuthRequest;

/**
 * Created by posam on 2017-02-13.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */
public class ItemNotFoundErrorResponse extends ErrorResponse {
    public static final String CONTACT = "Contact";
    public static final String USER = "User";

    public ItemNotFoundErrorResponse(String missing) {
        super(new Error(String.format("%s error", missing), String.format("No %s with the specified ID found!", missing.toLowerCase())));

    }

    @Override
    public void addErrorMessages(AuthRequest request) {
    }
}
