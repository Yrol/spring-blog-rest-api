package com.yrol.blog.service;

import com.yrol.blog.dto.CommentDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(long postId, CommentDto  commentDto);

    List<CommentDto> getCommentsByPostId(long postId);

    CommentDto getCommentById(long postId, long commentId);

    CommentDto updateCommentById(CommentDto commentDto, long postId, long commentId);

    void deleteCommentById(long postId, long commentId);
}
