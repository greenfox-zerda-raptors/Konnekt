package com.greenfoxacademy.repository;

import com.greenfoxacademy.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by JadeTeam on 1/19/2017.
 */

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserName(String userName);
}