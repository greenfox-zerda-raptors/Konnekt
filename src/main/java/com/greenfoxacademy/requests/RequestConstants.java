package com.greenfoxacademy.requests;

/**
 * Created by posam on 2017-02-20.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */
public enum RequestConstants {

    FIELD_NULL (0),
    FIELD_OK (1),
    DB_SEARCH_RETURNED_NULL (-1),
    FIELD_MISMATCH (-2),
    FIELD_INVALID (-3),
    FIELD_NOT_REQUIRED (2);

    int errorCode;


    RequestConstants(int errorCode) {
        this.errorCode = errorCode;
    }
}
