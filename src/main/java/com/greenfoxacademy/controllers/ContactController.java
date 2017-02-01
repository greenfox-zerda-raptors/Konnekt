package com.greenfoxacademy.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.greenfoxacademy.domain.Contact;
import com.greenfoxacademy.responses.*;
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
    private Gson gson;

    @Autowired
    public ContactController(ContactService contactService,
                             SessionService sessionService) {
        this.contactService = contactService;
        this.sessionService = sessionService;
        gson = new Gson();
    }

    @PostMapping("/add")
    public ResponseEntity addNewContact(@RequestBody String newContactData,
                                        @RequestHeader HttpHeaders headers) throws Exception {
        if (userIsAuthenticated(headers)) {

            JsonNode newContactJson = new ObjectMapper().readValue(newContactData, JsonNode.class);
            Long userId = sessionService.obtainUserIdFromToken(headers);
            Contact newContact = contactService.createNewContact(newContactJson, userId);
            if (contactService.newContactIsValid(newContact)) {
                contactService.saveNewContact(newContact);
                SingleContactResponse singleContactResponse = new SingleContactResponse(newContact);
                return new ResponseEntity<>(gson.toJson(singleContactResponse),
                        sessionService.generateHeaders("", ""),
                        HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("",
                        sessionService.generateHeaders("", ""),
                        HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("",
                    sessionService.generateHeaders("", ""),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/allcontacts")
    public ResponseEntity listAllContact(@RequestHeader HttpHeaders headers) {
        if (userIsAuthenticated(headers)) {
            String contacts = obtainContactList(() -> contactService.obtainAllContacts());
            return new ResponseEntity<>(new ContactListResponse(contacts),
                    sessionService.generateHeaders("", ""),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>("",
                    sessionService.generateHeaders("", ""),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/mycontacts")
    public ResponseEntity listMyContacts(@RequestHeader HttpHeaders headers) {
        if (userIsAuthenticated(headers)) {
            String contacts = obtainContactList(() ->
                    contactService.obtainMyContacts(sessionService.obtainUserIdFromToken(headers)));
            return new ResponseEntity<>(new ContactListResponse(contacts),
                    sessionService.generateHeaders("", ""),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>("",
                    sessionService.generateHeaders("", ""),
                    HttpStatus.UNAUTHORIZED);

        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteContact(@PathVariable("id") Long contactId,
                                        @RequestHeader HttpHeaders headers) {
        if (userIsAuthenticated(headers)) {
            Long userId = sessionService.obtainUserIdFromToken(headers);
            if (contactService.contactBelongsToUser(contactId, userId)) {
                contactService.deleteContact(contactId);
                return new ResponseEntity<>("",
                        sessionService.generateHeaders("", ""),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>("",
                        sessionService.generateHeaders("", ""),
                        HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("",
                    sessionService.generateHeaders("", ""),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity editContact(@PathVariable("id") Long contactId,
                                      @RequestBody String newContactData,
                                      @RequestHeader HttpHeaders headers) throws Exception {
        if (userIsAuthenticated(headers)) {
            Long userId = sessionService.obtainUserIdFromToken(headers);
            JsonNode newContactJson = new ObjectMapper().readValue(newContactData, JsonNode.class);
            Contact editedContact = contactService.createNewContact(newContactJson, userId);
            if (contactService.contactBelongsToUser(contactId, userId) && contactService.newContactIsValid(editedContact)) {
                contactService.editContact(contactId, newContactJson);
                SingleContactResponse singleContactResponse =
                        new SingleContactResponse(contactService.findContactById(contactId));
                return new ResponseEntity<>(gson.toJson(singleContactResponse),
                        sessionService.generateHeaders("", ""),
                        HttpStatus.OK);

            } else {
                return new ResponseEntity<>("",
                        sessionService.generateHeaders("", ""),
                        HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("",
                    sessionService.generateHeaders("", ""),
                    HttpStatus.UNAUTHORIZED);
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

    private boolean userIsAuthenticated(@RequestHeader HttpHeaders headers) {
        return sessionService.tokenExists(headers);
    }

}
