package com.yrol.blog.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * DTO for posts
 * */

@ApiModel(description = "Post DTO")
@Data
public class PostDto {

    @ApiModelProperty(value = "Blog post ID")
    private Long id;

    @ApiModelProperty(value = "Blog post title")
    @NotBlank
    @Size(min = 2, max = 100, message = "Title must be between 2 to 100 characters.")
    private String title;

    @ApiModelProperty(value = "Blog post description")
    @NotBlank
    @Size(min = 2, max = 500, message = "Description must be between 2 to 500 characters.")
    private String description;

    @ApiModelProperty(value = "Blog post content")
    @NotBlank
    @Size(min = 2, message = "Content must contain at least 2 characters.")
    private String content;

    @ApiModelProperty(value = "Blog post comments")
    private Set<CommentDto> comments;

    @ApiModelProperty(value = "Blog post category")
    private CategoryDto category;

    @ApiModelProperty(value = "Blog post creator")
    private UserDto user;
}
