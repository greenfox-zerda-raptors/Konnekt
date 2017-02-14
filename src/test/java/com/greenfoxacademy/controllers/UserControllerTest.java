package com.greenfoxacademy.controllers;

import com.greenfoxacademy.KonnektApplication;
import com.greenfoxacademy.config.Profiles;
import com.greenfoxacademy.domain.ForgotPasswordToken;
import com.greenfoxacademy.domain.Session;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.ContactRepository;
import com.greenfoxacademy.repository.ForgotPasswordRepository;
import com.greenfoxacademy.repository.UserRepository;
import com.greenfoxacademy.responses.UserRoles;
import com.greenfoxacademy.service.ContactService;
import com.greenfoxacademy.service.ForgotPasswordService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private ForgotPasswordService forgotPasswordService;
    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Autowired
    private SessionService sessionService;


    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(context).build();
        this.token = sessionService.generateToken();
        this.adminToken = sessionService.generateToken();
        forgotPasswordRepository.save(new ForgotPasswordToken(token, userRepository.findOne(1L)));
        User user = new User("Sanyi", "sanyi1", true, UserRoles.USER);
        User admin = new User("Pista", "1234", true, UserRoles.ADMIN);
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

}
