package com.yrol.blog.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@ApiModel(description = "Sign UP DTO")
@Data
public class SignUpDto {

    @NotBlank
    @Size(min = 2, message = "Name must contain at least 2 characters.")
    private String name;

    @NotBlank
    @Size(min = 2, message = "Username must contain at least 2 characters.")
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 2, message = "Password must contain at least 2 characters.")
    private String password;
}
