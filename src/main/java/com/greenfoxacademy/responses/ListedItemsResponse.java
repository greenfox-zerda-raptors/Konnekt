package com.greenfoxacademy.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Created by BSoptei on 2/1/2017.
 */
public class ListedItemsResponse extends CustomServerResponse {
    private String body;

    public ListedItemsResponse(String body){
        this.body = body;
    }

    @Override
    public ResponseEntity generateResponse() {
        return new ResponseEntity<>(body, createResponseHeaders(), HttpStatus.OK);

    }
}
