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
        mockMvc.perform(post("/contacts")
                .header("Origin", "https://lasers-cornubite-konnekt.herokuapp.com")
                .contentType(MediaType.APPLICATION_JSON).content(createTestJson(
                        new TestEmail(emailJson)))).
                andExpect(status().isUnauthorized()); //TODO find out why this returns 401 and ideally change to 202

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
        mockMvc.perform(get(String.format("/resetpassword?token=%s", token))
                .contentType(MediaType.APPLICATION_JSON).content(""))
                .andExpect(status().isOk());

    }

    @Test
    public void testResetPasswordGetWithInvalidToken() throws Exception {
        mockMvc.perform(get(String.format("/resetpassword?token=%s", "hahahahahahaha"))
                .contentType(MediaType.APPLICATION_JSON).content(""))
                .andExpect(status().isBadRequest());

    }

    private String createTestJson(TestEmail testEmail) {
        Gson testContactConverter = new Gson();
        return testContactConverter.toJson(testEmail);
    }
}
