package com.yrol.blog.controller;

import com.yrol.blog.dto.CategoryDto;
import com.yrol.blog.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Rest controller for Categories CRUD operations
 * Swagger Annotations - @Api, @ApiOperation
 */

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
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return new ResponseEntity<>(categoryService.createCategory(categoryDto), HttpStatus.CREATED);
    }

    @ApiOperation(value = "REST API to get all categories")
    @GetMapping
    public List<CategoryDto> getCategories() {
        return categoryService.getAllCategories();
    }

    @ApiOperation(value = "REST API to get category by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable(name = "id") long id) {
        return new ResponseEntity<>(categoryService.findCategoryById(id), HttpStatus.OK);
    }

    @ApiOperation(value = "REST API for updating categories by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@RequestBody @Valid CategoryDto categoryDto,
            @PathVariable(name = "id") long id) {
        return new ResponseEntity<>(categoryService.updateCategory(categoryDto, id), HttpStatus.OK);
    }

    @ApiOperation(value = "REST API for deleting categories by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable(name = "id") long id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>("Category delete successfully", HttpStatus.NO_CONTENT);
    }

}
