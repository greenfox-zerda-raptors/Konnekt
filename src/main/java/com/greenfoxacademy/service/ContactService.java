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

//    private String obtainUserNameFromSecurity() {
//        return SecurityContextHolder.
//                getContext().
//                getAuthentication().
//                getName();
//    }

    private User obtainUserByName(String userName) {
        return userService.findUserByName(userName);
    }

    public Contact createNewContact(JsonNode newContactJson, Long userId) {
        Contact contact = new Contact();
        contact.setContactName(newContactJson.get("contactName").textValue());
        contact.setContactDescription(newContactJson.get("contactDescription").textValue());
        contact.setUser(obtainUserById(userId));
        return contact;
    }

    private User obtainUserById(Long userId) {
        return userService.findUserById(userId);
    }

    public boolean newContactIsValid(Contact contact) {
        return contact.getContactName() != null && contact.getContactDescription() != null;
    }

    public void saveNewContact(Contact newContact) {
        contactRepository.save(newContact);
    }

    public List<Object[]> obtainAllContacts() {
        return contactRepository.findAllContacts();
    }

    public void deleteContact(Long id) {
        contactRepository.delete(id);
    }

    public boolean contactBelongsToUser(Long contactId, Long userId) {
        return contactRepository.findOne(contactId).getUser().getId().equals(userId);
    }

    public List<Object[]> obtainMyContacts(Long userId) {
        return contactRepository.findMyContacts(userId);
    }

    private Long obtainCurrentUserId() {

//        return obtainUserByName(obtainUserNameFromSecurity()).getId();
        return 1L;
    }

    public void editContact(Long id, JsonNode newContactJson) {
        Contact contactToEdit = contactRepository.findOne(id);
        contactToEdit.setContactName(newContactJson.get("contactName").textValue());
        contactToEdit.setContactDescription(newContactJson.get("contactDescription").textValue());
        contactRepository.save(contactToEdit);
    }
}
