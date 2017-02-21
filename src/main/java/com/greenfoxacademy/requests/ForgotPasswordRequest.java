package com.greenfoxacademy.requests;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Viktor on 2017.02.06..
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ForgotPasswordRequest extends BaseRequest {
    private String email;

}
