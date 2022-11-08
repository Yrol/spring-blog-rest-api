package com.yrol.blog.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * DTO for login
 * */

@ApiModel(description = "Login DTO")
@Data
public class LoginDto {

    @ApiModelProperty(value = "Username or email")
    private String usernameOrEmail;

    @ApiModelProperty(value = "User password")
    private String password;
}
