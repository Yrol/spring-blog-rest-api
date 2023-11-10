package com.yrol.blog.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
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

import com.yrol.blog.dto.PostDto;
import com.yrol.blog.dto.PostResponse;
import com.yrol.blog.entity.Category;
import com.yrol.blog.entity.Post;
import com.yrol.blog.entity.Role;
import com.yrol.blog.entity.User;
import com.yrol.blog.exception.BlogAPIException;
import com.yrol.blog.repository.CategoryRepository;
import com.yrol.blog.repository.PostRepository;
import com.yrol.blog.repository.RoleRepository;
import com.yrol.blog.repository.UserRepository;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostControllerIntegrationTest {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

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

    Category category;

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

        category = new Category();
        category.setId((long) 1);
        category.setTitle("Ships");
        category.setDescription("This is about ships");
        categoryRepository.save(category);
    }

    @Test
    @DisplayName("Post can be created")
    void testCreatePost_whenValidDetailsProvided_returnNewPost() throws JSONException {

        // Arrange
        String accessToken = this.signInUser();

        if (accessToken.isEmpty()) {
            Assertions.fail();
        }

        JSONObject postCreateDetails = new JSONObject();
        postCreateDetails.put("title", "Seawise Giant");
        postCreateDetails.put("description", "This is about Seawise Giant");
        postCreateDetails.put("content",
                "The biggest ship in the world by gross tonnage is the crane vessel Pioneering Spirit at a staggering 403,342 GT");
        postCreateDetails.put("category", new JSONObject(String.format("{ \"id\": %s }", category.getId()).toString()));

        HttpHeaders headers = this.setBasicHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(postCreateDetails.toString(), headers);

        // Act
        ResponseEntity<PostDto> newPost = testRestTemplate.postForEntity("/api/v1/posts",
                request, PostDto.class);

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED.value(), newPost.getStatusCode().value());
        Assertions.assertEquals(postCreateDetails.get("title"), newPost.getBody().getTitle());
    }

    @Test
    @DisplayName("Attempting to create post using an invalid category")
    void testCreatePost_whenInvalidCategoryProvided_returnError() throws JSONException {
        // Arrange
        String accessToken = this.signInUser();

        if (accessToken.isEmpty()) {
            Assertions.fail();
        }

        JSONObject postCreateDetails = new JSONObject();
        postCreateDetails.put("title", "Seawise Giant");
        postCreateDetails.put("description", "This is about Seawise Giant");
        postCreateDetails.put("content",
                "The biggest ship in the world by gross tonnage is the crane vessel Pioneering Spirit at a staggering 403,342 GT");
        postCreateDetails.put("category", new JSONObject(String.format("{ \"id\": %s }", 10).toString()));

        HttpHeaders headers = this.setBasicHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(postCreateDetails.toString(), headers);

        // Act
        ResponseEntity<PostDto> newPost = testRestTemplate.postForEntity("/api/v1/posts",
                request, PostDto.class);

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, newPost.getStatusCode());
    }

    @Test
    @DisplayName("Attempting to create post without auth token")
    void testCreatePost_whenNoAuthTokenProvided_returnError() throws JSONException {
        JSONObject postCreateDetails = new JSONObject();
        postCreateDetails.put("title", "Seawise Giant");
        postCreateDetails.put("description", "This is about Seawise Giant");
        postCreateDetails.put("content",
                "The biggest ship in the world by gross tonnage is the crane vessel Pioneering Spirit at a staggering 403,342 GT");
        postCreateDetails.put("category", new JSONObject(String.format("{ \"id\": %s }", 10).toString()));

        HttpHeaders headers = this.setBasicHeaders();
        HttpEntity<String> request = new HttpEntity<>(postCreateDetails.toString(), headers);

        // Act
        ResponseEntity<PostDto> newPost = testRestTemplate.postForEntity("/api/v1/posts",
                request, PostDto.class);

        // Assert
        Assertions.assertEquals(HttpStatus.FORBIDDEN, newPost.getStatusCode());
    }

    @Test
    @DisplayName("Attempting to create a post without the mandatory field: Title")
    void testCreatePost_whenNoTitleProvided_returnError() throws JSONException {
        // Arrange
        String accessToken = this.signInUser();

        if (accessToken.isEmpty()) {
            Assertions.fail();
        }

        JSONObject postCreateDetails = new JSONObject();
        postCreateDetails.put("title", "");
        postCreateDetails.put("description", "This is about Seawise Giant");
        postCreateDetails.put("content",
                "The biggest ship in the world by gross tonnage is the crane vessel Pioneering Spirit at a staggering 403,342 GT");
        postCreateDetails.put("category", new JSONObject(String.format("{ \"id\": %s }", category.getId()).toString()));

        HttpHeaders headers = this.setBasicHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(postCreateDetails.toString(), headers);

        // Act
        ResponseEntity<PostDto> newPost = testRestTemplate.postForEntity("/api/v1/posts",
                request, PostDto.class);

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, newPost.getStatusCode());
        // Assertions.assertEquals("must not be blank", newPost.getBody().getTitle());
    }

    @Test
    @DisplayName("Attempting to create post with same title")
    void testCreatePost_whenExistingTitleNameProvided_returnError() throws JSONException {
        // Arrange
        String postTitle = "Titanic";
        String postDescription = "This is about Titanic";
        String postContent = "RMS Titanic was the largest ship afloat at the time she entered service and the second of three Olympic-class ocean liners built for the White Star Line";
        this.createAndSavePost(postTitle, postDescription, postContent, category, user);

        JSONObject postCreateDetails = new JSONObject();
        postCreateDetails.put("title", postTitle);
        postCreateDetails.put("description", "This is about Titanic 2012");
        postCreateDetails.put("content",
                "The Titanic was a luxury British steamship that sank in the early hours of April 15, 1912 after striking an iceberg");
        postCreateDetails.put("category", new JSONObject(String.format("{ \"id\": %s }", category.getId()).toString()));

        String accessToken = this.signInUser();

        if (accessToken.isEmpty()) {
            Assertions.fail();
        }

        HttpHeaders headers = this.setBasicHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(postCreateDetails.toString(), headers);

        // Act
        ResponseEntity<BlogAPIException> newPost = testRestTemplate.postForEntity("/api/v1/posts",
                request, BlogAPIException.class);

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, newPost.getStatusCode());
        Assertions.assertEquals("Post with the same Title already exists. Please choose a different title.",
                newPost.getBody().getMessage());

    }

    @Test
    @DisplayName("Update a Post")
    void testUpdatePosts_whenValidPostDetailsProvided_returnUpdatedPost() throws JSONException {

        String postTitle = "Bismarck";
        String postDescription = "Bismarck";
        String postContent = "Bismarck, German battleship of World War II that had a short but spectacular career";
        Post savedPost = this.createAndSavePost(postTitle, postDescription, postContent, category, user);

        // Setup required for PATCH since TestRestTemplate doesn't support it out of the
        // box
        patchRestTemplate = testRestTemplate.getRestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

        String accessToken = this.signInUser();

        if (accessToken.isEmpty()) {
            Assertions.fail();
        }

        String newTitle = "Bismarck 1939 - 1941";
        JSONObject postUpdateDetails = new JSONObject();
        postUpdateDetails.put("title", newTitle);
        postUpdateDetails.put("description", "This is about Bismarck");
        postUpdateDetails.put("content",
                "Bismarck, German battleship of World War II that had a short but spectacular career");

        HttpHeaders headers = this.setBasicHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(postUpdateDetails.toString(), headers);

        // Act
        ResponseEntity<PostDto> getUpdatedResponse = patchRestTemplate.exchange(
                "/api/v1/posts/" + savedPost.getId(),
                HttpMethod.PATCH,
                request,
                new ParameterizedTypeReference<PostDto>() {
                });

        Assertions.assertEquals(HttpStatus.OK, getUpdatedResponse.getStatusCode());
        Assertions.assertEquals(newTitle, getUpdatedResponse.getBody().getTitle());
    }

    @Test
    @DisplayName("Find post by ID")
    void testFindPost_whenValidPostIdProvided_returnPost() throws JSONException {

        String postTitle = "USS Ronald Reagan";
        String postDescription = "This is about USS Ronald Reagan";
        String postContent = "USS Ronald Reagan is a Nimitz-class, nuclear-powered supercarrier in the service of the United States Navy.";

        Post savedPost = this.createAndSavePost(postTitle, postDescription, postContent, category, user);

        ResponseEntity<PostDto> getPostResponse = testRestTemplate.exchange("/api/v1/posts/" + savedPost.getId(),
                HttpMethod.GET,
                new HttpEntity<>(this.setBasicHeaders()),
                new ParameterizedTypeReference<PostDto>() {
                });

        Assertions.assertEquals(HttpStatus.OK, getPostResponse.getStatusCode());
        Assertions.assertEquals(postTitle, getPostResponse.getBody().getTitle());
    }

    @Test
    @DisplayName("Get all posts")
    void testGetAllPosts_whenRequestedAllPosts_returnPosts() throws JSONException {
        // Arrange
        // Post 1
        String postOneTitle = "MV Aurora";
        String postOneDescription = "This is about MV Aurora";
        String postOneContent = "MV Aurora is a cruise ship of the P&O Cruises fleet. The ship was built by Meyer Werft at their shipyard in Papenburg, Germany. At over 76,000 tonnes, Aurora is the smallest and oldest of seven ships currently in service with P&O Cruises";
        this.createAndSavePost(postOneTitle, postOneDescription, postOneContent, category, user);

        // Post 2
        String postTwoTitle = "MS Arcadia";
        String postTwoDescription = "This is about MS Arcadia";
        String postTwoContent = "MS Arcadia is a cruise ship in the P&O Cruises fleet. The ship was built by Fincantieri at their shipyard in Marghera, Italy";
        this.createAndSavePost(postTwoTitle, postTwoDescription, postTwoContent, category, user);

        // Act
        ResponseEntity<PostResponse> getPostResponse = testRestTemplate.exchange("/api/v1/posts",
                HttpMethod.GET,
                new HttpEntity<>(this.setBasicHeaders()),
                new ParameterizedTypeReference<PostResponse>() {
                });

        // Assert
        Assertions.assertEquals(HttpStatus.OK, getPostResponse.getStatusCode());
        Assertions.assertEquals(2, getPostResponse.getBody().getContent().size());
    }

    @Test
    @DisplayName("Search post by title")
    void testSearchPostByTitle_whenValidQueryProvided_returnPost() throws JSONException {

        // Arrange
        // Post 1
        String postOneTitle = "MV Britannia";
        String postOneDescription = "This is about MV Britannia";
        String postOneContent = "MV Britannia is a cruise ship of the P&O Cruises fleet. She was built by Fincantieri at its shipyard in Monfalcone, Italy";
        this.createAndSavePost(postOneTitle, postOneDescription, postOneContent, category, user);

        // Post 2
        String postTwoTitle = "MS Iona";
        String postTwoDescription = "This is about MS Iona";
        String postThreeContent = "MS Iona is an Excellence-class cruise ship in service for P&O Cruises, a subsidiary of Carnival Corporation & plc";
        this.createAndSavePost(postTwoTitle, postTwoDescription, postThreeContent, category, user);

        // Act
        ResponseEntity<PostResponse> getPostResponse = testRestTemplate.exchange("/api/v1/posts/search?query=brit",
                HttpMethod.GET,
                new HttpEntity<>(this.setBasicHeaders()),
                new ParameterizedTypeReference<PostResponse>() {
                });

        // Assert
        Assertions.assertEquals(HttpStatus.OK, getPostResponse.getStatusCode());
        Assertions.assertEquals(1, getPostResponse.getBody().getContent().size());
        Assertions.assertEquals(postOneTitle, getPostResponse.getBody().getContent().get(0).getTitle());
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

    private Post createAndSavePost(String title, String description, String content, Category category, User user) {
        Post anotherPost = new Post();
        anotherPost.setTitle(title);
        anotherPost.setDescription(description);
        anotherPost.setContent(content);
        anotherPost.setCategory(category);
        anotherPost.setUser(user);
        return postRepository.save(anotherPost);
    }
}
