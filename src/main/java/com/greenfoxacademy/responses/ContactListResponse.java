package com.greenfoxacademy.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by BSoptei on 2/1/2017.
 */
public class ContactListResponse {
    private String contacts;

    public ContactListResponse(String contacts){
        this.contacts = contacts;
    }

}
