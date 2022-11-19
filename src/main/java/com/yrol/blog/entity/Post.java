package com.yrol.blog.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

// Disabling @Data and use @Getter and @Setter instead since the toString method in it causing an infinite loop when try retrieve child objects (comments)
// @Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts", uniqueConstraints = {@UniqueConstraint(columnNames = {"title"})})
public class Post extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String content;

    /**
     * Specifying the bidirectional relationship OneToMany where a Post can have many comments.
     * mappedBy - "post" defined in Comment entity.
     * Using Set which doesn't allow duplicates unlike List.
     * orphanRemoval - remove child when parent is removed.
     * */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    /**
     * Relationship for user who created the post
     * */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Relationship for the post category
     * */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
