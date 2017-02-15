package com.greenfoxacademy.controllers;

import com.google.gson.Gson;
import com.greenfoxacademy.KonnektApplication;
import com.greenfoxacademy.config.Profiles;
import com.greenfoxacademy.domain.ForgotPasswordToken;
import com.greenfoxacademy.domain.Session;
import com.greenfoxacademy.repository.ContactRepository;
import com.greenfoxacademy.repository.ForgotPasswordRepository;
import com.greenfoxacademy.repository.SessionRepository;
import com.greenfoxacademy.repository.UserRepository;
import com.greenfoxacademy.responses.AuthCodes;
import com.greenfoxacademy.service.ContactService;
import com.greenfoxacademy.service.ForgotPasswordService;
import com.greenfoxacademy.service.SessionService;
import com.greenfoxacademy.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.http.HttpHeaders;
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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
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
    public void testEmailSendingWithExistingEmail() throws Exception { //this returns 503 without an internet connection
        String emailJson = createTestJson(new TestEmail(userService.findUserById(1L).getEmail()));
        mockMvc.perform(post("/forgotpassword")
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .contentType(MediaType.APPLICATION_JSON).content(emailJson)).
                andExpect(status().isAccepted());

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
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .contentType(MediaType.APPLICATION_JSON).content(testReset))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testResetPasswordPostWithNonMatchingPassword() throws Exception {
        String BadTestReset = createTestJson(new TestRegistration(userService.findUserById(1L).getEmail(), "goodpassword", "badpassword"));
        mockMvc.perform(post("/resetpassword?token={token}", token)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .contentType(MediaType.APPLICATION_JSON).content(BadTestReset))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testResetPasswordPostWithNullPassword() throws Exception {
        String BadTestReset = createTestJson(new TestRegistration(userService.findUserById(1L).getEmail(), "goodpassword", null));
        mockMvc.perform(post("/resetpassword?token={token}", token)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .contentType(MediaType.APPLICATION_JSON).content(BadTestReset))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testResetPasswordPostWithValidRequest() throws Exception {
        String testReset = createTestJson(new TestRegistration(userService.findUserById(1L).getEmail(), "goodpassword", "goodpassword"));
        String encrypted = userService.encryptPassword("goodpassword");
        mockMvc.perform(post("/resetpassword?token={token}", token)
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .contentType(MediaType.APPLICATION_JSON).content(testReset))
                .andExpect(status().isOk());
        assertTrue(userService.findUserById(1L).getPassword().equals(encrypted));
        assertFalse(sessionService.tokenExists(token, (string) -> forgotPasswordRepository.findOne(token)));

    }

    @Test
    public void simpleTokenTests() {
        ForgotPasswordToken forgotToken = new ForgotPasswordToken(5);
        forgotToken.setToken(forgotPasswordService.generateToken());
        ForgotPasswordToken expiredToken = new ForgotPasswordToken(-5);
        expiredToken.setToken(forgotPasswordService.generateToken());
        ForgotPasswordToken userIdToken = new ForgotPasswordToken(userService.findUserById(1L), 5);
        userIdToken.setToken(forgotPasswordService.generateToken());
        String tokenString = forgotPasswordService.generateToken();
        ForgotPasswordToken manualToken = new ForgotPasswordToken(tokenString, userService.findUserById(1L), 5);
        Session sessionToken = new Session(5);
        sessionToken.setToken(sessionService.generateToken());
        Session expiredSessionToken = new Session(-5);
        expiredSessionToken.setToken(sessionService.generateToken());
        Session userIdSessionToken = new Session(userService.findUserById(1L), 5);
        userIdSessionToken.setToken(sessionService.generateToken());
        Session manualSessionToken = new Session(tokenString, userService.findUserById(1L), 5);
        List<ForgotPasswordToken> forgotList = new LinkedList<>();
        forgotList.add(forgotToken);
        forgotList.add(expiredToken);
        forgotList.add(userIdToken);
        forgotList.add(manualToken);
        forgotPasswordRepository.save(forgotList);
        List<Session> sessionList = new LinkedList<>();
        sessionList.add(sessionToken);
        sessionList.add(expiredSessionToken);
        sessionList.add(userIdSessionToken);
        sessionList.add(manualSessionToken);
        sessionRepository.save(sessionList);

        assertEquals(AuthCodes.OK, sessionService.sessionTokenIsValid(forgotToken.getToken(), forgotPasswordRepository::findOne));
        assertEquals(AuthCodes.SESSION_TOKEN_EXPIRED, sessionService.sessionTokenIsValid(expiredToken.getToken(), forgotPasswordRepository::findOne));
        assertEquals(1L, (long) forgotPasswordService.findUserByToken(userIdToken.getToken()).getId());
        assertEquals(userService.findUserById(1L), forgotPasswordService.findUserByToken(tokenString));
        assertEquals(AuthCodes.OK, sessionService.sessionTokenIsValid(sessionToken.getToken(), sessionRepository::findOne));
        assertEquals(AuthCodes.SESSION_TOKEN_EXPIRED, sessionService.sessionTokenIsValid(expiredSessionToken.getToken(), sessionRepository::findOne));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("session_token", userIdSessionToken.getToken());
        assertEquals(1L, (long) sessionService.obtainUserIdFromHeaderToken(httpHeaders));
        httpHeaders.clear();
        httpHeaders.set("session_token", tokenString);
        assertEquals(1L, (long) sessionService.obtainUserIdFromHeaderToken(httpHeaders));
        Date now = new Date();
//        assertTrue(now.after(sessionRepository.findOne(tokenString).getTimestamp()));
        assertTrue(sessionRepository.findOne(tokenString).getDEFAULT_TIMEOUT() > 0);
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
