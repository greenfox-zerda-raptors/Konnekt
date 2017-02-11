package com.greenfoxacademy.repository;

import com.greenfoxacademy.domain.ForgotPasswordToken;

/**
 * Created by Viktor on 2017.02.06..
 */

public interface ForgotPasswordRepository extends GenericTokenRepository<ForgotPasswordToken, String> {

    @Override
    ForgotPasswordToken findOne(String token);
}
