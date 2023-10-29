package com.yrol.blog.controller.webLayer;

import com.yrol.blog.service.implementation.PostServiceImpl;
import com.yrol.blog.utils.AppConstants;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.yrol.blog.controller.PostController;
import com.yrol.blog.dto.PostDto;
import com.yrol.blog.dto.PostResponse;
import com.yrol.blog.entity.Post;

@WebMvcTest(controllers = PostController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PostControllerWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Using MockBean to mock the PostService layer. This will be added to the
     * Spring context.
     */
    @MockBean
    PostServiceImpl postService;

    Post post;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId((long) 1);
        post.setTitle("Lamborghini Countach");
        post.setDescription(
                "The Lamborghini Countach is a rear mid-engine, rear-wheel-drive sports car produced by the Italian automobile manufacturer Lamborghini from 1974 until 1990.");
        post.setContent(
                "The style was introduced to the public in 1970 as the Lancia Stratos Zero concept car. The first showing of the Countach prototype was at the 1971 Geneva Motor Show, as the Lamborghini LP500 concept.");
    }

    @Test
    @DisplayName("Create Post successfully")
    void testCreatePost_whenValidPostDetailsProvided_returnCreatedPostDetails() throws Exception {
        MvcResult mvcResult = this.sendMockRequestAndReturnResult();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        PostDto createdPost = new ObjectMapper().readValue(responseBodyAsString, PostDto.class);

        Assertions.assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus(),
                "Incorrect HTTP Status Code returned.");
        Assertions.assertEquals(post.getTitle(), createdPost.getTitle());
    }

    @Test
    @DisplayName("Create Post throws error on invalid input")
    void testCreatePost_whenInvalidPostDetailsProvided_returnsError() throws Exception {
        post.setTitle(null);
        MvcResult mvcResult = this.sendMockRequestAndReturnResult();
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus(),
                "Incorrect HTTP Status Code returned.");
    }

    @Test
    @DisplayName("Update Post successfully")
    void testUpdatePost_whenValidPostDetailsProvided_returnsUpdatedPostDetails() throws Exception {

        // Arrange
        PostDto postDto = new ModelMapper().map(post, PostDto.class);

        // Act
        when(postService.updatePost(any(PostDto.class), anyLong())).thenReturn(postDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/v1/posts/" + post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(post));

        MvcResult getUpdatedResult = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        Assertions.assertEquals(HttpStatus.OK.value(), getUpdatedResult.getResponse().getStatus(),
                "Incorrect HTTP Status Code returned.");
    }

    @Test
    @DisplayName("Attempting to update post without Title or Description")
    void testUpdatePost_whenIncompletePostDetailsProvided_returnsError() throws Exception {
        // Arrange
        post.setTitle("");
        post.setDescription("");
        PostDto postDto = new ModelMapper().map(post, PostDto.class);

        // Act
        when(postService.updatePost(any(PostDto.class), anyLong())).thenReturn(postDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/v1/posts/" + post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(post));

        MvcResult getUpdatedResult = mockMvc.perform(requestBuilder).andReturn();

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), getUpdatedResult.getResponse().getStatus(),
                "Incorrect HTTP Status Code returned.");
    }

    @Test
    @DisplayName("Fetch a post by ID")
    void testFetchPost_whenValidPostIdProvided_returnsExistingPost() throws Exception {

        // Arrange
        PostDto postDto = new ModelMapper().map(post, PostDto.class);
        when(postService.findPostById((long) 1)).thenReturn(postDto);

        RequestBuilder getRequestBuilder = MockMvcRequestBuilders.get("/api/v1/posts/" + post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // Act
        MvcResult getPostResult = mockMvc.perform(getRequestBuilder).andReturn();
        String getResponseBodyAsString = getPostResult.getResponse().getContentAsString();
        PostDto getPostDto = new ObjectMapper().readValue(getResponseBodyAsString, PostDto.class);

        Assertions.assertEquals(HttpStatus.OK.value(), getPostResult.getResponse().getStatus(),
                "Incorrect HTTP Status Code returned.");
        Assertions.assertEquals(post.getTitle(), getPostDto.getTitle(),
                "Post title does not match");
    }

    @Test
    @DisplayName("Attempting to fetch a post using an invalid ID")
    void testFetchPost_whenInvalidPostIdProvided_returnsNothing() throws Exception {

        PostDto postDto = new ModelMapper().map(post, PostDto.class);
        when(postService.findPostById((long) 1)).thenReturn(postDto);

        RequestBuilder getRequestBuilder = MockMvcRequestBuilders.get("/api/v1/posts/" + (post.getId() + 1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // Act
        MvcResult getPostResult = mockMvc.perform(getRequestBuilder).andReturn();
        String getResponseBodyAsString = getPostResult.getResponse().getContentAsString();
        Assertions.assertEquals("", getResponseBodyAsString);
    }

    @Test
    @DisplayName("Search post by using a related keyword")
    void testFetchPost_whenValidKeywordsProvided_returnsPosts() throws Exception {

        List<PostDto> listOfPost = new ArrayList<>();
        listOfPost.add(new ModelMapper().map(post, PostDto.class));

        PostResponse postResponse = new PostResponse(listOfPost, 0, 10, 1, 1, true);

        /**
         * Mocking searchPost to return a single post when searched with the keyword
         * Countach
         */
        when(postService.searchPosts(0, 10, AppConstants.DEFAULT_SORT_BY, AppConstants.DEFAULT_SORT_DIRECTION,
                "Countach")).thenReturn(postResponse);

        /**
         * Matching the exact keyword in above mock: Countach and hitting the endpoint
         */
        RequestBuilder getRequestBuilder = MockMvcRequestBuilders.get("/api/v1/posts/search?query=Countach")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult postResult = mockMvc.perform(getRequestBuilder).andReturn();
        String responseBodyAsString = postResult.getResponse().getContentAsString();
        Assertions.assertNotNull(responseBodyAsString);
    }

    @Test
    @DisplayName("Search post by using an unrelated keyword")
    void testFetchPost_whenUnrelatedKeywordsProvided_nothingIsReturned() throws Exception {

        List<PostDto> listOfPost = new ArrayList<>();
        listOfPost.add(new ModelMapper().map(post, PostDto.class));

        PostResponse postResponse = new PostResponse(listOfPost, 0, 10, 1, 1, true);

        /**
         * Mocking searchPost to return a single post when searched with the keyword
         * Countach
         */
        when(postService.searchPosts(0, 10, AppConstants.DEFAULT_SORT_BY, AppConstants.DEFAULT_SORT_DIRECTION,
                "Countach")).thenReturn(postResponse);

        /**
         * Attempting to match the using an unrelated keyword: toyota
         */
        RequestBuilder getRequestBuilder = MockMvcRequestBuilders.get("/api/v1/posts/search?query=toyota")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult postResult = mockMvc.perform(getRequestBuilder).andReturn();
        String responseBodyAsString = postResult.getResponse().getContentAsString();
        Assertions.assertEquals("", responseBodyAsString);
    }

    @Test
    @DisplayName("Get all posts")
    void testFetchPost_whenMetadataIsProvided_returnsPosts() throws Exception {

        Post anotherPost = new Post();
        anotherPost.setTitle("Ferarri f40");
        anotherPost.setDescription("This is about Ferarri f40");
        anotherPost.setContent(
                "The Ferrari F40 (Type F120) is a mid-engine, rear-wheel drive sports car engineered by Nicola Materazzi with styling by Pininfarina");

        List<PostDto> listOfPost = new ArrayList<>();
        listOfPost.add(new ModelMapper().map(post, PostDto.class));
        listOfPost.add(new ModelMapper().map(anotherPost, PostDto.class));

        PostResponse postResponse = new PostResponse(listOfPost, 0, 10, 2, 1, true);

        /**
         * Mocking searchPost to return a single post when searched with the keyword
         * Countach
         */
        when(postService.getAllPosts(0, 10, AppConstants.DEFAULT_SORT_BY, AppConstants.DEFAULT_SORT_DIRECTION))
                .thenReturn(postResponse);

        /**
         * Matching the exact keyword in above mock: Countach and hitting the endpoint
         */
        RequestBuilder getRequestBuilder = MockMvcRequestBuilders.get("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult postResult = mockMvc.perform(getRequestBuilder).andReturn();
        Assertions.assertEquals(HttpStatus.OK.value(), postResult.getResponse().getStatus());
    }

    /*
     * Mocking the Post creation and return the mock Post
     */
    private MvcResult sendMockRequestAndReturnResult() throws Exception {
        PostDto postDto = new ModelMapper().map(post, PostDto.class);
        when(postService.createPost(any(PostDto.class))).thenReturn(postDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(post));

        return mockMvc.perform(requestBuilder).andReturn();
    }
}