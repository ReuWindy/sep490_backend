package com.fpt.sep490.service;

import com.fpt.sep490.dto.TopCategoryResponseDTO;
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
    List<String> getAllCategoryNames();
    Page<Category> getCategoriesByFilter(String name,Boolean status, int pageNumber, int pageSize);
    Category enableCategory(Long id);
    Category disableCategory(Long id);
    TopCategoryResponseDTO getTopCategoriesWithTotalAmount(int limit);
}
