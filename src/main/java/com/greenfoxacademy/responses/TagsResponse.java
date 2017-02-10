package com.greenfoxacademy.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Created by BSoptei on 2/8/2017.
 */
@JsonSerialize
public class TagsResponse {
    @Getter
    @JsonProperty
    private ArrayList<String> tags =new ArrayList<String>();
}
