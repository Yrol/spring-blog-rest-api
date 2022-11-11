package com.yrol.blog.service.implementation;

import com.yrol.blog.dto.PostDto;
import com.yrol.blog.dto.PostResponse;
import com.yrol.blog.entity.Post;
import com.yrol.blog.entity.User;
import com.yrol.blog.exception.ResourceNotFoundException;
import com.yrol.blog.repository.PostRepository;
import com.yrol.blog.repository.UserRepository;
import com.yrol.blog.service.PostService;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    private ModelMapper mapper;

    private UserRepository userRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, ModelMapper mapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public PostDto createPost(PostDto postDto) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username = userDetails.getUsername();

        Optional<User> user = userRepository.findByEmail(username);

        Post post = this.mapToEntity(postDto);

        post.setUser(user.get());

        // save to DB
        Post newPost = postRepository.save(post);

        // Convert Entity to DTO
        PostDto postResponse = this.mapToDto(newPost);

        return postResponse;
    }

    @Override
    public PostResponse getAllPosts(int page, int size, String sortBy, String sortDir) {

        // Ternary condition to determine sorting to be by ASC or by DESC
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Post> posts = postRepository.findAll(pageable);

        List<Post> listOfPost = posts.getContent();

        // using Lambda Expressions and mapToDto custom function to map
        List<PostDto> content = listOfPost.stream().map(post -> mapToDto(post)).collect(Collectors.toList());

        return new PostResponse(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
    }

    @Override
    public PostDto findPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(id)));
        return this.mapToDto(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", String.valueOf(id)));
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setDescription(postDto.getDescription());

        Post updatedPost = postRepository.save(post);

        return this.mapToDto(updatedPost);
    }

    @Override
    public PostResponse searchPosts(int page, int size, String sortBy, String sortDir, String query) {

        StringBuilder words = new StringBuilder();

        List<String> searchTerms = Arrays.stream(query.split(" ")).collect(Collectors.toList());

        if (searchTerms.size() == 1 && StringUtils.isNotEmpty(searchTerms.get(0))) {
            words.append("%").append(searchTerms.get(0)).append("%");
        } else {
            searchTerms.stream()
                    .filter(StringUtils::isNotEmpty)
                    .forEach(s -> {
                        words.append("%").append(s).append("%");
                    });
        }

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Post> posts = postRepository.searchPosts(words.toString(), pageable);

        List<Post> listOfPost = posts.getContent();

        List<PostDto> content = listOfPost.stream().map(post -> mapToDto(post)).collect(Collectors.toList());

        return new PostResponse(content, posts.getNumber(), posts.getSize(), posts.getTotalElements(), posts.getTotalPages(), posts.isLast());
    }

    @Override
    public void deletePost(Long id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
        }
    }

    private PostDto mapToDto(Post post) {

        // Method 1: map using setters and getters
//        PostDto postDto = new PostDto();
//        postDto.setId(post.getId());
//        postDto.setTitle(post.getTitle());
//        postDto.setDescription(post.getDescription());
//        postDto.setContent(post.getContent());

        // Method 2: using the ModelMapper lib
        PostDto postDto = mapper.map(post, PostDto.class);

        return postDto;
    }

    private Post mapToEntity(PostDto postDto) {

        // Method 1: map using setters and getters
//        Post post = new Post();
//        post.setTitle(postDto.getTitle());
//        post.setDescription(postDto.getDescription());
//        post.setContent(postDto.getTitle());


        // Method 2: using the ModelMapper lib
        Post post = mapper.map(postDto, Post.class);

        return post;
    }
}
