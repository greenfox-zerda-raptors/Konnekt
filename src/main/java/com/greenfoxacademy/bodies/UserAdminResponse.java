package com.greenfoxacademy.bodies;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greenfoxacademy.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by posam on 2017-02-15.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */

@Data
@JsonSerialize
@NoArgsConstructor
public class UserAdminResponse {

    @JsonProperty
    private Long user_id;
    @JsonProperty
    private String email;
    @JsonProperty
    private String firstName;
    @JsonProperty
    private String lastName;
    @JsonProperty
    private String userRole;
    @JsonProperty
    private boolean enabled;


    public UserAdminResponse(User user) {
        this.user_id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.userRole = user.getUserRole();
        this.enabled = user.isEnabled();
    }
}
