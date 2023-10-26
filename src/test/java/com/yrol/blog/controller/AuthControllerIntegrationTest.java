package com.yrol.blog.controller;

import com.yrol.blog.entity.Role;
import com.yrol.blog.repository.RoleRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.junit.jupiter.api.Assertions;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerIntegrationTest {

    @Autowired
    RoleRepository roleRepository;

    /**
     * Using TestRestTemplate for sending HTTP request
     **/
    @Autowired
    private TestRestTemplate testRestTemplate;

    JSONObject jsonObject;

    @BeforeEach
    void setUp() {
        String roleName = "ROLE_ADMIN";
        Role role = new Role();
        role.setName(roleName);
        roleRepository.save(role);
    }

    @Test
    @DisplayName("User can be created")
    void testSignUpUser_whenValidDetailsProvided_aNewUserWillBeCreated() throws JSONException {

        // Arrange and Act
        ResponseEntity<String> createUserResponse = signUpUser("Yrol", "yrol", "yrol@test.com", "abc123");

        // Assert
        Assertions.assertEquals(HttpStatus.OK, createUserResponse.getStatusCode());
        Assertions.assertEquals("User registered successfully", createUserResponse.getBody());
    }

    @Test
    @DisplayName("User can login using the username")
    void testLoginUser_whenValidUsernameAndPassword_userWillBeAuthenticatedSuccessfully() throws JSONException {
        // Arrange
        String name = "Yrol";
        String username = "yrol";
        String email = "yrol@test.com";
        String password = "abc123";

        this.signUpUser(name, username, email, password);

        JSONObject userLoginDetails = new JSONObject();
        userLoginDetails.put("usernameOrEmail", username);
        userLoginDetails.put("password", password);

        // Act
        HttpEntity<String> loginRequest = this.constructHttpRequest(userLoginDetails.toString());
        ResponseEntity<String> loginUserResponse = testRestTemplate.postForEntity("/api/v1/auth/signin", loginRequest,
                String.class);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, loginUserResponse.getStatusCode());
    }

    @Test
    @DisplayName("User can login using the email")
    void testSignInUser_whenValidEmailAndPassword_userWillBeAuthenticatedSuccessfully() throws JSONException {
        // Arrange
        String name = "Yrol";
        String username = "yrol";
        String email = "yrol@test.com";
        String password = "abc123";

        this.signUpUser(name, username, email, password);

        JSONObject userLoginDetails = new JSONObject();
        userLoginDetails.put("usernameOrEmail", email);
        userLoginDetails.put("password", password);

        // Act
        HttpEntity<String> loginRequest = this.constructHttpRequest(userLoginDetails.toString());
        ResponseEntity<String> loginUserResponse = testRestTemplate.postForEntity("/api/v1/auth/signin", loginRequest,
                String.class);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, loginUserResponse.getStatusCode());
    }

    @Test
    @DisplayName("User attempting to login with invalid credentials")
    void testSignInUser_whenInvalidEmailAndPassword_userWillNotBeAuthenticatedSuccessfully() throws JSONException {
        // Arrange
        String name = "Yrol";
        String username = "yrol";
        String email = "yrol@test.com";
        String password = "abc123";

        this.signUpUser(name, username, email, password);

        JSONObject userLoginDetails = new JSONObject();
        userLoginDetails.put("usernameOrEmail", "james");
        userLoginDetails.put("password", password);

        // Act
        HttpEntity<String> loginRequest = this.constructHttpRequest(userLoginDetails.toString());
        ResponseEntity<String> loginUserResponse = testRestTemplate.postForEntity("/api/v1/auth/signin", loginRequest,
                String.class);

        try {
            jsonObject = new JSONObject(loginUserResponse.getBody());
        } catch (JSONException err) {
            Assertions.fail(String.format("Response string to JSON conversion error: %s", err.getMessage()));
        }

        // Assert
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, loginUserResponse.getStatusCode());
        Assertions.assertEquals("Bad credentials", jsonObject.getString("message"));
    }

    private ResponseEntity<String> signUpUser(String name, String username, String email, String password)
            throws JSONException {
        JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("name", name);
        userDetailsRequestJson.put("username", username);
        userDetailsRequestJson.put("email", email);
        userDetailsRequestJson.put("password", password);

        HttpEntity<String> signUpRequest = this.constructHttpRequest(userDetailsRequestJson.toString());

        return testRestTemplate.postForEntity("/api/v1/auth/signup", signUpRequest, String.class);
    }

    private HttpEntity<String> constructHttpRequest(String bodyData) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(bodyData, httpHeaders);
    }
}