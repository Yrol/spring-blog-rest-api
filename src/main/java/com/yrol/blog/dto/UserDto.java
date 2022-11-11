package com.yrol.blog.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserDto {

    @ApiModelProperty(value = "User's actual name")
    private String name;

    @ApiModelProperty(value = "Username")
    private String username;

    @ApiModelProperty(value = "Email")
    private String email;
}
