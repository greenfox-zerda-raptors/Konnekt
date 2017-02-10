package com.greenfoxacademy.controllers;

import com.google.gson.Gson;
import com.greenfoxacademy.KonnektApplication;
import com.greenfoxacademy.config.Profiles;
import com.greenfoxacademy.domain.ForgotPasswordToken;
import com.greenfoxacademy.repository.ContactRepository;
import com.greenfoxacademy.repository.ForgotPasswordRepository;
import com.greenfoxacademy.repository.SessionRepository;
import com.greenfoxacademy.repository.UserRepository;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by posam on 2017-02-09.
 * WHAAAAAAAAAAAAAAAASSSSSUUUUUP
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@TestExecutionListeners(value = TransactionalTestExecutionListener.class)
@ContextConfiguration(classes = KonnektApplication.class, loader = SpringBootContextLoader.class)
@ActiveProfiles(Profiles.TEST)
@Transactional
public class ForgotControllerTest extends AbstractJUnit4SpringContextTests {
    private MockMvc mockMvc;
    private String token;

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
    @Autowired
    private SessionRepository sessionRepository;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(context).build();
        token = forgotPasswordService.generateToken();
        forgotPasswordRepository.save(new ForgotPasswordToken(token, userRepository.findOne(1L)));
    }

    @Test
    public void testEmailSendingWithExistingEmail() throws Exception {
        String emailJson = createTestJson(new TestEmail(userService.findUserById(1L).getEmail()));
        mockMvc.perform(post("/forgotpassword")
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .contentType(MediaType.APPLICATION_JSON).content(emailJson)).
                andExpect(status().isAccepted()); //TODO find out why this returns 401 and ideally change to 202

    }

    @Test
    public void testEmailSendingWithInvalidEmail() throws Exception {
        mockMvc.perform(post("/forgotpassword")
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .contentType(MediaType.APPLICATION_JSON).content(createTestJson(
                        new TestEmail("-----------@----.---")))).
                andExpect(status().isBadRequest());
    }

    @Test
    public void testResetPasswordGetWithValidToken() throws Exception {
        mockMvc.perform(get("/resetpassword?token={token}", token)
                .contentType(MediaType.APPLICATION_JSON).content(""))
                .andExpect(status().isOk());

    }

    @Test
    public void testResetPasswordGetWithInvalidToken() throws Exception {
        mockMvc.perform(get("/resetpassword?token={token}", "hahahahahahaha")
                .contentType(MediaType.APPLICATION_JSON).content(""))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testResetPasswordPostWithInvalidToken() throws Exception {
        String testReset = createTestJson(new TestRegistration(userService.findUserById(1L).getEmail(), "goodpassword", "goodpassword"));
        mockMvc.perform(post("/resetpassword?token={token}", "hahahahahahaha")
                .contentType(MediaType.APPLICATION_JSON).content(testReset))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testResetPasswordPostWithNonMatchingPassword() throws Exception {
        String BadTestReset = createTestJson(new TestRegistration(userService.findUserById(1L).getEmail(), "goodpassword", "badpassword"));
        mockMvc.perform(post("/resetpassword?token={token}", token)
                .contentType(MediaType.APPLICATION_JSON).content(BadTestReset))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testResetPasswordPostWithNullPassword() throws Exception {
        String BadTestReset = createTestJson(new TestRegistration(userService.findUserById(1L).getEmail(), "goodpassword", null));
        mockMvc.perform(post("/resetpassword?token={token}", token)
                .contentType(MediaType.APPLICATION_JSON).content(BadTestReset))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testResetPasswordPostWithValidRequest() throws Exception {
        String testReset = createTestJson(new TestRegistration(userService.findUserById(1L).getEmail(), "goodpassword", "goodpassword"));
        String encrypted = userService.encryptPassword("goodpassword");
        mockMvc.perform(post("/resetpassword?token={token}", token)
                .contentType(MediaType.APPLICATION_JSON).content(testReset))
                .andExpect(status().isOk());
        assertTrue(userService.findUserById(1L).getPassword().equals(encrypted));
        assertFalse(sessionService.tokenExists(token));

    }

    private String createTestJson(TestEmail testEmail) {
        Gson testContactConverter = new Gson();
        return testContactConverter.toJson(testEmail);
    }

    private String createTestJson(TestRegistration testRegistration) {
        Gson testRegConverter = new Gson();
        return testRegConverter.toJson(testRegistration);
    }
}
