package com.yrol.blog.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO for login
 * */

@ApiModel(description = "Login DTO")
@Data
public class LoginDto {

    @ApiModelProperty(value = "Username or email")
    @NotBlank
    private String usernameOrEmail;

    @ApiModelProperty(value = "User password")
    @NotBlank
    @Size(min = 2, message = "Password must contain at least 2 characters.")
    private String password;
}
