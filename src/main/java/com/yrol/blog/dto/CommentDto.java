package com.yrol.blog.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommentDto {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 50, message = "Name must be 2 to 50 characters.")
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 2, max = 600, message = "Size must be 2 to 600 characters.")
    private String body;
}
