package com.greenfoxacademy.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;

/**
 * Created by BSoptei on 2/1/2017.
 */
@JsonSerialize
@AllArgsConstructor
public class Error {
    @JsonProperty
    private String name;
    @JsonProperty
    private String message;

}
