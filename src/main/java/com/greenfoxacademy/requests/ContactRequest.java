package com.greenfoxacademy.requests;

import lombok.Data;

/**
 * Created by Lenovo on 2/2/2017.
 */

@Data
public class ContactRequest {
    private Long user_id;
    private String name;
    private String description;
}
