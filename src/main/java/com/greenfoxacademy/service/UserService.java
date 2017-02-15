package com.greenfoxacademy.service;

import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.UserRepository;
import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.responses.*;
import com.greenfoxacademy.responses.Error;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by JadeTeam on 1/19/2017. Communicates with UserRepository
 */


@Service
public class UserService {

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired //TODO this is here because with a constructor autowire there would be a circular reference between UserService and SessionService
    private SessionService sessionService;

    @PersistenceContext(name = "default")
    EntityManager em;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public boolean userExists(String email) {
        return findUserByEmail(email) != null;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findUserByName(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(AuthRequest registrationRequest) {
        User newUser = new User();
        String rawPassword = registrationRequest.getPassword();
        newUser.setEmail(registrationRequest.getEmail());
        setUserPassword(newUser, encryptPassword(rawPassword));
        return newUser;
    }

    public boolean registrationIsValid(AuthRequest request) {
        return !oneOfRegistrationFieldsIsNull(request) &&
                !userExists(request.getEmail()) &&
                emailIsValid(request.getEmail()) &&
                passwordsMatch(request);
    }

    private boolean emailIsValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    public boolean passwordsMatch(AuthRequest request) {
        return request
                .getPassword()
                .equals(request.getPassword_confirmation());
    }

    public boolean userLoginIsValid(AuthRequest request) {
        return !emailOrPasswordIsNull(request) &&
                userExists(request.getEmail()) &&
                passwordAndEmailMatch(request);
    }

    public boolean passwordAndEmailMatch(AuthRequest request) {
        return passwordEncoder.matches(request.getPassword(), findUserByEmail(request.getEmail()).getPassword());

        // here you should hash the received pw
    }

    public User findUserById(Long userId) {
        return userRepository.findOne(userId);
    }

    public boolean emailOrPasswordIsNull(AuthRequest request) {
        return request.getEmail() == null ||
                request.getPassword() == null;
    }

    public boolean oneOfPasswordsIsNull(AuthRequest request) {
        return request.getPassword() == null ||
                request.getPassword_confirmation() == null;
    }

    public boolean oneOfRegistrationFieldsIsNull(AuthRequest request) {
        return emailOrPasswordIsNull(request) ||
                request.getPassword_confirmation() == null;
    }

    public String encryptPassword(String rawPassword) {
        try {
            return passwordEncoder.encode(rawPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setUserPassword(User user, String password) {
        user.setPassword(password);
        save(user);
    }

    public boolean userIsAdmin(Long userId) {
        return userRepository.findOne(userId).getUserRole().equals(UserRoles.ADMIN);
    }

    public List<User> obtainAllUsers() {
        return userRepository.findAll();
    }

    public boolean adminEditIsValid(UserAdminResponse userAdminResponse, User user) {
        return userAdminResponse.getUser_id() != null &&
                userAdminResponse.getUser_id().equals(user.getId()) &&
                userAdminResponse.getEmail() != null &&
                userAdminResponse.getUserRole().equals(UserRoles.USER) ||
                userAdminResponse.getUserRole().equals(UserRoles.ADMIN);
    }

    public void updateUser(UserAdminResponse userAdminResponse) { //TODO this will produce a NPE if called without ensuring the id exists (currently enforced)
        User user = userRepository.findOne(userAdminResponse.getUser_id());
        user.setEmail(userAdminResponse.getEmail());
        user.setFirstName(userAdminResponse.getFirstName());
        user.setLastName(userAdminResponse.getLastName());
        user.setUserRole(userAdminResponse.getUserRole());
        user.setEnabled(userAdminResponse.isEnabled());
        userRepository.save(user);
    }

    public void restartUserIdSeq() {
        em.createNativeQuery("ALTER SEQUENCE user_id_seq RESTART WITH 2")
                .executeUpdate();
    }

    public ResponseEntity respondWithUnauthorized(int authResult) {
        NotAuthenticatedErrorResponse response = new NotAuthenticatedErrorResponse();
        response.addErrorMessages(authResult);
        return new ResponseEntity<>(response,
                sessionService.generateHeaders(),
                HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity respondWithItemNotFound() {
        return new ResponseEntity<>(new ItemNotFoundErrorResponse(ItemNotFoundErrorResponse.USER),
                sessionService.generateHeaders(),
                HttpStatus.NOT_FOUND);
    }


    private ResponseEntity respondWithBadRequest() { //TODO this is the same as in contactcontroller
        BadRequestErrorResponse badRequestErrorResponse =
                new BadRequestErrorResponse(
                        new Error("Data error", "Data did not match required format."));
        return new ResponseEntity<>(badRequestErrorResponse,
                sessionService.generateHeaders(),
                HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity showEditingResults(UserAdminResponse userAdminResponse, User user) {
        if (user != null) {
            return (adminEditIsValid(userAdminResponse, user)) ?
                    showEditingOKResults(userAdminResponse) :
                    respondWithBadRequest();
        } else return respondWithItemNotFound();
    }

    private ResponseEntity showEditingOKResults(UserAdminResponse userAdminResponse) {
        updateUser(userAdminResponse);
        return new ResponseEntity<>(userAdminResponse,
                sessionService.generateHeaders(),
                HttpStatus.OK);
    }

    public ResponseEntity respondWithAllUsers() {
        List<User> allUsers = obtainAllUsers();
        MultipleUserResponse multipleUserResponse = new MultipleUserResponse(allUsers);
        return new ResponseEntity<>(multipleUserResponse,
                sessionService.generateHeaders(),
                HttpStatus.OK);
    }

    private ResponseEntity respondWithSingleUser(User user) {
        return new ResponseEntity<>(new UserAdminResponse(user),
                sessionService.generateHeaders(),
                HttpStatus.OK);
    }

    public ResponseEntity respondWithFoundOrNotFound(User user) {
        return (user != null) ?
                respondWithSingleUser(user)
                : respondWithItemNotFound();
    }
}