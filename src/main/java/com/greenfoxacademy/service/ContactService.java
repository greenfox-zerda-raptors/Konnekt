package com.greenfoxacademy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.greenfoxacademy.domain.Contact;
import com.greenfoxacademy.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Created by Jade Team on 2017.01.24..
 */
@Service
public class ContactService {

    private UserService userService;
    private ContactRepository contactRepository;

    @Autowired
    public ContactService(UserService userService, ContactRepository contactRepository) {
        this.userService = userService;
        this.contactRepository = contactRepository;
    }

    private String obtainUserNameFromSecurity() {
        return SecurityContextHolder.
                getContext().
                getAuthentication().
                getName();
    }

    private Long obtainUserIdByName(String userName) {
        return userService.findUserByName(userName).getId();
    }

    public Contact createNewContact(JsonNode newContactJson) {
        Contact contact = new Contact();
        contact.setContactName(newContactJson.get("contactName").textValue());
        contact.setContactDescription(newContactJson.get("contactDescription").textValue());
        contact.setUserId(obtainUserIdByName(obtainUserNameFromSecurity()));
        return contact;
    }

    public boolean newContactIsValid(Contact contact) {
        return contact.getContactName() != null && contact.getContactDescription() != null;
    }

    public void saveNewContact(Contact newContact) {
        contactRepository.save(newContact);
    }
}
