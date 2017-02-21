package com.greenfoxacademy.service;

import com.greenfoxacademy.bodies.UserAdminResponse;
import com.greenfoxacademy.domain.User;
import com.greenfoxacademy.repository.UserRepository;
import com.greenfoxacademy.requests.AuthRequest;
import com.greenfoxacademy.requests.RequestConstants;
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
public class UserService extends BaseService {

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @PersistenceContext(name = "default")
    private EntityManager em;

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

    public boolean registrationIsValid(AuthRequest request) { //TODO replace this with new
        return !oneOfRegistrationFieldsIsNull(request) &&
                !userExists(request.getEmail()) &&
                emailIsValid(request.getEmail()) &&
                passwordsMatch(request);
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

    RequestConstants checkEmail(String email) {
        if (email == null) {
            return RequestConstants.FIELD_NULL;
        } else if (!emailIsValid(email)) {
            return RequestConstants.FIELD_INVALID;
        } else if (!userExists(email)) {
            return RequestConstants.DB_SEARCH_RETURNED_NULL;
        } else {
            return RequestConstants.FIELD_OK;
        }
    }

    RequestConstants checkPasswords(String password, String confirmation) {
        if (password == null) {
            return RequestConstants.FIELD_NULL;
        } else if
    }

    public String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public void setUserPassword(User user, String rawPassword) {
        user.setPassword(encryptPassword(rawPassword));
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
                generateHeaders(),
                HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity respondWithItemNotFound() {
        return new ResponseEntity<>(new ItemNotFoundErrorResponse(ItemNotFoundErrorResponse.USER),
                generateHeaders(),
                HttpStatus.NOT_FOUND);
    }


    private ResponseEntity respondWithBadRequest() { //TODO this is the same as in contactcontroller
        BadRequestErrorResponse badRequestErrorResponse =
                new BadRequestErrorResponse(
                        new Error("Data error", "Data did not match required format."));
        return new ResponseEntity<>(badRequestErrorResponse,
                generateHeaders(),
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
                generateHeaders(),
                HttpStatus.OK);
    }

    public ResponseEntity respondWithAllUsers() {
        List<User> allUsers = obtainAllUsers();
        MultipleUserResponse multipleUserResponse = new MultipleUserResponse(allUsers);
        return new ResponseEntity<>(multipleUserResponse,
                generateHeaders(),
                HttpStatus.OK);
    }

    private ResponseEntity respondWithSingleUser(User user) {
        return new ResponseEntity<>(new UserAdminResponse(user),
                generateHeaders(),
                HttpStatus.OK);
    }

    public ResponseEntity respondWithFoundOrNotFound(User user) {
        return (user != null) ?
                respondWithSingleUser(user)
                : respondWithItemNotFound();
    }

    public ResponseEntity showDeletingResults(User user) {
        return (user != null) ?
                deleteUser(user)
                : respondWithItemNotFound();
    }

    private ResponseEntity deleteUser(User user) {
        userRepository.delete(user.getId());
        return respondWithSingleUser(user);
    }
}