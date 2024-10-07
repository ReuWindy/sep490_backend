package com.fpt.sep490.service;

import com.fpt.sep490.model.Category;
import com.fpt.sep490.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(int id) {
        Optional<Category> category = categoryRepository.findById((long) id);
        return category.orElse(null);
    }

    @Override
    public Category getCategoryByName(String name) {
        Optional<Category> category = categoryRepository.findByName(name);
        return category.orElse(null);
    }

    @Override
    public Category createCategory(Category category) {
        Category newCategory = new Category();
        newCategory.setName(category.getName());
        newCategory.setDescription(category.getDescription());
        categoryRepository.save(newCategory);
        return newCategory;
    }

    @Override
    public Category updateCategory(Category category) {
        Category existingCategory = categoryRepository.findById(category.getId()).orElse(null);
        if (existingCategory != null) {
            existingCategory.setName(category.getName());
            existingCategory.setDescription(category.getDescription());
            categoryRepository.save(existingCategory);
            return existingCategory;
        } else {
            return null;
        }
    }

    @Override
    public Category deleteCategory(int id) {
        return null;
    }

    @Override
    public Page<Category> getCategoriesByFilter(String name, int pageNumber, int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
            Specification<Category> specification = CategorySpecification.hasName(name);
            return categoryRepository.findAll(specification, pageable);
        }
        catch (Exception e) {
            return null;
        }
    }
}