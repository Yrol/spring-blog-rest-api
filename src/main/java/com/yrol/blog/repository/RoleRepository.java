package com.yrol.blog.repository;

import com.yrol.blog.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Using automatic / derived queries
     * */
    Optional<Role> findByName(String name);
}
