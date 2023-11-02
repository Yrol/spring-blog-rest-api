package com.yrol.blog.controller.webLayer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.yrol.blog.controller.CategoryController;
import com.yrol.blog.dto.CategoryDto;
import com.yrol.blog.entity.Category;
import com.yrol.blog.service.implementation.CategoryServiceImpl;

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryServiceImpl categoryService;

    Category category;

    @BeforeEach
    void setup() {
        category = new Category();
        category.setTitle("Sedan Cars");
        category.setDescription("This is about sedan cars");
    }

    @Test
    @DisplayName("Create Category successfully")
    void testCreateCategory_whenValidCategoryDetailsProvided_returnNewCategory() throws Exception {
        // Arrange
        CategoryDto categoryDto = new ModelMapper().map(category, CategoryDto.class);
        when(categoryService.createCategory(any(CategoryDto.class))).thenReturn(categoryDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(categoryDto));

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        CategoryDto createdCategory = new ObjectMapper().readValue(responseBodyAsString, CategoryDto.class);

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus(),
                "Incorrect HTTP Status Code returned.");
        Assertions.assertEquals(category.getTitle(), createdCategory.getTitle());
    }

    @Test
    @DisplayName("Attempting to create a category without mandatory fields")
    void testCreateCategory_whenMandatoryFieldsNotProvided_returnError() throws Exception {
        category.setTitle("");
        CategoryDto categoryDto = new ModelMapper().map(category, CategoryDto.class);
        when(categoryService.createCategory(any(CategoryDto.class))).thenReturn(categoryDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(categoryDto));

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus(),
                "Incorrect HTTP Status Code returned.");
    }

    @Test
    @DisplayName("Get category By ID")
    void testFetchCategory_whenValidCategoryIdProvided_returnCategory() throws Exception {
        // Arrange
        CategoryDto categoryDto = new ModelMapper().map(category, CategoryDto.class);
        when(categoryService.findCategoryById(anyLong())).thenReturn(categoryDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(String.format("/api/v1/categories/%s", 1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        CategoryDto createdCategory = new ObjectMapper().readValue(responseBodyAsString, CategoryDto.class);

        // Assert
        Assertions.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(),
                "Incorrect HTTP Status Code returned.");
        Assertions.assertEquals(category.getTitle(), createdCategory.getTitle());
    }

    @Test
    @DisplayName("Get all categories")
    void testFetchCategories_whenValidCategoryIdProvided_returnCategory() throws Exception {
        // Arrange
        List<CategoryDto> categoryList = new ArrayList<>();
        categoryList.add((new ModelMapper().map(category, CategoryDto.class)));

        when(categoryService.getAllCategories()).thenReturn(categoryList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        // Mapping the returned string to List<CategoryDto>
        List<CategoryDto> actual = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<CategoryDto>>() {
                });

        // Assert
        Assertions.assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(),
                "Incorrect HTTP Status Code returned.");
        Assertions.assertEquals(category.getTitle(), actual.get(0).getTitle(),
                "Title doesn't match");
    }

    @Test
    @DisplayName("Update category")
    void testUpdateCategory_whenValidCategoryDetailsProvided_returnUpdatedCategory() throws Exception {
        // Arrange
        CategoryDto categoryDto = new ModelMapper().map(category, CategoryDto.class);
        when(categoryService.updateCategory(any(CategoryDto.class), anyLong())).thenReturn(categoryDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/v1/categories/" + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(category));

        // Act
        MvcResult getUpdatedResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = getUpdatedResult.getResponse().getContentAsString();
        CategoryDto createdCategory = new ObjectMapper().readValue(responseBodyAsString, CategoryDto.class);

        // Assert
        Assertions.assertEquals(HttpStatus.OK.value(), getUpdatedResult.getResponse().getStatus(),
                "Incorrect HTTP Status Code returned.");
        Assertions.assertEquals(category.getTitle(), createdCategory.getTitle());

    }
}
