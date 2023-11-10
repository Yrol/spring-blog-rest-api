package com.yrol.blog.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.yrol.blog.dto.CategoryDto;
import com.yrol.blog.entity.Category;
import com.yrol.blog.entity.Role;
import com.yrol.blog.entity.User;
import com.yrol.blog.repository.CategoryRepository;
import com.yrol.blog.repository.RoleRepository;
import com.yrol.blog.repository.UserRepository;

import org.junit.jupiter.api.Assertions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerIntegrationTest {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Using TestRestTemplate for sending HTTP request
     **/
    @Autowired
    private TestRestTemplate testRestTemplate;

    private RestTemplate patchRestTemplate;

    JSONObject jsonObject;

    User user;

    String password;

    @BeforeEach
    void setUp() {
        String roleName = "ROLE_ADMIN";
        Role role = new Role();
        role.setId(1);
        role.setName(roleName);
        if(roleRepository.findByName(roleName).isEmpty()) {
            roleRepository.save(role);
        }

        Set<Role> roles = new HashSet<Role>();
        roles.add(role);

        password = "12345678";

        user = new User();
        user.setId(1);
        user.setEmail("james@test.com");
        user.setName("James");
        user.setUsername("james");
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Test
    @DisplayName("Category can be created")
    void testCreateCategory_whenValidDetailsProvided_returnNewCategory() throws JSONException {

        // Arrange
        String accessToken = this.signInUser();

        if (accessToken.isEmpty()) {
            Assertions.fail();
        }

        JSONObject categoryCreateDetails = new JSONObject();
        categoryCreateDetails.put("title", "Ships");
        categoryCreateDetails.put("description", "This is about ships");

        HttpHeaders headers = this.setBasicHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(categoryCreateDetails.toString(), headers);

        // Act
        ResponseEntity<CategoryDto> newCategory = testRestTemplate.postForEntity("/api/v1/categories",
                request, CategoryDto.class);

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED.value(), newCategory.getStatusCode().value());
        Assertions.assertEquals(categoryCreateDetails.get("title"), newCategory.getBody().getTitle());
    }

    @Test
    @DisplayName("Attempting to create category without auth token")
    void testCreateCategory_whenNoAuthTokenProvided_returnError() throws JSONException {

        JSONObject categoryCreateDetails = new JSONObject();
        categoryCreateDetails.put("title", "Birds");
        categoryCreateDetails.put("description", "This is about birds");

        HttpHeaders headers = this.setBasicHeaders();
        HttpEntity<String> request = new HttpEntity<>(categoryCreateDetails.toString(), headers);

        // Act & Assert
        Assertions.assertEquals(HttpStatus.FORBIDDEN, testRestTemplate.postForEntity("/api/v1/categories",
                request, CategoryDto.class).getStatusCode());
    }

    @Test
    @DisplayName("Attempting to create a category without the mandatory field: Title")
    void testCreateCategory_whenNoTitleProvided_returnError() throws JSONException {

        // Arrange
        String accessToken = this.signInUser();

        if (accessToken.isEmpty()) {
            Assertions.fail();
        }

        JSONObject categoryCreateDetails = new JSONObject();
        categoryCreateDetails.put("title", "");
        categoryCreateDetails.put("description", "This is about cars");

        HttpHeaders headers = this.setBasicHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(categoryCreateDetails.toString(), headers);

        // Act
        ResponseEntity<CategoryDto> newCategory = testRestTemplate.postForEntity("/api/v1/categories",
                request, CategoryDto.class);

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), newCategory.getStatusCode().value());
    }

    @Test
    @DisplayName("Attempting to create category with same name")
    void testCreateCategory_whenExistingTitleNameProvided_returnError() throws JSONException {

        // Arrange
        String categoryTitle = "Vans";
        String categoryDescription = "This is about vans";

        Category category = new Category();
        category.setTitle(categoryTitle);
        category.setDescription(categoryDescription);
        categoryRepository.save(category);

        String accessToken = this.signInUser();

        if (accessToken.isEmpty()) {
            Assertions.fail();
        }

        JSONObject categoryCreateDetails = new JSONObject();
        categoryCreateDetails.put("title", categoryTitle);
        categoryCreateDetails.put("description", categoryDescription);

        HttpHeaders headers = this.setBasicHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(categoryCreateDetails.toString(), headers);

        // Act
        ResponseEntity<CategoryDto> newCategory = testRestTemplate.postForEntity("/api/v1/categories",
                request, CategoryDto.class);

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), newCategory.getStatusCode().value());
    }

    @Test
    @DisplayName("Fetch category by ID")
    void testFetchCategory_whenValidIdProvided_returnCategory() throws JSONException {
        // Arrange
        String categoryTitle = "Trucks";
        String categoryDescription = "This is about trucks";

        Category category = new Category();
        category.setId((long) 1);
        category.setTitle(categoryTitle);
        category.setDescription(categoryDescription);
        Category savedCategory = categoryRepository.save(category);

        // Act
        ResponseEntity<CategoryDto> getCategoryResponse = testRestTemplate.exchange(
                "/api/v1/categories/" + savedCategory.getId(),
                HttpMethod.GET,
                new HttpEntity<>(this.setBasicHeaders()),
                new ParameterizedTypeReference<CategoryDto>() {
                });

        // Assert
        Assertions.assertEquals(HttpStatus.OK, getCategoryResponse.getStatusCode());
        Assertions.assertEquals(categoryTitle, getCategoryResponse.getBody().getTitle());
    }

    @Test
    @DisplayName("Fetch all categories")
    void testFetchCategories_whenAllCategoriesRequested_returnCategories() throws JSONException {
        // Arrange
        Category category = new Category();
        category.setId((long) 1);
        category.setTitle("Boats");
        category.setDescription("This is about boats");
        categoryRepository.save(category);

        Category anotherCategory = new Category();
        anotherCategory.setId((long) 2);
        anotherCategory.setTitle("Bikes");
        anotherCategory.setDescription("This is about bikes");
        categoryRepository.save(anotherCategory);

        // Act
        ResponseEntity<List<CategoryDto>> getCategoriesResponse = testRestTemplate.exchange("/api/v1/categories",
                HttpMethod.GET,
                new HttpEntity<>(this.setBasicHeaders()),
                new ParameterizedTypeReference<List<CategoryDto>>() {
                });

        // Assert
        Assertions.assertEquals(HttpStatus.OK, getCategoriesResponse.getStatusCode());
        Assertions.assertEquals(category.getTitle(), getCategoriesResponse.getBody().get(0).getTitle());
    }

    @Test
    @DisplayName("Update a category")
    void testUpdateCategories_whenValidCategoryDetailsProvided_returnUpdatedCategory() throws JSONException {

        // Setup required for PATCH since TestRestTemplate doesn't support it out of the
        // box
        patchRestTemplate = testRestTemplate.getRestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

        // Arrange
        Category category = new Category();
        category.setTitle("Cars");
        category.setDescription("This is about cars");
        Category savedCategory = categoryRepository.save(category);

        String accessToken = this.signInUser();

        if (accessToken.isEmpty()) {
            Assertions.fail();
        }

        String newTitle = "Jets";
        JSONObject categoryCreateDetails = new JSONObject();
        categoryCreateDetails.put("title", newTitle);
        categoryCreateDetails.put("description", "This is about jets");

        HttpHeaders headers = this.setBasicHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(categoryCreateDetails.toString(), headers);

        // Act
        ResponseEntity<CategoryDto> getUpdatedResponse = patchRestTemplate.exchange(
                "/api/v1/categories/" + savedCategory.getId(),
                HttpMethod.PATCH,
                request,
                new ParameterizedTypeReference<CategoryDto>() {
                });

        // Assert
        Assertions.assertEquals(HttpStatus.OK, getUpdatedResponse.getStatusCode());
        Assertions.assertEquals(newTitle, getUpdatedResponse.getBody().getTitle());

    }

    private String signInUser() throws JSONException {

        JSONObject userLoginDetails = new JSONObject();
        userLoginDetails.put("usernameOrEmail", user.getEmail());
        userLoginDetails.put("password", password);

        HttpHeaders headers = this.setBasicHeaders();
        HttpEntity<String> loginRequest = new HttpEntity<>(userLoginDetails.toString(), headers);
        ResponseEntity<String> loginUserResponse = testRestTemplate.postForEntity("/api/v1/auth/signin", loginRequest,
                String.class);

        try {
            jsonObject = new JSONObject(loginUserResponse.getBody());
        } catch (JSONException err) {
            Assertions.fail(String.format("Response string to JSON conversion error: %s", err.getMessage()));
        }

        return jsonObject.getString("accessToken");
    }

    private HttpHeaders setBasicHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        return httpHeaders;
    }
}
