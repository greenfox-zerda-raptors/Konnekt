package com.greenfoxacademy.repository;

import com.greenfoxacademy.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Lenovo on 1/31/2017.
 */
public interface SessionRepository extends JpaRepository<Session, String>{
}
