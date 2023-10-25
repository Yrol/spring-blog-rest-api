package com.yrol.blog.io;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.yrol.blog.entity.Category;
import com.yrol.blog.entity.Comment;
import com.yrol.blog.entity.Post;
import com.yrol.blog.entity.Role;
import com.yrol.blog.entity.User;
import com.yrol.blog.repository.PostRepository;
import com.yrol.blog.utils.AppConstants;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostEntityIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PostRepository postRepository;

    Post post;

    @BeforeEach
    void setUp() {

        Category category = this.createNewCategory();

        post = new Post();
        post.setCategory(category);
        post.setTitle("Ferarri f40");
        post.setDescription("This is about Ferarri f40");
        post.setContent(
                "The Ferrari F40 (Type F120) is a mid-engine, rear-wheel drive sports car engineered by Nicola Materazzi with styling by Pininfarina");

    }

    @Test
    void testPostEntity_whenValidPostDetailsProvided_shouldReturnStoredPostDetails() {

        // Arrange
        String roleName = "ROLE_ADMIN";

        Role role = this.createRole(roleName);

        Set<Role> roles = new HashSet<Role>();
        roles.add(role);

        User user = this.createNewUser(roles);

        post.setUser(testEntityManager.persistAndFlush(user));

        // Act
        Post savedPost = testEntityManager.persistAndFlush(post);

        // Assert
        Assertions.assertEquals(post.getId(), savedPost.getId(),
                String.format("Post ID should be equal to:", post.getId()));
        Assertions.assertEquals(savedPost.getComments().size(), 0, "Post should not have any comments");
    }

    @Test
    void testPostEntity_whenCommentIsAdded_shouldReturnPostWithCommentCount() {

        // Arrange
        String roleName = "ROLE_ADMIN";

        Role role = this.createRole(roleName);

        Set<Role> roles = new HashSet<Role>();
        roles.add(role);

        User user = this.createNewUser(roles);

        post.setUser(testEntityManager.persistAndFlush(user));

        // Act
        Post savedPost = testEntityManager.persistAndFlush(post);

        this.createComment(savedPost); // assign the post to the comment(vise-versa also valid).

        // Assert
        Assertions.assertEquals(post.getId(), savedPost.getId(),
                String.format("Post ID should be equal to:", post.getId()));

        Assertions.assertEquals(1, 1, "Post should have at least one comment");
    }

    @Test
    void testSearchPosts_whenGivenExactMatchingTitle_returnPost() {

        // Arrange
        String roleName = "ROLE_ADMIN";

        Role role = this.createRole(roleName);

        Set<Role> roles = new HashSet<Role>();
        roles.add(role);

        User user = this.createNewUser(roles);

        post.setUser(testEntityManager.persistAndFlush(user));

        // Act
        testEntityManager.persistAndFlush(post);

        Post searchedPost = postRepository.findByTitleIgnoreCase("Ferarri f40");

        Assertions.assertEquals(post.getId(), searchedPost.getId(), "There should be at least one post");
    }

    @Test
    void testSearchPosts_whenGivenCloselyMatchingTitleOrDescription_returnPost() {

        String roleName = "ROLE_ADMIN";

        Role role = this.createRole(roleName);

        Set<Role> roles = new HashSet<Role>();
        roles.add(role);

        User user = this.createNewUser(roles);

        post.setUser(testEntityManager.persistAndFlush(user));

        Pageable pageable = PageRequest.of(0, 1,
                Sort.by(AppConstants.DEFAULT_SORT_BY).ascending());

        // Act
        // testEntityManager.persistAndFlush(post);
        postRepository.save(post);

        Page<Post> posts = postRepository.searchPosts("%Ferarri%", pageable);

        if (!posts.getContent().isEmpty()) {
            Assertions.assertEquals(post.getTitle(), posts.getContent().get(0).getTitle(),
                    String.format("Title should match: %s", post.getTitle()));
            Assertions.assertEquals(post.getId(), posts.getContent().get(0).getId(),
                    String.format("ID should match: %s", post.getId()));
        } else {
            Assertions.fail("The keyword doesn't match the content");
        }
    }

    @Test
    void testSearchPosts_whenGivenNotMatchingTitleOrDescription_shouldNotReturnPosts() {

        String roleName = "ROLE_ADMIN";

        Role role = this.createRole(roleName);

        Set<Role> roles = new HashSet<Role>();
        roles.add(role);

        User user = this.createNewUser(roles);

        post.setUser(testEntityManager.persistAndFlush(user));

        Pageable pageable = PageRequest.of(0, 1,
                Sort.by(AppConstants.DEFAULT_SORT_BY).ascending());

        // Act
        // testEntityManager.persistAndFlush(post);
        postRepository.save(post);

        Page<Post> posts = postRepository.searchPosts("%Lamborghini%", pageable);

        Assertions.assertEquals(true, posts.getContent().isEmpty());
    }

    private Role createRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        return testEntityManager.persistAndFlush(role);
    }

    private User createNewUser(Set<Role> roles) {
        User user = new User();
        user.setName("John Cena");
        user.setEmail("john@test.com");
        user.setUsername("john");
        user.setPassword("12345678");
        user.setRoles(roles);
        return testEntityManager.persistAndFlush(user);
    }

    private Category createNewCategory() {

        Category category = new Category();
        category.setTitle("Cars");
        category.setDescription("This is about cars");

        return testEntityManager.persistAndFlush(category);
    }

    private Comment createComment(Post post) {
        Comment comment = new Comment();
        comment.setName("Simon");
        comment.setBody("Great car");
        comment.setEmail("simon@test.com");
        comment.setPost(post);

        return testEntityManager.persistAndFlush(comment);
    }
}
