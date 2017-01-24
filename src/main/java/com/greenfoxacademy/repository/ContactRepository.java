package com.greenfoxacademy.repository;

import com.greenfoxacademy.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Jade Team on 2017.01.24..
 */
public interface ContactRepository extends JpaRepository<Contact, Long> {
}
