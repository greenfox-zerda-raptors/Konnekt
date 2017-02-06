package com.greenfoxacademy.service;

import com.greenfoxacademy.domain.Contact;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.ContactRepository;
import com.greenfoxacademy.requests.ContactRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
        return "0";
//                SecurityContextHolder.
//                getContext().
//                getAuthentication().
//                getName();
    }

    private User obtainUserByName(String userName) {
        return userService.findUserByName(userName);
    }

    public Contact createContact(ContactRequest contactRequest, Long contactId) {
        Contact contact = (contactId == null) ?
                new Contact() :
                contactRepository.findOne(contactId);
        contact.setContactName(contactRequest.getContact_name());
        contact.setContactDescription(contactRequest.getContact_description());
        contact.setUser(userService.findUserById(contactRequest.getUser_id()));
        return contact;
    }

    public boolean newContactIsValid(Contact contact) {
        return contact.getContactName() != null && contact.getContactDescription() != null;
    }

    public void saveNewContact(Contact newContact) {
        contactRepository.save(newContact);
    }

    public Contact findContactById(Long contactId) {
        return contactRepository.findOne(contactId);
    }

    public List<Contact> obtainAllContacts() {
        return contactRepository.findAll();
    }

    public void deleteContact(Long contactId) {
        contactRepository.delete(contactId);
    }

    public boolean contactBelongsToUser(Long contactId, Long userId) {
        return contactExists(contactId) &&
                contactIdMatchesUserId(contactId, userId);
    }

    private boolean contactIdMatchesUserId(Long contactId, Long userId) {
        return findContactById(contactId)
                .getUser()
                .getId()
                .equals(userId);
    }

    private boolean contactExists(Long contactId) {
        return findContactById(contactId) != null;
    }

    public List<Object[]> obtainMyContacts() {
        return contactRepository.findMyContacts(obtainCurrentUserId());
    }

    private Long obtainCurrentUserId() {
        return obtainUserByName(obtainUserNameFromSecurity()).getId();
    }

    public boolean contactRequestIsValid(ContactRequest contactRequest) {
        return contactRequest.getUser_id() != null &&
                contactRequest.getContact_name() != null &&
                contactRequest.getContact_description() != null;
    }

    public void emptyRepositoryBeforeTest() {
        contactRepository.deleteAll();
    }

    public Contact findContactByName(String contactName) {
        return contactRepository.findByContactName(contactName);
    }
}
