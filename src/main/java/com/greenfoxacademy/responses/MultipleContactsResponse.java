package com.greenfoxacademy.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greenfoxacademy.domain.Contact;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * Created by Lenovo on 2/2/2017.
 */
@JsonSerialize
public class MultipleContactsResponse {
    @JsonProperty
    private Integer count;
    @JsonProperty
    private List<Contact> contacts;

    public MultipleContactsResponse(List<Contact>contacts){
        this.contacts = contacts;
        this.count = contacts.size();
    }

}
