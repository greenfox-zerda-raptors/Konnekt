package com.greenfoxacademy.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by BSoptei on 2/1/2017.
 */
@JsonSerialize
public class UserResponse {
    @JsonProperty
    private Long user_id;

    public UserResponse(Long user_id){
        this.user_id = user_id;
    }
}