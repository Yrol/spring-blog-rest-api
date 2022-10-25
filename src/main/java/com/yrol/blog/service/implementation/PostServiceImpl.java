package com.yrol.blog.service.implementation;

import com.yrol.blog.dto.PostDto;
import com.yrol.blog.entity.Post;
import com.yrol.blog.exception.ResourceNotFoundException;
import com.yrol.blog.repository.PostRepository;
import com.yrol.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public PostDto createPost(PostDto postDto) {

        // save to DB
        Post newPost = postRepository.save(this.mapToEntity(postDto));

        // Convert Entity to DTO
        PostDto postResponse = this.mapToDto(newPost);

        return postResponse;
    }

    @Override
    public List<PostDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();

        // using Lambda Expressions and mapToDto custom function to map
        return posts.stream().map(post -> mapToDto(post)).collect(Collectors.toList());
    }

    @Override
    public PostDto findPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(id)));
        return this.mapToDto(post);
    }

    private PostDto mapToDto(Post post) {
        PostDto postResponse = new PostDto();
        postResponse.setId(post.getId());
        postResponse.setTitle(post.getTitle());
        postResponse.setDescription(post.getDescription());
        postResponse.setContent(post.getContent());

        return postResponse;
    }

    private Post mapToEntity(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getTitle());

        return post;
    }
}
