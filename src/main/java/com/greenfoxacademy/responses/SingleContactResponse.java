package com.greenfoxacademy.responses;

import com.greenfoxacademy.domain.Contact;

/**
 * Created by Lenovo on 2/1/2017.
 */
public class SingleContactResponse {
    String name;
    String description;
    Long id;
    Long user_id;
    String user_email;

    public SingleContactResponse(Contact contact) {
        this.name = contact.getContactName();
        this.description = contact.getContactDescription();
        this.id = contact.getId();
        this.user_id = contact.getUser().getId();
        this.user_email = contact.getUser().getEmail();
    }
}
