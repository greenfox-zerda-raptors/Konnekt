package com.greenfoxacademy.controllers;

import lombok.Data;

/**
 * Created by Rita on 2017.01.31..
 */
@Data
public class TestContact {
    private String contactName;
    private String contactDescription;

    public TestContact(String contactName, String contactDescription) {
        this.contactName = contactName;
        this.contactDescription = contactDescription;
    }
}
