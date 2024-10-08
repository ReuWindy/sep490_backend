package com.fpt.sep490.service;

import com.fpt.sep490.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(int id);
    Category getCategoryByName(String name);
    Category createCategory(Category category);
    Category updateCategory(Category category);
    public void disableCategoryAndReassignProduct(int id, long defaultCategoryId);
    Page<Category> getCategoriesByFilter(String name, int pageNumber, int pageSize);
}
