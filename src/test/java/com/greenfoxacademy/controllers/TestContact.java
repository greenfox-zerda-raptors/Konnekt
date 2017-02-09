package com.greenfoxacademy.controllers;

import lombok.Data;

/**
 * Created by Rita on 2017.01.31..
 */
@Data
public class TestContact {
    private String contact_name;
    private String contact_description;
    private Long user_id;

    public TestContact(String contact_name, String contact_description, Long user_id) {
        this.contact_name = contact_name;
        this.contact_description = contact_description;
        this.user_id = user_id;
    }
}
