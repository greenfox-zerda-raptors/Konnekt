package com.greenfoxacademy.controllers;

import com.greenfoxacademy.domain.Contact;
import com.greenfoxacademy.requests.ContactRequest;
import com.greenfoxacademy.responses.BadRequestErrorResponse;
import com.greenfoxacademy.responses.Error;
import com.greenfoxacademy.responses.MultipleContactsResponse;
import com.greenfoxacademy.responses.NotAuthenticatedErrorResponse;
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
        return (authIsSuccessful(headers)) ?
                showAddingResults(contactRequest) :
                respondWithNotAuthenticated();
    }

    private ResponseEntity showAddingResults(ContactRequest contactRequest) {
        return (contactService.contactRequestIsValid(contactRequest)) ?
                showAddingOKResults(contactRequest) :
                respondWithBadRequest();
    }

    private ResponseEntity showAddingOKResults(ContactRequest contactRequest) {
        Contact newContact = contactService.createContact(contactRequest, null);
        contactService.saveNewContact(newContact);
        return new ResponseEntity<>(newContact.toString(),
                                    sessionService.generateHeaders(),
                                    HttpStatus.CREATED);
    }

    @GetMapping("/contacts")
    public ResponseEntity listAllContacts(@RequestHeader HttpHeaders headers) {
        return (authIsSuccessful(headers)) ?
                showContacts() :
                respondWithNotAuthenticated();
    }

    private ResponseEntity showContacts() {
        MultipleContactsResponse multipleContactsResponse =
                new MultipleContactsResponse(contactService.obtainAllContacts());
        return new ResponseEntity<>(multipleContactsResponse.toString(),
                                    sessionService.generateHeaders(),
                                    HttpStatus.OK);
    }

    @DeleteMapping("/contact/{id}")
    public ResponseEntity deleteContact(@PathVariable("id") Long contactId,
                                        @RequestHeader HttpHeaders headers) {
        return (authIsSuccessful(headers)) ?
                showDeletingResults(contactId, headers) :
                respondWithNotAuthenticated();
    }

    private ResponseEntity showDeletingResults(Long contactId, HttpHeaders headers) {
        Long userId = obtainUserId(headers);
        return (contactService.contactBelongsToUser(contactId, userId)) ?
                showDeletingOKResults(contactId) :
                respondWithBadRequest();
    }

    private ResponseEntity showDeletingOKResults(Long contactId) {
        Contact contactToDelete = contactService.findContactById(contactId);
        String deletedContactInfo = contactToDelete.toString();
        contactService.deleteContact(contactId);
        return new ResponseEntity<>(deletedContactInfo,
                                    sessionService.generateHeaders(),
                                    HttpStatus.OK);

    }

    @PutMapping("/contact/{id}")
    public ResponseEntity editContact(@PathVariable("id") Long contactId,
                                      @RequestBody ContactRequest contactRequest,
                                      @RequestHeader HttpHeaders headers) throws Exception {
        return (authIsSuccessful(headers)) ?
                showEditingResults(contactId, headers, contactRequest) :
                respondWithNotAuthenticated();
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
        return new ResponseEntity<>(updatedContact.toString(),
                                    sessionService.generateHeaders(),
                                    HttpStatus.OK);
    }

    private boolean editingParametersAreValid(Long contactId,
                                              Long userId,
                                              ContactRequest contactRequest) {
        return contactService.contactBelongsToUser(contactId, userId) &&
                contactService.contactRequestIsValid(contactRequest);
    }

    private boolean authIsSuccessful(HttpHeaders headers) {
        return sessionService.sessionIsValid(headers);
    }

    private Long obtainUserId(HttpHeaders headers) {
        return sessionService.obtainUserIdFromHeaderToken(headers);
    }

    private ResponseEntity respondWithNotAuthenticated() {
        NotAuthenticatedErrorResponse notAuthenticatedErrorResponse =
                new NotAuthenticatedErrorResponse(
                        new Error("Authentication error", "Not authenticated"));
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
