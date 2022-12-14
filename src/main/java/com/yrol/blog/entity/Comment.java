package com.yrol.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {

    private String name;
    private String email;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    /**
     * ManyToOne - A post can have many comments
     * FetchType.LAZY - only fetch related entities from DB when the relationship is being used.
     * "post_id" - the foreign key in comments table
     * */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
