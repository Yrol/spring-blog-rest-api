package com.yrol.blog.repository;

import com.yrol.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // using JPQL query (instead of native ) to search products
    @Query(value = "SELECT * FROM posts p WHERE p.title iLIKE(:query) " +
            "OR p.description iLIKE(:query)", nativeQuery = true)
    Page<Post> searchPosts(String query, Pageable pageable);
}
