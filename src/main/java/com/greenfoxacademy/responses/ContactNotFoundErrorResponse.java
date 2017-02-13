package com.greenfoxacademy.responses;

import com.greenfoxacademy.requests.AuthRequest;

/**
 * Created by posam on 2017-02-13.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */
public class ContactNotFoundErrorResponse extends ErrorResponse {
    public ContactNotFoundErrorResponse() {
        super(new Error("Contact error", "No contact with the specified ID found!"));
    }

    @Override
    public void addErrorMessages(AuthRequest request) {
    }
}
