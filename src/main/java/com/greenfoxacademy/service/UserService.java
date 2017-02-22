package com.greenfoxacademy.service;

import com.greenfoxacademy.bodies.UserAdminBody;
import com.greenfoxacademy.constants.UserRoles;
import com.greenfoxacademy.constants.Valid;
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
import java.util.ArrayList;
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

    public boolean registrationIsValid(ArrayList[] issues) {
        for (ArrayList a : issues) {
            if (!a.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public ArrayList[] validateAuthRequest(AuthRequest request, int[] pattern) {
        ArrayList[] issues = new ArrayList[3];
        issues[0] = new ArrayList<Valid.issues>();
        issues[1] = new ArrayList<Valid.issues>();
        issues[2] = new ArrayList<Valid.issues>();
        String email = request.getEmail();
        String password = request.getPassword();
        String confirm = request.getPassword_confirmation();
        if (email != null) {
        validateEmail(request, pattern[0], issues);
        } else issues[0].add(Valid.issues.NULL);
        if (password != null) {
            validatePassword(request, pattern[1], issues);
        } else issues[1].add(Valid.issues.NULL);
        if (confirm != null) {
            validateConfirmation(request, pattern[2], issues);
        } else issues[2].add(Valid.issues.NULL);
        return issues;
    }

    private void validateConfirmation(AuthRequest request, int i, ArrayList[] issues) {
        switch (i) {
            case Valid.NOT_REQUIRED:
                break;
            case Valid.MATCH:
                if (!passwordsMatch(request)) {
                    issues[2].add(Valid.issues.MISMATCH);
                }
        }
    }

    private void validatePassword(AuthRequest request, int requirement, ArrayList[] issues) {
        switch (requirement) {
            case Valid.NOT_REQUIRED:
                break;
            case Valid.MATCH:
                if (!passwordsMatch(request)) {
                    issues[1].add(Valid.issues.MISMATCH);
                }
                break;
            case Valid.AUTH:
                if (!passwordAndEmailMatch(request)) {
                    issues[1].add(Valid.issues.UNAUTHORIZED);
                }
                break;
        }
    }

    private void validateEmail(AuthRequest request, int requirement, ArrayList[] issues) {
        switch (requirement) {
            case Valid.NOT_REQUIRED:
                break;
            case Valid.AUTH:
                if (!userExists(request.getEmail())) {
                    issues[0].add(Valid.issues.NOT_FOUND);
                }
                break;
            case Valid.UNIQUE:
                if (!emailIsValid(request.getEmail())) {
                    issues[0].add(Valid.issues.INVALID);
                }
                if (userExists(request.getEmail())) {
                    issues[0].add(Valid.issues.NOT_UNIQUE);
                }
                break;
        }
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
        return passwordEncoder.encode(rawPassword);
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

    public boolean adminEditIsValid(UserAdminBody userAdminBody, User user) {
        return userAdminBody.getUser_id() != null &&
                userAdminBody.getUser_id().equals(user.getId()) &&
                userAdminBody.getEmail() != null &&
                userAdminBody.getUserRole().equals(UserRoles.USER) ||
                userAdminBody.getUserRole().equals(UserRoles.ADMIN);
    }

    public void updateUser(UserAdminBody userAdminBody) { //TODO this will produce a NPE if called without ensuring the id exists (currently enforced)
        User user = userRepository.findOne(userAdminBody.getUser_id());
        user.setEmail(userAdminBody.getEmail());
        user.setFirstName(userAdminBody.getFirstName());
        user.setLastName(userAdminBody.getLastName());
        user.setUserRole(userAdminBody.getUserRole());
        user.setEnabled(userAdminBody.isEnabled());
        userRepository.save(user);
    }

    public void restartUserIdSeq() {
        em.createNativeQuery("ALTER SEQUENCE user_id_seq RESTART WITH 2")
                .executeUpdate();
    }

    public ResponseEntity<NotAuthenticatedErrorResponse> respondWithUnauthorized(int authResult) {
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


    private ResponseEntity respondWithBadRequest() { //TODO this is the same as in contactcontroller, also now baseservice
        BadRequestErrorResponse badRequestErrorResponse =
                new BadRequestErrorResponse(
                        new Error("Data error", "Data did not match required format."));
        return new ResponseEntity<>(badRequestErrorResponse,
                generateHeaders(),
                HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity showEditingResults(UserAdminBody userAdminBody, User user) {
        if (user != null) {
            return (adminEditIsValid(userAdminBody, user)) ?
                    showEditingOKResults(userAdminBody) :
                    respondWithBadRequest();
        } else return respondWithItemNotFound();
    }

    private ResponseEntity showEditingOKResults(UserAdminBody userAdminBody) {
        updateUser(userAdminBody);
        return new ResponseEntity<>(userAdminBody,
                generateHeaders(),
                HttpStatus.OK);
    }

    public ResponseEntity<MultipleUserResponse> respondWithAllUsers() {
        List<User> allUsers = obtainAllUsers();
        MultipleUserResponse multipleUserResponse = new MultipleUserResponse(allUsers);
        return new ResponseEntity<>(multipleUserResponse,
                generateHeaders(),
                HttpStatus.OK);
    }

    private ResponseEntity respondWithSingleUser(User user) {
        return new ResponseEntity<>(new UserAdminBody(user),
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