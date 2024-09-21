package com.fpt.sep490.service;

import com.fpt.sep490.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();
    Category getCategoryById(int id);
    Category getCategoryByName(String name);
    Category createCategory(Category category);
    Category updateCategory(Category category);
    Category deleteCategory(int id);
}
