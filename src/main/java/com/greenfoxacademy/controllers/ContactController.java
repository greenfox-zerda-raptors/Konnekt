package com.greenfoxacademy.controllers;

import com.greenfoxacademy.bodies.ContactBody;
import com.greenfoxacademy.domain.Contact;
import com.greenfoxacademy.requests.ContactRequest;
import com.greenfoxacademy.responses.*;
import com.greenfoxacademy.service.ContactService;
import com.greenfoxacademy.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Created by Jade Team on 2017.01.24.. Controller responsible for CRUD operations
 */

@BaseController
public class ContactController extends CommonTasksHandler {

    private ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService,
                             SessionService sessionService) {
        super(sessionService);
        this.contactService = contactService;
    }

    @PostMapping("/contacts")
    public ResponseEntity addNewContact(@RequestBody ContactRequest contactRequest,
                                        @RequestHeader HttpHeaders headers) throws Exception {
        int authResult = authIsSuccessful(headers);
        return (authResult == AuthCodes.OK) ?
                showAddingResults(contactRequest) :
                respondWithNotAuthenticated(authResult);
    }

    private ResponseEntity showAddingResults(ContactRequest contactRequest) {
        return (contactService.contactRequestIsValid(contactRequest)) ?
                showAddingOKResults(contactRequest) :
                respondWithBadRequest(badFormat);
    }

    private ResponseEntity showAddingOKResults(ContactRequest contactRequest) {
        Contact newContact = contactService.createContact(contactRequest, null);
        contactService.saveNewContact(newContact);
        return showCustomResults(newContact, HttpStatus.CREATED);
    }

    @GetMapping("/contacts")
    public ResponseEntity listAllContacts(@RequestHeader HttpHeaders headers) {
        int authResult = authIsSuccessful(headers);
        return (authResult == AuthCodes.OK) ?
                showContacts() :
                respondWithNotAuthenticated(authResult);
    }

    private ResponseEntity showContacts() {
        MultipleContactsResponse multipleContactsResponse =
                new MultipleContactsResponse(contactService.obtainAllContacts());
        return showCustomResults(multipleContactsResponse, HttpStatus.OK);
    }

    @DeleteMapping("/contact/{id}")
    public ResponseEntity deleteContact(@PathVariable("id") Long contactId,
                                        @RequestHeader HttpHeaders headers) {
        int authResult = authIsSuccessful(headers);
        return (authResult == AuthCodes.OK) ?
                showDeletingResults(contactId, headers) :
                respondWithNotAuthenticated(authResult);
    }

    private ResponseEntity showDeletingResults(Long contactId, HttpHeaders headers) {
        Long userId = obtainUserId(headers);
        return (contactService.contactBelongsToUser(contactId, userId)) ?
                showDeletingOKResults(contactId) :
                respondWithBadRequest(badFormat);
    }

    private ResponseEntity showDeletingOKResults(Long contactId) {
        Contact contactToDelete = contactService.findContactById(contactId);
        ContactBody deletedContactInfo = new ContactBody(contactToDelete);
        contactService.deleteContact(contactId);
        return showCustomResults(deletedContactInfo, HttpStatus.OK);
    }

    @PutMapping("/contact/{id}")
    public ResponseEntity editContact(@PathVariable("id") Long contactId,
                                      @RequestBody ContactRequest contactRequest,
                                      @RequestHeader HttpHeaders headers) throws Exception {
        int authResult = authIsSuccessful(headers);
        return (authResult == AuthCodes.OK) ?
                showEditingResults(contactId, headers, contactRequest) :
                respondWithNotAuthenticated(authResult);
    }

    private ResponseEntity showEditingResults(Long contactId,
                                              HttpHeaders headers,
                                              ContactRequest contactRequest) {
        Long userId = obtainUserId(headers);
        return (editingParametersAreValid(contactId, userId, contactRequest)) ?
                showEditingOKResults(contactId, contactRequest) :
                respondWithBadRequest(badFormat);
    }

    private ResponseEntity showEditingOKResults(Long contactId,
                                                ContactRequest contactRequest) {
        Contact updatedContact = contactService.createContact(contactRequest, contactId);
        contactService.saveNewContact(updatedContact);
        return showCustomResults(updatedContact, HttpStatus.OK);
    }

    private boolean editingParametersAreValid(Long contactId,
                                              Long userId,
                                              ContactRequest contactRequest) {
        return contactService.contactBelongsToUser(contactId, userId) &&
                contactService.contactRequestIsValid(contactRequest);
    }

    private Long obtainUserId(HttpHeaders headers) {
        return sessionService.obtainUserIdFromHeaderToken(headers);
    }

}
