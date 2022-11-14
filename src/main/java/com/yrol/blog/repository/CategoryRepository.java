package com.yrol.blog.repository;

import com.yrol.blog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    public List<Category> findByTitleIgnoreCase(String title);
}
