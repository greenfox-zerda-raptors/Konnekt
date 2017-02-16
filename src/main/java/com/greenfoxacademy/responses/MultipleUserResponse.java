package com.greenfoxacademy.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greenfoxacademy.bodies.UserAdminResponse;
import com.greenfoxacademy.domain.User;

import java.util.ArrayList;
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
    private List<UserAdminResponse> users = new ArrayList<UserAdminResponse>();

    public MultipleUserResponse(List<User> users) {
        for (User u : users) {
            this.users.add(new UserAdminResponse(u));
        }
        this.count = users.size();
    }

}

