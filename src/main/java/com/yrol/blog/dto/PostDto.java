package com.yrol.blog.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class PostDto {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 100, message = "Title must be between 2 to 100 characters.")
    private String title;

    @NotBlank
    @Size(min = 2, message = "Description must contain at least 2 characters.")
    private String description;

    @NotBlank
    @Size(min = 2, message = "Content must contain at least 2 characters.")
    private String content;

    private Set<CommentDto> comments;
}
