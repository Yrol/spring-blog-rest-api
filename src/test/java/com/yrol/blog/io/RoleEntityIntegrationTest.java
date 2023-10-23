package com.yrol.blog.io;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import com.yrol.blog.entity.Role;
import com.yrol.blog.repository.RoleRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleEntityIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private RoleRepository roleRepository;

    Role role;

    String roleName;

    @BeforeEach
    void setUp() {
        roleName = "ROLE_ADMIN";
        role = new Role();
        role.setName(roleName);
    }

    @Test
    void testRoleEntity_whenValidRoleDetailsProvided_shouldReturnStoredRoleDetails() {

        // Arrange

        // Act
        Role storedRoleEntity = testEntityManager.persistAndFlush(role);

        // Assert
        Assertions.assertEquals(storedRoleEntity.getId() == role.getId(), true, String.format("User ID should be ", 1));
    }

    @Test
    void testFindByName_whenGivenCorrectRoleName_returnRoles() {

        // Arrange

        // Act
        testEntityManager.persistAndFlush(role);
        Optional<Role> fetchedRole = roleRepository.findByName(roleName);

        // Assert
        Assertions.assertTrue(fetchedRole.isPresent(), String.format("Role: %s should exist", roleName));
        Assertions.assertEquals(fetchedRole.get().getName(), roleName,
                String.format("Role name should match", roleName));
    }

    @Test
    void testRoleEntity_whenFirstNameIsTooLong_shouldThrowException() {

        // Arrange
        role.setName(
                "Loremipsumdolorsitamet,consectetueradipiscingelit.Aeneancommodoligulaegetdolor.Aeneanmassa.Cumsociisnatoquepenatibusetmagnisdisparturientmontes");

        // Act & Assert
        Assertions.assertThrows(PersistenceException.class, () -> {
            testEntityManager.persistAndFlush(role);
        }, "PersistenceException is expected to be thrown");
    }
}
