package com.greenfoxacademy.constants;

/**
 * Created by posam on 2017-02-22.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */
public final class Valid {

    public static final int REQUIRED = 1;
    public static final int NOT_REQUIRED = 2;
    public static final int UNIQUE = 3;
    public static final int AUTH = 4;
    public static final int MATCH = 5;

    public enum issues {
        NOT_FOUND,
        INVALID,
        NOT_UNIQUE,
        UNAUTHORIZED,
        MISMATCH,
        NULL
    }


    public static final int[] register = new int[]{UNIQUE, MATCH, MATCH};
    public static final int[] login = new int[]{AUTH, AUTH, NOT_REQUIRED};
    public static final int[] forgot = new int[]{REQUIRED, NOT_REQUIRED, NOT_REQUIRED};
    public static final int[] reset = new int[]{NOT_REQUIRED, MATCH, MATCH};
}
