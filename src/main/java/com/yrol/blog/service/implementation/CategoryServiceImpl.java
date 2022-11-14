package com.yrol.blog.service.implementation;

import com.yrol.blog.dto.CategoryDto;
import com.yrol.blog.entity.Category;
import com.yrol.blog.exception.BlogAPIException;
import com.yrol.blog.repository.CategoryRepository;
import com.yrol.blog.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;

    private ModelMapper mapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        String newCategoryTitle = (categoryDto.getTitle()).trim();

        if(categoryRepository.findByTitleIgnoreCase(newCategoryTitle).size() > 0) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, String.format("Category %s already exists.", newCategoryTitle));
        }

        Category category = categoryRepository.save(this.mapToEntity(categoryDto));

        return this.mapToDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return null;
    }

    @Override
    public CategoryDto findCategoryById(long id) {
        return null;
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, long id) {
        return null;
    }

    @Override
    public void deleteCategory(long id) {

    }

    private CategoryDto mapToDto(Category category) {
        CategoryDto categoryDto = mapper.map(category, CategoryDto.class);
        return categoryDto;
    }

    private Category mapToEntity(CategoryDto categoryDto) {
        Category category = mapper.map(categoryDto, Category.class);
        return category;
    }
}
