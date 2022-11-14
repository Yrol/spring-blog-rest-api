package com.yrol.blog.controller;


import com.yrol.blog.dto.CategoryDto;
import com.yrol.blog.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


/**
 * Rest controller for Categories CRUD operations
 * Swagger Annotations - @Api, @ApiOperation
 * */

@Api(value = "REST API for Category CRUD operations")
@RestController
@RequestMapping("api/v1/categories")
public class CategoryController {

    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @ApiOperation(value = "REST API for Category creation")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryDto> createPost(@RequestBody @Valid CategoryDto categoryDto) {
        return new ResponseEntity<>(categoryService.createCategory(categoryDto), HttpStatus.CREATED);
    }
}
