package com.greenfoxacademy.controllers;

import com.greenfoxacademy.bodies.ContactBody;
import com.greenfoxacademy.domain.Contact;
import com.greenfoxacademy.requests.ContactRequest;
import com.greenfoxacademy.responses.*;
import com.greenfoxacademy.responses.Error;
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
public class ContactController {

    private ContactService contactService;
    private SessionService sessionService;

    @Autowired
    public ContactController(ContactService contactService,
                             SessionService sessionService) {
        this.contactService = contactService;
        this.sessionService = sessionService;
    }

    @PostMapping("/contacts")
    public ResponseEntity addNewContact(@RequestBody ContactRequest contactRequest,
                                        @RequestHeader HttpHeaders headers) throws Exception {
        int authresult = authIsSuccessful(headers, false);
        return (authresult == AuthCodes.OK) ?
                showAddingResults(contactRequest) :
                respondWithNotAuthenticated(authresult);
    }

    private ResponseEntity showAddingResults(ContactRequest contactRequest) {
        return (contactService.contactRequestIsValid(contactRequest)) ?
                showAddingOKResults(contactRequest) :
                respondWithBadRequest();
    }

    private ResponseEntity showAddingOKResults(ContactRequest contactRequest) {
        Contact newContact = contactService.createContact(contactRequest, null);
        contactService.saveNewContact(newContact);
        return new ResponseEntity<>(newContact,
                sessionService.generateHeaders(),
                HttpStatus.CREATED);
    }

    @GetMapping("/contacts")
    public ResponseEntity listAllContacts(@RequestHeader HttpHeaders headers) {
        int authresult = authIsSuccessful(headers, false);
        return (authresult == AuthCodes.OK) ?
                showContacts() :
                respondWithNotAuthenticated(authresult);
    }

    private ResponseEntity showContacts() {
        MultipleContactsResponse multipleContactsResponse =
                new MultipleContactsResponse(contactService.obtainAllContacts());
        return new ResponseEntity<>(multipleContactsResponse,
                sessionService.generateHeaders(),
                HttpStatus.OK);
    }

    @DeleteMapping("/contact/{id}")
    public ResponseEntity deleteContact(@PathVariable("id") Long contactId,
                                        @RequestHeader HttpHeaders headers) {
        int authresult = authIsSuccessful(headers, false);
        return (authresult == AuthCodes.OK) ?
                showDeletingResults(contactId, headers) :
                respondWithNotAuthenticated(authresult);
    }

    private ResponseEntity showDeletingResults(Long contactId, HttpHeaders headers) {
        Long userId = obtainUserId(headers);
        return (contactService.contactBelongsToUser(contactId, userId)) ?
                showDeletingOKResults(contactId) :
                respondWithBadRequest();
    }

    private ResponseEntity showDeletingOKResults(Long contactId) {
        Contact contactToDelete = contactService.findContactById(contactId);
        ContactBody deletedContactInfo = new ContactBody(contactToDelete);
        contactService.deleteContact(contactId);
        return new ResponseEntity<>(deletedContactInfo,
                sessionService.generateHeaders(),
                HttpStatus.OK);
    }

    @PutMapping("/contact/{id}")
    public ResponseEntity editContact(@PathVariable("id") Long contactId,
                                      @RequestBody ContactRequest contactRequest,
                                      @RequestHeader HttpHeaders headers) throws Exception {
        int authresult = authIsSuccessful(headers, false);
        return (authresult == AuthCodes.OK) ?
                showEditingResults(contactId, headers, contactRequest) :
                respondWithNotAuthenticated(authresult);
    }

    @GetMapping("/contact/{id}")
    public ResponseEntity getSingleContact(@PathVariable("id") Long contactId,
                                           @RequestHeader HttpHeaders headers) throws Exception {
        int authresult = authIsSuccessful(headers, false);
        return (authresult == AuthCodes.OK) ?
                showSingleContact(contactId) :
                respondWithNotAuthenticated(authresult);
    }

    private ResponseEntity showSingleContact(Long contactId) {
        Contact singleContact = contactService.findContactById(contactId);
        if (singleContact != null) {
            return new ResponseEntity<>(singleContact,
                    sessionService.generateHeaders(),
                    HttpStatus.OK);
        } else return new ResponseEntity<>(new ContactNotFoundErrorResponse(),
                sessionService.generateHeaders(),
                HttpStatus.NOT_FOUND);
    }

    private ResponseEntity showEditingResults(Long contactId,
                                              HttpHeaders headers,
                                              ContactRequest contactRequest) {
        Long userId = obtainUserId(headers);
        return (editingParametersAreValid(contactId, userId, contactRequest)) ?
                showEditingOKResults(contactId, contactRequest) :
                respondWithBadRequest();
    }

    private ResponseEntity showEditingOKResults(Long contactId,
                                                ContactRequest contactRequest) {
        Contact updatedContact = contactService.createContact(contactRequest, contactId);
        contactService.saveNewContact(updatedContact);
        return new ResponseEntity<>(updatedContact,
                sessionService.generateHeaders(),
                HttpStatus.OK);
    }

    private boolean editingParametersAreValid(Long contactId,
                                              Long userId,
                                              ContactRequest contactRequest) {
        return contactService.contactBelongsToUser(contactId, userId) &&
                contactService.contactRequestIsValid(contactRequest);
    }

    private int authIsSuccessful(HttpHeaders headers, boolean requireAdmin) {
        return sessionService.sessionIsValid(headers, requireAdmin);
    }

    private Long obtainUserId(HttpHeaders headers) {
        return sessionService.obtainUserIdFromHeaderToken(headers);
    }

    private ResponseEntity respondWithNotAuthenticated(int authresult) {
        NotAuthenticatedErrorResponse notAuthenticatedErrorResponse =
                new NotAuthenticatedErrorResponse(
                        new Error("Authentication error", "Not authenticated"));
        notAuthenticatedErrorResponse.addErrorMessages(authresult);
        return new ResponseEntity<>(notAuthenticatedErrorResponse,
                sessionService.generateHeaders(),
                HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity respondWithBadRequest() {
        BadRequestErrorResponse badRequestErrorResponse =
                new BadRequestErrorResponse(
                        new Error("Data error", "Data did not match required format."));
        return new ResponseEntity<>(badRequestErrorResponse,
                sessionService.generateHeaders(),
                HttpStatus.BAD_REQUEST);
    }

}
