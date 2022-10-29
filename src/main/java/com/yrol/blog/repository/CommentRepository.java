package com.yrol.blog.repository;

import com.yrol.blog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Find comment by post ID using automatic / derived query method
     * */
    List<Comment> findByPostId(long postId);
}
