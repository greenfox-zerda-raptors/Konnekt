package com.greenfoxacademy.controllers;

import com.google.gson.Gson;
import com.greenfoxacademy.KonnektApplication;
import com.greenfoxacademy.bodies.UserAdminResponse;
import com.greenfoxacademy.config.Profiles;
import com.greenfoxacademy.domain.Session;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.ContactRepository;
import com.greenfoxacademy.repository.UserRepository;
import com.greenfoxacademy.responses.UserRoles;
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

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by posam on 2017-02-14.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@TestExecutionListeners(value = TransactionalTestExecutionListener.class)
@ContextConfiguration(classes = KonnektApplication.class, loader = SpringBootContextLoader.class)
@ActiveProfiles(Profiles.TEST)
@Transactional
public class UserControllerTest extends AbstractJUnit4SpringContextTests {
    private String token;
    private String adminToken;
    private MockMvc mockMvc;
    private User user;
    private User admin;

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


    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(context).build();
        this.token = sessionService.generateToken();
        this.adminToken = sessionService.generateToken();
        userService.restartUserIdSeq();
        user = new User("Sanyi", "sanyi1", true, UserRoles.USER);
        user.setEmail("never@mind.me");
        admin = new User("Pista", "1234", true, UserRoles.ADMIN);
        admin.setEmail("never@mind.me");
        userService.save(user);
        userService.save(admin);
        Session session = new Session(token, user);
        Session adminSession = new Session(adminToken, admin);
        sessionService.saveSession(session);
        sessionService.saveSession(adminSession);
    }

    @Test
    public void testUsersGetWithProperPrivileges() throws Exception {
        mockMvc.perform(get("/users")
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .header("session_token", adminToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void testUsersGetWithImproperPrivileges() throws Exception {
        mockMvc.perform(get("/users")
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .header("session_token", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testUsersPutWithImproperPrivileges() throws Exception {
        int id = 2;
        String validRequest = createTestJson(new UserAdminResponse(user));
        mockMvc.perform(put("/user/{id}", id)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .header("session_token", token)
                .contentType(MediaType.APPLICATION_JSON).content(validRequest))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testIdChangeRejectionOnUsersPut() throws Exception {
        long id = 2;
        long newId = 3;
        UserAdminResponse response = new UserAdminResponse();
        response.setUser_id(newId);
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setUserRole(user.getUserRole());
        response.setEnabled(user.isEnabled());
        String invalidInfo = createTestJson(response);
        mockMvc.perform(put("/user/{id}", id)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .header("session_token", adminToken)
                .contentType(MediaType.APPLICATION_JSON).content(invalidInfo))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUsersPutWithValidRequest() throws Exception {
        long id = 2;
        String newFirstName = "Alejandro";
        UserAdminResponse userAdminResponse = new UserAdminResponse(user);
        userAdminResponse.setFirstName(newFirstName);
        String updatedInfo = createTestJson(userAdminResponse);
        mockMvc.perform(put("/user/{id}", id)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .header("session_token", adminToken)
                .contentType(MediaType.APPLICATION_JSON).content(updatedInfo));

        assertTrue(newFirstName.equals(userService.findUserById(id).getFirstName()));
    }

    @Test
    public void testUsersPutWithInvalidRequest() throws Exception {
        int id = 2;
        user.setUserRole("a legmenőbb ember");
        String updatedInfo = createTestJson(new UserAdminResponse(user));
        user.setUserRole(UserRoles.USER);
        mockMvc.perform(put("/user/{id}", id)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .header("session_token", adminToken)
                .contentType(MediaType.APPLICATION_JSON).content(updatedInfo))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUsersPutWithNonexistentID() throws Exception {
        long id = -1;
        UserAdminResponse userAdminResponse = new UserAdminResponse(user);
        String updatedInfo = createTestJson(userAdminResponse);
        mockMvc.perform(put("/user/{id}", id)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .header("session_token", adminToken)
                .contentType(MediaType.APPLICATION_JSON).content(updatedInfo))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUsersSingleGetWithNonexistentID() throws Exception {
        long id = -1;
        mockMvc.perform(get("/user/{id}", id)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .header("session_token", adminToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUsersSingleGetWithValidRequest() throws Exception {
        long id = 2;
        mockMvc.perform(get("/user/{id}", id)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .header("session_token", adminToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUsersSingleGetWithImproperPrivileges() throws Exception {
        long id = 2;
        mockMvc.perform(get("/user/{id}", id)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .header("session_token", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUsersDeleteWithImproperPrivileges() throws Exception {
        long id = 2;
        mockMvc.perform(delete("/user/{id}", id)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .header("session_token", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUsersDeleteWithProperPrivileges() throws Exception {
        long id = 2;
        mockMvc.perform(delete("/user/{id}", id)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .header("session_token", adminToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertTrue(userRepository.findOne(id) == null);
    }

    @Test
    public void testUsersDeleteWithNonExistentID() throws Exception {
        long id = -1;
        mockMvc.perform(delete("/user/{id}", id)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .header("session_token", adminToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private String createTestJson(UserAdminResponse userAdminResponse) {
        Gson testContactConverter = new Gson();
        return testContactConverter.toJson(userAdminResponse);
    }

}
