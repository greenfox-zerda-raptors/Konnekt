package com.greenfoxacademy.repository;

import com.greenfoxacademy.domain.Session;

/**
 * Created by Lenovo on 1/31/2017.
 */
public interface SessionRepository extends GenericTokenRepository<Session, String> {

    @Override
    Session findOne(String token);
}