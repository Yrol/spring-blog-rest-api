package com.yrol.blog.service;

import com.yrol.blog.dto.PostDto;
import com.yrol.blog.dto.PostResponse;

public interface PostService {

    PostDto createPost(PostDto postDto);

    PostResponse getAllPosts(int page, int size, String sortBy, String sortDir);

    PostDto findPostById(Long id);

    PostDto updatePost(PostDto postDto, Long id);

    PostResponse searchPosts(int page, int size, String sortBy, String sortDir, String query);

    void deletePost(Long id);
}
