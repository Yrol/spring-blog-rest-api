package com.yrol.blog.controller.webLayer;

import com.yrol.blog.service.implementation.CommentServiceImpl;

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

import com.yrol.blog.controller.CommentController;
import com.yrol.blog.dto.CommentDto;
import com.yrol.blog.entity.Comment;
import com.yrol.blog.entity.Post;

@WebMvcTest(controllers = CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Using MockBean to mock the PostService layer. This will be added to the
     * Spring context.
     */
    @MockBean
    CommentServiceImpl commentService;

    Comment comment;

    Post post;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        comment.setName("Yrol");
        comment.setBody("Great car");
        comment.setEmail("yrol@test.com");
    }

    @Test
    @DisplayName("Adding a comment successfully")
    void testCreateComment_whenValidCommentDetailsProvided_returnCreatedComment() throws Exception {

        CommentDto commentDto = new ModelMapper().map(comment, CommentDto.class);
        when(commentService.createComment(anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(String.format("/api/v1/posts/%s/comments", 1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(commentDto));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        CommentDto commentCreated = new ObjectMapper().readValue(responseBodyAsString, CommentDto.class);

        Assertions.assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus(),
                "Incorrect HTTP Status Code returned.");
        Assertions.assertEquals(comment.getEmail(), commentCreated.getEmail(), "Comment info doesn't match");

    }

    @Test
    @DisplayName("Get comment by Id")
    void testFetchComment_whenValidCommentIdProvided_returnComment() throws Exception {
        CommentDto commentDto = new ModelMapper().map(comment, CommentDto.class);
        when(commentService.getCommentById(anyLong(), anyLong())).thenReturn(commentDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(String.format("/api/v1/posts/%s/comments/%s", 1, 1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        CommentDto commentById = new ObjectMapper().readValue(responseBodyAsString, CommentDto.class);

        Assertions.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(),
                "Incorrect HTTP Status Code returned.");
        Assertions.assertEquals(comment.getEmail(), commentById.getEmail(), "Comment info doesn't match");
    }

    @Test
    @DisplayName("Get all comments belong to a post")
    void testFetchComments_whenValidPostIdProvided_returnAllComments() throws Exception {

        Comment anotherComment = new Comment();
        anotherComment.setName("James");
        anotherComment.setBody("Unreliable car");
        anotherComment.setEmail("james@test.com");

        List<CommentDto> listOfComments = new ArrayList<>();
        listOfComments.add(new ModelMapper().map(comment, CommentDto.class));
        listOfComments.add(new ModelMapper().map(anotherComment, CommentDto.class));

        when(commentService.getCommentsByPostId(anyLong())).thenReturn(listOfComments);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(String.format("/api/v1/posts/%s/comments", 1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(),
                "Incorrect HTTP Status Code returned.");
        Assertions.assertNotNull(responseBodyAsString);

    }

    @Test
    @DisplayName("Update comment")
    void testUpdateComment_whenValidPostIdAndCommentIdProvided_returnUpdatedComment() throws Exception {
        CommentDto commentDto = new ModelMapper().map(comment, CommentDto.class);
        when(commentService.updateCommentById(any(CommentDto.class), anyLong(), anyLong())).thenReturn(commentDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch(String.format("/api/v1/posts/%s/comments/%s", 1, 1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(commentDto));

        MvcResult getUpdatedResult = mockMvc.perform(requestBuilder).andReturn();

        Assertions.assertEquals(HttpStatus.OK.value(), getUpdatedResult.getResponse().getStatus(),
                "Incorrect HTTP Status Code returned.");
    }

}
