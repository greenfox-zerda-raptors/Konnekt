package com.greenfoxacademy.controllers;

import lombok.Data;

/**
 * Created by Rita on 2017.01.31..
 */
@Data
public class TestContact {
    private String name;
    private String description;
    private Long user_id;

    public TestContact(String name, String description, Long user_id) {
        this.name = name;
        this.description = description;
        this.user_id = user_id;
    }
}
