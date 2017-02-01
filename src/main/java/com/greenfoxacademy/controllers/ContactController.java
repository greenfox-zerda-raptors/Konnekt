package com.greenfoxacademy.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.greenfoxacademy.domain.Contact;
import com.greenfoxacademy.responses.BadRequestResponse;
import com.greenfoxacademy.responses.CreatedResponse;
import com.greenfoxacademy.responses.OKResponse;
import com.greenfoxacademy.responses.UnauthorizedResponse;
import com.greenfoxacademy.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.greenfoxacademy.domain.ContactsDisplay;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jade Team on 2017.01.24..
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

    @PostMapping("/add")
    public ResponseEntity addNewContact(@RequestBody String newContactData,
                                        @RequestHeader HttpHeaders headers) throws Exception {
        if (sessionService.tokenExists(headers)) {
            JsonNode newContactJson = new ObjectMapper().readValue(newContactData, JsonNode.class);
            Long userId = sessionService.obtainUserIdFromToken(headers);
            Contact newContact = contactService.createNewContact(newContactJson, userId);
            if (contactService.newContactIsValid(newContact)) {
                contactService.saveNewContact(newContact);
                return new CreatedResponse().generateResponse();
            } else {
                return new BadRequestResponse().generateResponse();
            }
        } else {
            return new UnauthorizedResponse().generateResponse();
        }

    }

    @GetMapping("/allcontacts")
    public String listAllContact(@RequestHeader HttpHeaders headers) {
        if (sessionService.tokenExists(headers)) {
            return obtainContactList(() -> contactService.obtainAllContacts());
        } else {
            return "401";
        }
    }

    @GetMapping("/mycontacts")
    public String listMyContact(@RequestHeader HttpHeaders headers) {
        if (sessionService.tokenExists(headers)) {
            return obtainContactList(() ->
                    contactService.obtainMyContacts(sessionService.obtainUserIdFromToken(headers)));
        } else {
            return "401";
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteContact(@PathVariable("id") Long contactId,
                                        @RequestHeader HttpHeaders headers) {
        if (sessionService.tokenExists(headers)) {
            Long userId = sessionService.obtainUserIdFromToken(headers);
            if (contactService.contactBelongsToUser(contactId, userId)) {
                contactService.deleteContact(contactId);
                return new OKResponse().generateResponse();
            } else {
                return new BadRequestResponse().generateResponse();
            }
        }
        else {
            return new UnauthorizedResponse().generateResponse();
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity editContact(@PathVariable("id") Long contactId,
                                      @RequestBody String newContactData,
                                      @RequestHeader HttpHeaders headers) throws Exception {
        if (sessionService.tokenExists(headers)) {
            Long userId = sessionService.obtainUserIdFromToken(headers);
            JsonNode newContactJson = new ObjectMapper().readValue(newContactData, JsonNode.class);
            Contact editedContact = contactService.createNewContact(newContactJson, userId);
            if (contactService.contactBelongsToUser(contactId, userId) && contactService.newContactIsValid(editedContact)) {
                contactService.editContact(contactId, newContactJson);
                return new OKResponse().generateResponse();
            } else {
                return new BadRequestResponse().generateResponse();
            }
        } else {
            return new UnauthorizedResponse().generateResponse();
        }
    }

    private String obtainContactList(
            ContactCollector contactCollector) {
        List<Object[]> allContacts =
                contactCollector.collectContacts();
        ArrayList<ContactsDisplay> contactsDisplays = new ArrayList<>();
        for (Object[] contactArray : allContacts) {
            contactsDisplays.add(new ContactsDisplay(contactArray[0], contactArray[1], contactArray[2], contactArray[3]));
        }
        if (allContacts.size() != 0) {
            Gson contactsGson = new Gson();
            return contactsGson.toJson(contactsDisplays);
        } else {
            return "nothing to show";
        }
    }

    public ResponseEntity manageContacts(){
        return null;
    }

    public boolean validateRequest(){
        return true;
    }

}
