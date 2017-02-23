package com.greenfoxacademy.service;

import com.greenfoxacademy.bodies.ContactBody;
import com.greenfoxacademy.domain.Contact;
import com.greenfoxacademy.domain.Tag;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.ContactRepository;
import com.greenfoxacademy.requests.ContactRequest;
import com.greenfoxacademy.responses.BadRequestErrorResponse;
import com.greenfoxacademy.responses.Error;
import com.greenfoxacademy.responses.ItemNotFoundErrorResponse;
import com.greenfoxacademy.responses.MultipleContactsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;

/**
 * Created by Jade Team on 2017.01.24..
 */
@Service
public class ContactService extends BaseService {

    private UserService userService;
    private ContactRepository contactRepository;
    private TagService tagService;
    private final BadRequestErrorResponse badFormat =
            new BadRequestErrorResponse(
                    new Error("Data error", "Data did not match required format."));

    @Autowired
    public ContactService(UserService userService,
                          ContactRepository contactRepository,
                          TagService tagService) {
        this.userService = userService;
        this.contactRepository = contactRepository;
        this.tagService = tagService;
    }

    @PersistenceContext(name = "default")
    EntityManager em;

    private User obtainUserByName(String userName) {
        return userService.findUserByName(userName);
    }

    public Contact createContact(ContactRequest contactRequest, Long contactId) {
        Contact contact = (contactId == null) ?
                new Contact() : contactRepository.findOne(contactId);
        adjustContactProperties(contact, contactRequest); //TODO unnecessary double set
        contact.setName(contactRequest.getName());
        contact.setDescription(contactRequest.getDescription());
        return contact;
    }

    private boolean tagExists(String currentTagName) {
        return tagService.findByTagName(currentTagName) != null;
    }

    private void adjustContactProperties(Contact contact,
                                         ContactRequest contactRequest) {
        contact.setName(contactRequest.getName());
        contact.setDescription(contactRequest.getDescription());
        contact.setUser(userService.findUserById(contactRequest.getUser_id()));
        manageTags(contact, contactRequest);
    }

    private void manageTags(Contact contact, ContactRequest contactRequest) {
        contact.getTags().clear();
        String rawTags = contactRequest.getTags();
        if (tagsAreRelevant(rawTags)) {
            processRawTags(contact, rawTags);
        }
    }

    private void processRawTags(Contact contact, String rawTags) {
        String[] tags = rawTags.split(",");
        for (int i = 0; i < tags.length; i++) {
            String currentTagName = tags[i].trim().toLowerCase();
            tags[i] = currentTagName;
            if (currentTagName.length() > 0) {
                processCurrentTag(contact, currentTagName);
            }
        }
    }

    private void processCurrentTag(Contact contact, String tagName) {
        Tag currentTag = (tagExists(tagName)) ?
                tagService.findByTagName(tagName) : new Tag(tagName);
        tagService.saveTag(currentTag);
        if (!contact.getTags().contains(currentTag)) {
            contact.getTags().add(currentTag);
        }
    }

    private boolean tagsAreRelevant(String rawTags) {
        return rawTags != null && rawTags.length() > 0;
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

    public boolean contactBelongsToUserOrIsAdmin(Long contactId, Long userId) {
        return contactExists(contactId) &&
                (contactIdMatchesUserId(contactId, userId) ||
                        userService.userIsAdmin(userId));
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

    public boolean contactRequestIsValid(ContactRequest contactRequest) {
        return contactRequest.getUser_id() != null &&
                contactRequest.getName() != null &&
                contactRequest.getDescription() != null;
    }

    public Set<Contact> findContactsByTag(Tag tag) {
        return tag.getContacts();
    }

    public Contact findContactByName(String contactName) {
        return contactRepository.findByName(contactName);
    }

    public void emptyRepositoryBeforeTest() {
        contactRepository.deleteAll();
        em.createNativeQuery("ALTER SEQUENCE contact_id_seq RESTART WITH 1")
                .executeUpdate();
    }

    public ResponseEntity showAddingResults(ContactRequest contactRequest) {
        return (contactRequestIsValid(contactRequest)) ?
                showAddingOKResults(contactRequest) :
                respondWithBadRequest(badFormat);
    }

    private ResponseEntity showAddingOKResults(ContactRequest contactRequest) {
        Contact newContact = createContact(contactRequest, null);
        saveNewContact(newContact);
        return showCustomResults(newContact, HttpStatus.CREATED);
    }

    public ResponseEntity showContacts() {
        MultipleContactsResponse multipleContactsResponse =
                new MultipleContactsResponse(obtainAllContacts());
        return showCustomResults(multipleContactsResponse, HttpStatus.OK);
    }

    public ResponseEntity showDeletingResults(Long contactId, Long userId) {
        return (contactBelongsToUserOrIsAdmin(contactId, userId)) ?
                showDeletingOKResults(contactId) :
                respondWithBadRequest(badFormat);
    }

    private ResponseEntity showDeletingOKResults(Long contactId) {
        Contact contactToDelete = findContactById(contactId);
        ContactBody deletedContactInfo = new ContactBody(contactToDelete);
        deleteContact(contactId);
        return showCustomResults(deletedContactInfo, HttpStatus.OK);
    }

    public ResponseEntity showEditingResults(Long contactId,
                                             ContactRequest contactRequest,
                                             Long userId) {
        return (editingParametersAreValid(contactId, userId, contactRequest)) ?
                showEditingOKResults(contactId, contactRequest) :
                respondWithBadRequest(badFormat);
    }

    private ResponseEntity showEditingOKResults(Long contactId,
                                                ContactRequest contactRequest) {
        Contact updatedContact = createContact(contactRequest, contactId);
        saveNewContact(updatedContact);
        return showCustomResults(updatedContact, HttpStatus.OK);
    }

    private boolean editingParametersAreValid(Long contactId,
                                              Long userId,
                                              ContactRequest contactRequest) {
        return contactBelongsToUserOrIsAdmin(contactId, userId) &&
                contactRequestIsValid(contactRequest);
    }

    public ResponseEntity showSingleContact(Long contactId) {
        Contact singleContact = findContactById(contactId);
        if (singleContact != null) {
            return showCustomResults(singleContact,
                    HttpStatus.OK);
        } else {
            return showCustomResults(new ItemNotFoundErrorResponse(ItemNotFoundErrorResponse.CONTACT),
                    HttpStatus.NOT_FOUND);
        }
    }
}
