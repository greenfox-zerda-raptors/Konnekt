package com.greenfoxacademy.service;

import com.greenfoxacademy.domain.Contact;
import com.greenfoxacademy.domain.Tag;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.ContactRepository;
import com.greenfoxacademy.requests.ContactRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by Jade Team on 2017.01.24..
 */
@Service
public class ContactService {

    private UserService userService;
    private ContactRepository contactRepository;
    private TagService tagService;

    @Autowired
    public ContactService(UserService userService,
                          ContactRepository contactRepository,
                          TagService tagService) {
        this.userService = userService;
        this.contactRepository = contactRepository;
        this.tagService = tagService;
    }

    private String obtainUserNameFromSecurity() {
        return SecurityContextHolder.
                getContext().
                getAuthentication().
                getName();
    }

    private User obtainUserByName(String userName) {
        return userService.findUserByName(userName);
    }

    // TODO refactor this method!!!
    public Contact createContact(ContactRequest contactRequest, Long contactId) {
        Contact contact = (contactId == null) ?
                new Contact() :
                contactRepository.findOne(contactId);
        contact.setName(contactRequest.getContact_name());
        contact.setDescription(contactRequest.getContact_description());
        contact.setUser(userService.findUserById(contactRequest.getUser_id()));
        contact.getTags().clear();

        String rawTags = contactRequest.getTags();
        if (rawTags != null && rawTags.length() > 0) {
            String[] tags = rawTags.split(",");
            for (int i = 0; i < tags.length; i++) {
                String currentTagName = tags[i].trim().toLowerCase();
                tags[i] = currentTagName;
                Tag currentTag;
                if (tagService.findByTagName(currentTagName) == null) {
                    currentTag = new Tag(currentTagName);
                } else {
                    currentTag = tagService.findByTagName(currentTagName);
                }
                tagService.saveTag(currentTag);
                if (!contact.getTags().contains(currentTag)) {
                    contact.getTags().add(currentTag);
                }
            }
        }
        return contact;
    }

    public boolean newContactIsValid(Contact contact) {
        return contact.getName() != null && contact.getDescription() != null;
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

    private Long obtainCurrentUserId() {
        return obtainUserByName(obtainUserNameFromSecurity()).getId();
    }

    public boolean contactRequestIsValid(ContactRequest contactRequest) {
        return contactRequest.getUser_id() != null &&
                contactRequest.getContact_name() != null &&
                contactRequest.getContact_description() != null;
    }

    public Set<Contact> findContactsByTag(Tag tag) {
        return tag.getContacts();
    }
}
