package com.greenfoxacademy.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greenfoxacademy.domain.User;

import java.util.List;

/**
 * Created by posam on 2017-02-14.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */
@JsonSerialize
public class MultipleUserResponse {
    @JsonProperty
    private Integer count;
    @JsonProperty
    private List<User> users;

    public MultipleUserResponse(List<User> users) {
        this.users = users;
        this.count = users.size();
    }

}

