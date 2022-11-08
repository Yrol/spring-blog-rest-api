package com.yrol.blog.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO for comments
 * */

@ApiModel(description = "Comments DTO")
@Data
public class CommentDto {

    @ApiModelProperty(value = "Comment ID")
    private Long id;

    @ApiModelProperty(value = "Comment name / title")
    @NotBlank
    @Size(min = 2, max = 50, message = "Name must be 2 to 50 characters.")
    private String name;

    @ApiModelProperty(value = "Commenter's email")
    @NotBlank
    @Email
    private String email;

    @ApiModelProperty(value = "Comment description")
    @NotBlank
    @Size(min = 2, max = 600, message = "Size must be 2 to 600 characters.")
    private String body;
}
