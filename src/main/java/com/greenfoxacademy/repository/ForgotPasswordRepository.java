package com.greenfoxacademy.repository;

import com.greenfoxacademy.domain.ForgotPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Viktor on 2017.02.06..
 */
public interface ForgotPasswordRepository extends JpaRepository<ForgotPasswordToken, String> {


}
