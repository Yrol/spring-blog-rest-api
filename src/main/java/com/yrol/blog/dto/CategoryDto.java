package com.yrol.blog.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO for categories
 * */

@ApiModel(description = "Category DTO")
@Data
public class CategoryDto {
    @ApiModelProperty(value = "Category ID")
    private Long id;

    @ApiModelProperty(value = "Category title")
    @NotBlank
    @Size(min = 2, max = 100, message = "Title must be between 2 to 100 characters.")
    private String title;

    @ApiModelProperty(value = "Category description")
    @Size(min = 2, max = 500, message = "Title must be between 2 to 500 characters.")
    private String description;

}
