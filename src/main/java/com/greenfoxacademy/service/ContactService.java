package com.greenfoxacademy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.greenfoxacademy.domain.Contact;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    private User obtainUserIdByName(String userName) {
        return userService.findUserByName(userName);
    }

    public Contact createNewContact(JsonNode newContactJson) {
        Contact contact = new Contact();
        contact.setContactName(newContactJson.get("contactName").textValue());
        contact.setContactDescription(newContactJson.get("contactDescription").textValue());
        contact.setUser(obtainUserIdByName(obtainUserNameFromSecurity()));
        return contact;
    }

    public boolean newContactIsValid(Contact contact) {
        return contact.getContactName() != null && contact.getContactDescription() != null;
    }

    public void saveNewContact(Contact newContact) {
        contactRepository.save(newContact);
    }

    public List<Contact> obtainAllContacts() {
        return contactRepository.findAll();
    }

    public void deleteContact(Long id) {
        contactRepository.delete(id);
    }

    public boolean contactBelongsToUser(Long id) {
        return contactRepository.findOne(id).getUser().getUserName().equals(obtainUserNameFromSecurity());
    }

}
