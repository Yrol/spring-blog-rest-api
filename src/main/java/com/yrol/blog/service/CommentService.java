package com.yrol.blog.service;

import com.yrol.blog.dto.CommentDto;

public interface CommentService {

    CommentDto createComment(long postId, CommentDto  commentDto);
}
