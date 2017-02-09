package com.greenfoxacademy.repository;

import com.greenfoxacademy.domain.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Jade Team on 2017.01.24..
 */
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findByUserId(Long id);

    @Query(value = "SELECT konnekt.contact.id, konnekt.user.user_name, konnekt.contact.contact_name, konnekt.contact.contact_description FROM konnekt.contact INNER JOIN konnekt.user ON konnekt.contact.user_id=konnekt.user.id", nativeQuery = true)
    List<Object[]> findAllContacts();

    @Query(value = "SELECT konnekt.contact.id, konnekt.user.user_name, konnekt.contact.contact_name, konnekt.contact.contact_description FROM konnekt.contact INNER JOIN konnekt.user ON konnekt.contact.user_id=konnekt.user.id WHERE konnekt.user.id = :id", nativeQuery = true)
    List<Object[]> findMyContacts(@Param("id") Long id);

    Contact findByName(String contactName);
}
