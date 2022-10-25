package com.yrol.blog.service;

import com.yrol.blog.dto.PostDto;

import java.util.List;

public interface PostService {

    PostDto createPost(PostDto postDto);

    List<PostDto> getAllPosts();

    PostDto findPostById(Long id);

}
