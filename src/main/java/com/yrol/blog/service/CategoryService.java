package com.yrol.blog.service;

import com.yrol.blog.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);

    List<CategoryDto> getAllCategories();

    CategoryDto findCategoryById(long id);

    CategoryDto updateCategory(CategoryDto categoryDto, long id);

    void deleteCategory(long id);
}
