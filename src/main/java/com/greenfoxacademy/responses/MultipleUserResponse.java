package com.greenfoxacademy.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greenfoxacademy.bodies.UserAdminBody;
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
    private List<UserAdminBody> users = new ArrayList<UserAdminBody>();

    public MultipleUserResponse(List<User> users) {
        for (User u : users) {
            this.users.add(new UserAdminBody(u));
        }
        this.count = users.size();
    }

}

