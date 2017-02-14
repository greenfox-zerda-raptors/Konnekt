package com.greenfoxacademy.controllers;

import com.google.gson.Gson;
import com.greenfoxacademy.KonnektApplication;
import com.greenfoxacademy.config.Profiles;
import com.greenfoxacademy.domain.Contact;
import com.greenfoxacademy.domain.Session;
import com.greenfoxacademy.repository.ContactRepository;
import com.greenfoxacademy.repository.SessionRepository;
import com.greenfoxacademy.repository.UserRepository;
import com.greenfoxacademy.service.ContactService;
import com.greenfoxacademy.service.SessionService;
import com.greenfoxacademy.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by Jade Team on 2017.01.30..
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@TestExecutionListeners(value = TransactionalTestExecutionListener.class)
@ContextConfiguration(classes = KonnektApplication.class, loader = SpringBootContextLoader.class)
@ActiveProfiles(Profiles.TEST)
@Transactional
public class ContactControllerTest extends AbstractJUnit4SpringContextTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ContactService contactService;
    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionService sessionService;
    @Autowired
    private SessionRepository sessionRepository;


    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(context).build();
        contactService.emptyRepositoryBeforeTest();
        Session session = new Session("abcde", userService.findUserById(1L));
        sessionService.saveSession(session);
    }

    @Test
    public void addContactWithInvalidData() throws Exception {
        mockMvc.perform(post("/contacts")
                .header("session_token", "abcde")
                .contentType(MediaType.APPLICATION_JSON).content(""))
                .andExpect(status().isBadRequest());
        assertTrue(contactService.findContactByName("Jane Doe") == null);
    }

    @Test
    public void addContactWithValidData() throws Exception {
        TestContact validTestContact = new TestContact("Jane Doe", "FOOBAR", 1L);
        String validTestJson = createTestJson(validTestContact);
        mockMvc.perform(post("/contacts")
                .header("session_token", "abcde")
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .contentType(MediaType.APPLICATION_JSON).content(validTestJson))
                .andExpect(status().isCreated());
        assertTrue(contactService.findContactByName("Jane Doe") != null);
    }

    @Test
    public void addContactWithoutToken() throws Exception {
        TestContact validTestContact = new TestContact("Jane Doe", "FOOBAR", 1L);
        String validTestJson = createTestJson(validTestContact);

        mockMvc.perform(post("/contacts")
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .contentType(MediaType.APPLICATION_JSON).content(validTestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void addContactWithWrongToken() throws Exception {
        TestContact validTestContact = new TestContact("Jane Doe", "FOOBAR", 1L);
        String validTestJson = createTestJson(validTestContact);

        mockMvc.perform(post("/contacts")
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .header("session_token", "wrong")
                .contentType(MediaType.APPLICATION_JSON).content(validTestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void listContactsWithValidData() throws Exception {
        mockMvc.perform(get("/contacts")
                .header("session_token", "abcde"))
                .andExpect(status().isOk());
    }

    @Test
    public void listContactsWithoutToken() throws Exception {
        mockMvc.perform(get("/contacts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void listContactsWithWrongToken() throws Exception {
        mockMvc.perform(get("/contacts")
                .header("session_token", "wrong"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateContactWithValidData() throws Exception {

        Contact testcontact = new Contact(userService.findUserById(1L), "John Doe", "FOOBAR");
        contactRepository.save(testcontact);
        Long contactId = contactService.findContactByName("John Doe").getId();

        TestContact updatedTestContact = new TestContact("Jane Doe", "FOOBAR", 1L);
        String updateJson = createTestJson(updatedTestContact);
        mockMvc.perform(put("/contact/{id}", contactId)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .header("session_token", "abcde")
                .contentType(MediaType.APPLICATION_JSON).content(updateJson))
                .andExpect(status().isOk());

        assertTrue(contactService.findContactByName("Jane Doe") != null);
    }

    @Test
    public void updateContactWithoutToken() throws Exception {
        Contact testcontact = new Contact(userService.findUserById(1L), "John Doe", "FOOBAR");
        contactRepository.save(testcontact);
        Long contactId = contactService.findContactByName("John Doe").getId();

        TestContact updatedTestContact = new TestContact("Jane Doe", "FOOBAR", 1L);
        String updateJson = createTestJson(updatedTestContact);
        mockMvc.perform(put("/contact/{id}", contactId)
                .contentType(MediaType.APPLICATION_JSON).content(updateJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateContactWithWrongToken() throws Exception {
        Contact testcontact = new Contact(userService.findUserById(1L), "John Doe", "FOOBAR");
        contactRepository.save(testcontact);
        Long contactId = contactService.findContactByName("John Doe").getId();


        TestContact updatedTestContact = new TestContact("Jane Doe", "FOOBAR", 1L);
        String updateJson = createTestJson(updatedTestContact);
        mockMvc.perform(put("/contact/{id}", contactId)
                .header("session_token", "wrong")
                .contentType(MediaType.APPLICATION_JSON).content(updateJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteContact() throws Exception {
        Contact testcontact = new Contact(userService.findUserById(1L), "John Doe", "FOOBAR");
        contactRepository.save(testcontact);
        Long contactId = contactService.findContactByName("John Doe").getId();
        mockMvc.perform(delete("/contact/{id}", contactId)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .header("session_token", "abcde")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk());

        assertTrue(contactService.findContactByName("John Doe") == null);
    }

    @Test
    public void deleteContactWithWrongToken() throws Exception {
        Contact testcontact = new Contact(userService.findUserById(1L), "John Doe", "FOOBAR");
        contactRepository.save(testcontact);
        Long contactId = contactService.findContactByName("John Doe").getId();
        mockMvc.perform(delete("/contact/{id}", contactId)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .header("session_token", "wrong")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void badRequestPost() throws Exception {
        mockMvc.perform(post("/contacts")
                .header("session_token", "abcde")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void badRequestPut() throws Exception {
        Contact testcontact = new Contact(userService.findUserById(1L), "John Doe", "FOOBAR");
        contactRepository.save(testcontact);

        mockMvc.perform(put("/contact/1")
                .header("session_token", "abcde")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void unauthenticatedContactIdGet() throws Exception {
        int id = 1;
        mockMvc.perform(get("/contact/{id}", id)
                .header("session_token", "abcdefgéééééééééééé"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void contactIdGet() throws Exception {
        int id = 1;
        Contact testcontact = new Contact(userService.findUserById(1L), "Pista bá", "Fúr, farag");
        contactRepository.save(testcontact);
        mockMvc.perform(get("/contact/{id}", id)
                .header("session_token", "abcde"))
                .andExpect(status().isOk());

    }

    @Test
    public void invalidContactIdGet() throws Exception {
        int id = -1;
        mockMvc.perform(get("/contact/{id}", id)
                .header("session_token", "abcde"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void deleteContactWithoutToken() throws Exception {
        Contact testcontact = new Contact(userService.findUserById(1L), "John Doe", "FOOBAR");
        contactRepository.save(testcontact);
        Long contactId = contactService.findContactByName("John Doe").getId();

        mockMvc.perform(delete("/contact/{id}", contactId)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }


    private String createTestJson(TestContact testContact) {
        Gson testContactConverter = new Gson();
        return testContactConverter.toJson(testContact);
    }


}
