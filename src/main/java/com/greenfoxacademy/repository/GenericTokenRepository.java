package com.greenfoxacademy.repository;

import com.greenfoxacademy.domain.GenericToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Created by posam on 2017-02-10.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */

@NoRepositoryBean
public interface GenericTokenRepository<T extends GenericToken, String extends Serializable> extends JpaRepository<T, String> {

    @Override
    T findOne(String token);
}
