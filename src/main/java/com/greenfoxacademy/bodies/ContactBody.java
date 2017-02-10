package com.greenfoxacademy.bodies;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greenfoxacademy.domain.Contact;
import com.greenfoxacademy.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by BSoptei on 2/10/2017.
 */
@Data
@JsonSerialize
public class ContactBody {
    @JsonProperty
    private Long id;
    @JsonProperty
    private User user;
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private String tags;

    public ContactBody(Contact contact) {
        this.id = contact.getId();
        this.user = contact.getUser();
        this.name = contact.getName();
        this.description = contact.getDescription();
        this.tags = contact.getTags().toString();
    }
}
