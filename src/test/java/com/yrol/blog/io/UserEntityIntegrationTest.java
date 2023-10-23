package com.yrol.blog.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.junit.jupiter.api.Assertions;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.persistence.PersistenceException;

import com.yrol.blog.entity.Role;
import com.yrol.blog.entity.User;
import com.yrol.blog.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserEntityIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    UserRepository userRepository;

    User user;

    Role role;

    String userEmail;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName("ROLE_ADMIN");

        Set<Role> roles = new HashSet<Role>();
        roles.add(role);

        userEmail = "john@test.com";

        user = new User();
        user.setName("John Cena");
        user.setEmail(userEmail);
        user.setUsername("john");
        user.setPassword("12345678");
        user.setRoles(roles);
    }

    @Test
    void testUserEntity_whenValidUserDetailsProvided_shouldReturnStoredUserDetails() {

        // Arrange

        // Act
        User storedUser = testEntityManager.persistAndFlush(user);

        // Assert
        Assertions.assertEquals(storedUser.getName() == user.getName(), true,
                String.format("Name should match:%s", user.getName()));
        Assertions.assertEquals(storedUser.getRoles().size(), 1, "User should have at least one role assigned");
        Assertions.assertEquals(storedUser.getPosts().size(), 0, "User should not have any posts");
    }

    @Test
    void testUserEntity_whenUserAlreadyExistByEmail_shouldThrowException() {

        // Arrange
        User anotherUser = new User();
        anotherUser.setName("James Dyson");
        anotherUser.setEmail(userEmail);
        anotherUser.setUsername("james");
        anotherUser.setPassword("12345678");

        // Act
        testEntityManager.persistAndFlush(user);

        // Assert & Act
        Assertions.assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(anotherUser);
        }, "PersistenceException is expected to be thrown");
    }

    @Test
    void testFindByEmail_whenGivenCorrectEmail_returnUsers() {

        // Arrange

        // Act
        testEntityManager.persistAndFlush(user);
        Optional<User> users = userRepository.findByEmail(userEmail);

        // Assert
        Assertions.assertEquals(users.get().getEmail(), userEmail);
    }

    @Test
    void testFindByEmailOrUsername_whenGivenCorrectEmailOrUsername_returnsUser() {

        // Arrange

        // Act
        testEntityManager.persistAndFlush(user);
        Optional<User> storedUser = userRepository.findByUsernameOrEmail("James", userEmail);

        // Assert
        Assertions.assertEquals(storedUser.isPresent(), true, "User should exist");
        Assertions.assertEquals(storedUser.get().getEmail(), user.getEmail(),
                String.format("The email should match:%s", user.getEmail()));

    }
}
