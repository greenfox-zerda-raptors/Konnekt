package com.greenfoxacademy.controllers;

import com.greenfoxacademy.requests.ContactRequest;
import com.greenfoxacademy.responses.*;
import com.greenfoxacademy.service.ContactService;
import com.greenfoxacademy.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Created by Jade Team on 2017.01.24.. Controller responsible for CRUD operations
 */

@BaseController
public class ContactController {

    private SessionService sessionService;
    private ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService,
                             SessionService sessionService) {
        this.contactService = contactService;
        this.sessionService = sessionService;
    }

    @PostMapping("/contacts")
    public ResponseEntity addNewContact(@RequestBody ContactRequest contactRequest,
                                        @RequestHeader HttpHeaders headers) throws Exception {
        int authResult = sessionService.sessionIsValid(headers);
        return (authResult == AuthCodes.OK) ?
                contactService.showAddingResults(contactRequest) :
                contactService.respondWithNotAuthenticated(authResult);
    }

    @GetMapping("/contacts")
    public ResponseEntity listAllContacts(@RequestHeader HttpHeaders headers) {
        int authResult = sessionService.sessionIsValid(headers);
        return (authResult == AuthCodes.OK) ?
                contactService.showContacts() :
                contactService.respondWithNotAuthenticated(authResult);
    }

    @DeleteMapping("/contact/{id}")
    public ResponseEntity deleteContact(@PathVariable("id") Long contactId,
                                        @RequestHeader HttpHeaders headers) {
        int authResult = sessionService.sessionIsValid(headers);
        return (authResult == AuthCodes.OK) ?
                contactService.showDeletingResults(
                        contactId,
                        headers,
                        sessionService.obtainUserIdFromHeaderToken(headers)) :
                contactService.respondWithNotAuthenticated(authResult);
    }

    @PutMapping("/contact/{id}")
    public ResponseEntity editContact(@PathVariable("id") Long contactId,
                                      @RequestBody ContactRequest contactRequest,
                                      @RequestHeader HttpHeaders headers) throws Exception {
        int authResult = sessionService.sessionIsValid(headers);
        return (authResult == AuthCodes.OK) ?
                contactService.showEditingResults(contactId,
                        contactRequest,
                        sessionService.obtainUserIdFromHeaderToken(headers)) :
                contactService.respondWithNotAuthenticated(authResult);
    }

}
